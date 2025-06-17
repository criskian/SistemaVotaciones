package com.votaciones.seguridad;

import SecurityModule.SecurityService;
import com.votaciones.seguridad.service.*;
import com.votaciones.seguridad.model.AlertaSeguridad;
import com.votaciones.seguridad.model.CiudadanoInfo;
import com.zeroc.Ice.Current;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityServiceI implements SecurityService {
    private static final Logger logger = LoggerFactory.getLogger(SecurityServiceI.class);
    
    private final ControladorDatos controladorDatos;
    private final ProxyCacheFiscalia cacheFiscalia;
    private final ConexionSistemaFiscal conexionFiscalia;
    private final SistemaAlertas sistemaAlertas;
    
    public SecurityServiceI(ControladorDatos controladorDatos, 
                           ProxyCacheFiscalia cacheFiscalia,
                           ConexionSistemaFiscal conexionFiscalia,
                           SistemaAlertas sistemaAlertas) {
        this.controladorDatos = controladorDatos;
        this.cacheFiscalia = cacheFiscalia;
        this.conexionFiscalia = conexionFiscalia;
        this.sistemaAlertas = sistemaAlertas;
    }
    
    @Override
    public boolean validateSecurity(String document, Current current) {
        try {
            logger.info("Validando seguridad para documento: {}", document);
            
            // 1. Verificar que el ciudadano existe
            CiudadanoInfo ciudadano = controladorDatos.getCiudadano(document);
            if (ciudadano == null) {
                logger.warn("Ciudadano no encontrado: {}", document);
                return false;
            }
            
            // 2. Verificar antecedentes criminales (E12)
            boolean tieneAntecedentes = verificarAntecedentes(document);
            if (tieneAntecedentes) {
                logger.warn("Ciudadano con antecedentes criminales: {}", document);
                sistemaAlertas.enviarAlerta(new AlertaSeguridad(
                    "ANTECEDENTES_CRIMINALES", 
                    "Ciudadano con antecedentes: " + document,
                    "ALTA"
                ));
                return false;
            }
            
            // 3. Verificar si ya votó en otro lugar (E13)
            boolean yaVoto = controladorDatos.verificarSiYaVoto(document);
            if (yaVoto) {
                logger.warn("Intento de doble votación: {}", document);
                controladorDatos.agregarSospechoso(document, "DOBLE_VOTACION");
                sistemaAlertas.enviarAlerta(new AlertaSeguridad(
                    "DOBLE_VOTACION",
                    "Intento de voto duplicado: " + document,
                    "CRITICA"
                ));
                return false;
            }
            
            logger.info("Validación de seguridad exitosa para: {}", document);
            return true;
            
        } catch (Exception e) {
            logger.error("Error en validación de seguridad para documento: " + document, e);
            return false;
        }
    }
    
    @Override
    public boolean checkVotingStatus(String document, Current current) {
        try {
            logger.info("Verificando estado de votación para: {}", document);
            
            // Verificar si el ciudadano ya votó
            boolean yaVoto = controladorDatos.verificarSiYaVoto(document);
            
            if (yaVoto) {
                logger.info("Ciudadano {} ya votó", document);
            } else {
                logger.info("Ciudadano {} no ha votado", document);
            }
            
            return !yaVoto; // Retorna true si puede votar (no ha votado)
            
        } catch (Exception e) {
            logger.error("Error verificando estado de votación para: " + document, e);
            return false;
        }
    }
    
    @Override
    public boolean validateMesaZonaAsignada(String document, int mesaId, int zonaId, Current current) {
        try {
            logger.info("Validando zona para documento: {} en zona: {}", document, zonaId);
            
            // Verificar que el ciudadano esté asignado a la zona (no a una mesa específica)
            boolean asignadoAZona = controladorDatos.verificarAsignacionZona(document, zonaId);
            if (!asignadoAZona) {
                logger.warn("Ciudadano no asignado a la zona: {} - zonaId: {}", document, zonaId);
                return false;
            }
            
            // Verificar que no haya votado ya en esta zona
            boolean yaVotoEnZona = controladorDatos.verificarSiYaVotoEnZona(document, zonaId);
            if (yaVotoEnZona) {
                logger.warn("Ciudadano ya votó en la zona: {} - zonaId: {}", document, zonaId);
                return false;
            }
            
            logger.info("Validación de zona exitosa para: {} en zona: {}", document, zonaId);
            return true;
            
        } catch (Exception e) {
            logger.error("Error en validación de zona para documento: " + document, e);
            return false;
        }
    }
    
    private boolean verificarAntecedentes(String document) {
        try {
            // Primero verificar en cache
            Boolean enCache = cacheFiscalia.consultarCache(document);
            if (enCache != null) {
                logger.debug("Resultado de antecedentes desde cache para {}: {}", document, enCache);
                return enCache;
            }
            
            // Si no está en cache, consultar API de fiscalía
            boolean tieneAntecedentes = conexionFiscalia.consultarAntecedentes(document);
            
            // Guardar en cache
            cacheFiscalia.guardarEnCache(document, tieneAntecedentes);
            
            logger.debug("Resultado de antecedentes desde API para {}: {}", document, tieneAntecedentes);
            return tieneAntecedentes;
            
        } catch (Exception e) {
            logger.error("Error consultando antecedentes para: " + document, e);
            // En caso de error, por seguridad permitir el voto
            return false;
        }
    }
} 