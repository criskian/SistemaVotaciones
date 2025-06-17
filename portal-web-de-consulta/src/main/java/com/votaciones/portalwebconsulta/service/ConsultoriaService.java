package com.votaciones.portalwebconsulta.service;

import VotingSystem.QueryStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsultoriaService {
    private static final Logger logger = LoggerFactory.getLogger(ConsultoriaService.class);
    private final QueryStation queryStation;

    public ConsultoriaService(QueryStation queryStation) {
        this.queryStation = queryStation;
    }

    public void inicializar() {
        // TODO: Inicializar conexión con el servicio de consultoría
        logger.info("Inicializando servicio de consultoría");
    }

    public String consultarMesa(String cedula) {
        try {
            // Obtener la información de la mesa de votación
            String resultado = queryStation.consultVotingStation(cedula, null);
            logger.info("Consultando información de mesa para cédula: {}", cedula);
            return "Mesa de Votación: " + resultado;
        } catch (Exception e) {
            logger.error("Error al consultar información de mesa", e);
            return "Error al consultar información de mesa: " + e.getMessage();
        }
    }

    public String consultarZonaVotacion(String cedula) {
        try {
            String resultado = queryStation.consultZone(cedula, null);
            logger.info("Consultando zona y colegio para cédula: {}", cedula);
            return resultado;
        } catch (Exception e) {
            logger.error("Error al consultar zona y colegio", e);
            return "Error al consultar zona y colegio: " + e.getMessage();
        }
    }

    public String consultarCandidatos() {
        try {
            String resultado = queryStation.consultCandidates(null);
            logger.info("Consultando lista de candidatos");
            return resultado;
        } catch (Exception e) {
            logger.error("Error al consultar candidatos", e);
            return "Error al consultar candidatos: " + e.getMessage();
        }
    }

    public String verConteoVotos() {
        try {
            String resultado = queryStation.consultVoteCount(null);
            logger.info("Consultando conteo de votos");
            return resultado;
        } catch (Exception e) {
            logger.error("Error al consultar conteo de votos", e);
            return "Error al consultar conteo de votos: " + e.getMessage();
        }
    }

    public void cerrar() {
        // TODO: Cerrar conexión con el servicio de consultoría
        logger.info("Cerrando servicio de consultoría");
    }
}