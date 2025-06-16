package com.votaciones.seguridad.service;

import com.votaciones.seguridad.model.CiudadanoInfo;
import com.votaciones.seguridad.db.SecurityDatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ControladorDatos {
    private static final Logger logger = LoggerFactory.getLogger(ControladorDatos.class);
    
    private final DataSource dataSource;
    private final Map<String, CiudadanoInfo> cacheCiudadanos;
    private final Map<String, String> sospechosos;
    
    public ControladorDatos() {
        this.dataSource = SecurityDatabaseConnection.getDataSource();
        this.cacheCiudadanos = new ConcurrentHashMap<>();
        this.sospechosos = new ConcurrentHashMap<>();
        logger.info("ControladorDatos inicializado");
    }
    
    public CiudadanoInfo getCiudadano(String documento) {
        try {
            // Primero verificar en cache
            CiudadanoInfo enCache = cacheCiudadanos.get(documento);
            if (enCache != null) {
                logger.debug("Ciudadano {} encontrado en cache", documento);
                return enCache;
            }
            
            // Si no está en cache, consultar base de datos
            String sql = "SELECT c.documento, c.nombres, c.apellidos, c.ciudad_id, c.zona_id, c.mesa_id, " +
                        "EXISTS(SELECT 1 FROM votos v WHERE v.ciudadano_id = c.id) as ya_voto " +
                        "FROM ciudadanos c WHERE c.documento = ?";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, documento);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        CiudadanoInfo ciudadano = new CiudadanoInfo(
                            rs.getString("documento"),
                            rs.getString("nombres"),
                            rs.getString("apellidos"),
                            rs.getInt("ciudad_id"),
                            rs.getInt("zona_id"),
                            rs.getInt("mesa_id")
                        );
                        ciudadano.setYaVoto(rs.getBoolean("ya_voto"));
                        ciudadano.setEsSospechoso(sospechosos.containsKey(documento));
                        
                        // Guardar en cache
                        cacheCiudadanos.put(documento, ciudadano);
                        
                        logger.debug("Ciudadano {} cargado desde BD: {}", documento, ciudadano);
                        return ciudadano;
                    }
                }
            }
            
            logger.warn("Ciudadano no encontrado: {}", documento);
            return null;
            
        } catch (SQLException e) {
            logger.error("Error consultando ciudadano: " + documento, e);
            return null;
        }
    }
    
    public boolean verificarSiYaVoto(String documento) {
        try {
            String sql = "SELECT COUNT(*) as count FROM votos v " +
                        "JOIN ciudadanos c ON v.ciudadano_id = c.id WHERE c.documento = ?";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, documento);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        boolean yaVoto = rs.getInt("count") > 0;
                        logger.debug("Verificación de voto para {}: {}", documento, yaVoto);
                        
                        // Actualizar cache si existe
                        CiudadanoInfo enCache = cacheCiudadanos.get(documento);
                        if (enCache != null) {
                            enCache.setYaVoto(yaVoto);
                        }
                        
                        return yaVoto;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error verificando estado de voto para: " + documento, e);
        }
        
        return false;
    }
    
    public void agregarSospechoso(String documento, String motivo) {
        try {
            sospechosos.put(documento, motivo);
            
            // Registrar en base de datos
            String sql = "INSERT INTO sospechosos (documento, motivo, fecha_registro, estado) " +
                        "VALUES (?, ?, ?, 'ACTIVO') " +
                        "ON CONFLICT (documento) DO UPDATE SET motivo = ?, fecha_registro = ?, estado = 'ACTIVO'";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                Timestamp ahora = Timestamp.valueOf(LocalDateTime.now());
                stmt.setString(1, documento);
                stmt.setString(2, motivo);
                stmt.setTimestamp(3, ahora);
                stmt.setString(4, motivo);
                stmt.setTimestamp(5, ahora);
                
                stmt.executeUpdate();
                
                // Actualizar cache si existe
                CiudadanoInfo enCache = cacheCiudadanos.get(documento);
                if (enCache != null) {
                    enCache.setEsSospechoso(true);
                }
                
                logger.info("Ciudadano {} agregado como sospechoso por: {}", documento, motivo);
                
            }
        } catch (SQLException e) {
            logger.error("Error agregando sospechoso: " + documento, e);
        }
    }
    
    public boolean esSospechoso(String documento) {
        boolean enMemoria = sospechosos.containsKey(documento);
        if (enMemoria) {
            return true;
        }
        
        try {
            String sql = "SELECT COUNT(*) as count FROM sospechosos WHERE documento = ? AND estado = 'ACTIVO'";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, documento);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        boolean esSospechoso = rs.getInt("count") > 0;
                        if (esSospechoso) {
                            sospechosos.put(documento, "CARGADO_BD");
                        }
                        return esSospechoso;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error verificando si es sospechoso: " + documento, e);
        }
        
        return false;
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
} 