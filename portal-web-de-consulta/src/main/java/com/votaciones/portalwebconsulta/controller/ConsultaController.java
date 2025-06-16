package com.votaciones.portalwebconsulta.controller;

import com.votaciones.portalwebconsulta.service.AccesoDatosService;
import com.votaciones.portalwebconsulta.service.SeguridadService;
import com.votaciones.portalwebconsulta.service.ConsultoriaService;
import com.votaciones.portalwebconsulta.service.ProxyCacheAdapter;
import com.votaciones.portalwebconsulta.PortalWebConsultaI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsultaController {
    private static final Logger logger = LoggerFactory.getLogger(ConsultaController.class);
    private final AccesoDatosService accesoDatos;
    private final SeguridadService seguridad;
    private final ConsultoriaService consultoria;

    public ConsultaController() {
        this.accesoDatos = new AccesoDatosService();
        this.seguridad = new SeguridadService();

        // Crear instancia de PortalWebConsultaI y adaptarla a QueryStation
        PortalWebConsultaI proxy = new PortalWebConsultaI();
        ProxyCacheAdapter adapter = new ProxyCacheAdapter(proxy);
        this.consultoria = new ConsultoriaService(adapter);

        // Inicializar servicios
        try {
            this.accesoDatos.inicializar();
            this.seguridad.inicializar();
            this.consultoria.inicializar();
        } catch (Exception e) {
            logger.error("Error al inicializar servicios", e);
            throw new RuntimeException("Error al inicializar servicios", e);
        }
    }

    public String consultarMesaVotacion(String cedula) {
        try {
            if (!seguridad.validarFormato(cedula)) {
                return "Formato de cédula inválido";
            }
            return consultoria.consultarMesa(cedula);
        } catch (Exception e) {
            logger.error("Error al consultar mesa de votación", e);
            return "Error al consultar mesa de votación: " + e.getMessage();
        }
    }

    public String consultarZona(String cedula) {
        try {
            if (!seguridad.validarFormato(cedula)) {
                return "Formato de cédula inválido";
            }
            return consultoria.consultarZonaVotacion(cedula);
        } catch (Exception e) {
            logger.error("Error al consultar zona", e);
            return "Error al consultar zona: " + e.getMessage();
        }
    }

    public String consultarCandidatos() {
        try {
            return consultoria.consultarCandidatos();
        } catch (Exception e) {
            logger.error("Error al consultar candidatos", e);
            return "Error al consultar candidatos: " + e.getMessage();
        }
    }

    public String consultarVotos() {
        try {
            return consultoria.verConteoVotos();
        } catch (Exception e) {
            logger.error("Error al consultar votos", e);
            return "Error al consultar votos: " + e.getMessage();
        }
    }

    public void cerrar() {
        try {
            accesoDatos.cerrar();
            seguridad.cerrar();
            consultoria.cerrar();
        } catch (Exception e) {
            logger.error("Error al cerrar servicios", e);
        }
    }
}