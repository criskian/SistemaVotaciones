package com.votaciones.seguridad.service;

import VotingSystem.ProxyCacheDBCiudadPrx;
import VotingSystem.Votante;
import com.votaciones.seguridad.model.CiudadanoInfo;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ControladorDatos {
    private static final Logger logger = LoggerFactory.getLogger(ControladorDatos.class);

    private final Map<String, CiudadanoInfo> cacheCiudadanos;
    private final Map<String, String> sospechosos;
    private final ProxyCacheDBCiudadPrx proxyCache;
    private final Communicator communicator;

    public ControladorDatos() {
        this.cacheCiudadanos = new ConcurrentHashMap<>();
        this.sospechosos = new ConcurrentHashMap<>();
        // Configura la conexión ICE al proxy-cache-db-ciudad
        this.communicator = Util.initialize();
        String proxyStr = "ProxyCacheDBCiudad:tcp -h 127.0.0.1 -p 10000"; // Ajusta host/puerto si es necesario
        ObjectPrx base = communicator.stringToProxy(proxyStr);
        this.proxyCache = ProxyCacheDBCiudadPrx.checkedCast(base);
        if (this.proxyCache == null) {
            throw new RuntimeException("No se pudo conectar al ProxyCacheDBCiudad en " + proxyStr);
        }
        logger.info("ControladorDatos inicializado y conectado a ProxyCacheDBCiudad");
    }

    public CiudadanoInfo getCiudadano(String documento) {
        // Primero verificar en cache
        CiudadanoInfo enCache = cacheCiudadanos.get(documento);
        if (enCache != null) {
            logger.debug("Ciudadano {} encontrado en cache", documento);
            return enCache;
        }
        try {
            Votante votante = proxyCache.ConsultarVotantePorCedula(documento);
            if (votante == null || votante.documento == null || votante.documento.isEmpty()) {
                logger.warn("Ciudadano no encontrado: {}", documento);
                return null;
            }
            CiudadanoInfo ciudadano = new CiudadanoInfo(
                votante.documento,
                votante.nombres,
                votante.apellidos,
                votante.ciudadId,
                votante.zonaId,
                votante.mesaId
            );
            // No hay campo yaVoto ni esSospechoso en Votante, se pueden consultar aparte si es necesario
            ciudadano.setYaVoto(verificarSiYaVoto(documento));
            ciudadano.setEsSospechoso(esSospechoso(documento));
            cacheCiudadanos.put(documento, ciudadano);
            logger.debug("Ciudadano {} cargado desde ProxyCacheDBCiudad: {}", documento, ciudadano);
            return ciudadano;
        } catch (Exception e) {
            logger.error("Error consultando ciudadano en ProxyCacheDBCiudad: " + documento, e);
            return null;
        }
    }

    public boolean verificarSiYaVoto(String documento) {
        try {
            return proxyCache.YaVoto(documento);
        } catch (Exception e) {
            logger.error("Error verificando estado de voto para: " + documento, e);
            return false;
        }
    }

    public void agregarSospechoso(String documento, String motivo) {
        try {
            sospechosos.put(documento, motivo);
            // Llamar al método remoto para agregar sospechoso
            boolean ok = proxyCache.AgregarSospechoso(documento, motivo);
            if (ok) {
                CiudadanoInfo enCache = cacheCiudadanos.get(documento);
                if (enCache != null) {
                    enCache.setEsSospechoso(true);
                }
                logger.info("Ciudadano {} agregado como sospechoso por: {} (ProxyCacheDBCiudad)", documento, motivo);
            } else {
                logger.warn("No se pudo agregar sospechoso en ProxyCacheDBCiudad para: {}", documento);
            }
        } catch (Exception e) {
            logger.error("Error agregando sospechoso en ProxyCacheDBCiudad: " + documento, e);
        }
    }

    public boolean esSospechoso(String documento) {
        try {
            return proxyCache.EsSospechoso(documento);
        } catch (Exception e) {
            logger.error("Error verificando si es sospechoso en ProxyCacheDBCiudad: " + documento, e);
            return false;
        }
    }

    public void limpiarCache() {
        cacheCiudadanos.clear();
        logger.info("Cache de ciudadanos limpiado");
    }

    public int getTamanioCache() {
        return cacheCiudadanos.size();
    }

    public int getCantidadSospechosos() {
        return sospechosos.size();
    }

    /**
     * Verifica si un ciudadano está asignado a una zona específica
     */
    public boolean verificarAsignacionZona(String documento, int zonaId) {
        try {
            // Usar conexión directa a la base de datos para verificar asignación a zona
            String sql = "SELECT COUNT(*) FROM asignaciones_ciudadanos ac " +
                        "JOIN ciudadanos c ON ac.ciudadano_id = c.id " +
                        "WHERE c.documento = ? AND ac.zona_id = ? AND ac.activo = true";
            
            try (var connection = com.votaciones.seguridad.db.SecurityDatabaseConnection.getDataSource().getConnection();
                 var stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, documento);
                stmt.setInt(2, zonaId);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    boolean asignado = rs.getInt(1) > 0;
                    logger.debug("Verificación de asignación a zona: {} en zona {} = {}", documento, zonaId, asignado);
                    return asignado;
                }
            }
        } catch (Exception e) {
            logger.error("Error verificando asignación a zona para: " + documento, e);
        }
        return false;
    }
    
    /**
     * Verifica si un ciudadano ya votó en una zona específica
     */
    public boolean verificarSiYaVotoEnZona(String documento, int zonaId) {
        try {
            // Usar conexión directa a la base de datos para verificar votos en zona
            String sql = "SELECT COUNT(*) FROM votos v " +
                        "JOIN ciudadanos c ON v.ciudadano_id = c.id " +
                        "JOIN mesas_votacion m ON v.mesa_id = m.id " +
                        "JOIN colegios col ON m.colegio_id = col.id " +
                        "WHERE c.documento = ? AND col.zona_id = ?";
            
            try (var connection = com.votaciones.seguridad.db.SecurityDatabaseConnection.getDataSource().getConnection();
                 var stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, documento);
                stmt.setInt(2, zonaId);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    boolean yaVoto = rs.getInt(1) > 0;
                    logger.debug("Verificación de voto en zona: {} en zona {} = {}", documento, zonaId, yaVoto);
                    return yaVoto;
                }
            }
        } catch (Exception e) {
            logger.error("Error verificando voto en zona para: " + documento, e);
        }
        return false;
    }
} 