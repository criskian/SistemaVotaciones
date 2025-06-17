package com.votaciones.portalwebconsulta;

import VotingSystem.ProxyCacheDBCiudad;
import VotingSystem.ProxyCacheDBCiudadPrx;
import VotingSystem.Votante;
import VotingSystem.Candidato;
import VotingSystem.Zona;
import VotingSystem.Voto;
import VotingSystem.LogEntry;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PortalWebConsultaI implements ProxyCacheDBCiudad {
    private static final Logger logger = LoggerFactory.getLogger(PortalWebConsultaI.class);
    private final ProxyCacheDBCiudadPrx proxy;

    public PortalWebConsultaI() {
        try {
            // Inicializar el comunicador Ice con las propiedades del archivo
            String[] args = new String[]{"--Ice.Config=portal_web_consulta.properties"};
            Communicator communicator = Util.initialize(args);
            
            // Obtener el proxy del servidor proxy-cache-db-ciudad usando la configuración
            String proxyStr = communicator.getProperties().getProperty("ProxyCacheDBCiudad.Proxy");
            if (proxyStr == null || proxyStr.isEmpty()) {
                throw new Error("Proxy configuration not found");
            }
            
            ObjectPrx base = communicator.stringToProxy(proxyStr);
            proxy = ProxyCacheDBCiudadPrx.checkedCast(base);
            
            if (proxy == null) {
                throw new Error("Invalid proxy");
            }
            logger.info("Conexión exitosa con el proxy en el puerto 10000");
        } catch (Exception e) {
            logger.error("Error al inicializar el proxy", e);
            throw new RuntimeException("Error al inicializar el proxy", e);
        }
    }

    @Override
    public Votante ConsultarVotantePorCedula(String cedula, Current current) {
        try {
            logger.info("Consultando votante por cédula: {}", cedula);
            return proxy.ConsultarVotantePorCedula(cedula, null);
        } catch (Exception e) {
            logger.error("Error al consultar el votante", e);
            throw new RuntimeException("Error al consultar el votante", e);
        }
    }

    @Override
    public Candidato[] ConsultarCandidatos(Current current) {
        try {
            logger.info("Consultando lista de candidatos");
            return proxy.ConsultarCandidatos(null);
        } catch (Exception e) {
            logger.error("Error al consultar los candidatos", e);
            throw new RuntimeException("Error al consultar los candidatos", e);
        }
    }

    @Override
    public Zona[] GetZonasVotacion(Current current) {
        try {
            logger.info("Consultando zonas de votación");
            return proxy.GetZonasVotacion(null);
        } catch (Exception e) {
            logger.error("Error al consultar las zonas de votación", e);
            throw new RuntimeException("Error al consultar las zonas de votación", e);
        }
    }

    @Override
    public Zona ZonaMesaAsignada(String cedula, Current current) {
        try {
            logger.info("Consultando zona y mesa asignada para cédula: {}", cedula);
            return proxy.ZonaMesaAsignada(cedula, null);
        } catch (Exception e) {
            logger.error("Error al consultar la zona y mesa asignada", e);
            throw new RuntimeException("Error al consultar la zona y mesa asignada", e);
        }
    }

    @Override
    public int IDZonaVotacion(String cedula, Current current) {
        try {
            logger.info("Consultando ID de zona de votación para cédula: {}", cedula);
            return proxy.IDZonaVotacion(cedula, null);
        } catch (Exception e) {
            logger.error("Error al consultar el ID de zona de votación", e);
            throw new RuntimeException("Error al consultar el ID de zona de votación", e);
        }
    }

    @Override
    public int GetConteoVotos(int mesaId, Current current) {
        try {
            logger.info("Consultando conteo de votos para mesa: {}", mesaId);
            return proxy.GetConteoVotos(mesaId, null);
        } catch (Exception e) {
            logger.error("Error al consultar el conteo de votos", e);
            throw new RuntimeException("Error al consultar el conteo de votos", e);
        }
    }

    @Override
    public boolean AgregarVoto(Voto voto, Current current) {
        try {
            logger.info("Agregando voto");
            return proxy.AgregarVoto(voto, null);
        } catch (Exception e) {
            logger.error("Error al agregar el voto", e);
            throw new RuntimeException("Error al agregar el voto", e);
        }
    }

    @Override
    public boolean AgregarSospechoso(String cedula, String motivo, Current current) {
        try {
            logger.info("Agregando sospechoso: {}", cedula);
            return proxy.AgregarSospechoso(cedula, motivo, null);
        } catch (Exception e) {
            logger.error("Error al agregar el sospechoso", e);
            throw new RuntimeException("Error al agregar el sospechoso", e);
        }
    }

    @Override
    public boolean RegistrarLogs(LogEntry log, Current current) {
        try {
            logger.info("Registrando log");
            return proxy.RegistrarLogs(log, null);
        } catch (Exception e) {
            logger.error("Error al registrar el log", e);
            throw new RuntimeException("Error al registrar el log", e);
        }
    }

    @Override
    public int GetConteoVotosPorCandidato(int candidatoId, Current current) {
        try {
            return proxy.GetConteoVotosPorCandidato(candidatoId, null);
        } catch (Exception e) {
            logger.error("Error al consultar el conteo de votos por candidato", e);
            throw new RuntimeException("Error al consultar el conteo de votos por candidato", e);
        }
    }

    @Override
    public String ConsultarMesaDescriptiva(String cedula, com.zeroc.Ice.Current current) {
        try {
            return proxy.ConsultarMesaDescriptiva(cedula);
        } catch (Exception e) {
            logger.error("Error al consultar la mesa descriptiva", e);
            return "Error al consultar la mesa descriptiva: " + e.getMessage();
        }
    }

    @Override
    public boolean YaVoto(String cedula, Current current) {
        try {
            logger.info("Verificando si ya votó: {}", cedula);
            return proxy.YaVoto(cedula);
        } catch (Exception e) {
            logger.error("Error al verificar si ya votó", e);
            throw new RuntimeException("Error al verificar si ya votó", e);
        }
    }

    @Override
    public boolean EsSospechoso(String cedula, Current current) {
        try {
            logger.info("Verificando si es sospechoso: {}", cedula);
            return proxy.EsSospechoso(cedula);
        } catch (Exception e) {
            logger.error("Error al verificar si es sospechoso", e);
            throw new RuntimeException("Error al verificar si es sospechoso", e);
        }
    }

    @Override
    public boolean AgregarLoteVotos(VotingSystem.Voto[] lote, com.zeroc.Ice.Current current) {
        try {
            logger.info("Agregando lote de votos ({} votos)", lote != null ? lote.length : 0);
            return proxy.AgregarLoteVotos(lote, null);
        } catch (Exception e) {
            logger.error("Error al agregar lote de votos", e);
            return false;
        }
    }
}