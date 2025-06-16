package com.votaciones.seguridad.client;

import SecurityModule.SecurityServicePrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper para facilitar la conexión de otros módulos al nodo de seguridad.
 * 
 * PARA USO EN ESTACION-DE-VOTACION-LOCAL Y SISTEMA-DE-GESTION-MESAS:
 * 
 * // Crear conexión
 * SecurityServiceHelper helper = new SecurityServiceHelper();
 * 
 * // Validar ciudadano antes de permitir voto
 * boolean valid = helper.validateSecurity("567890123");
 * if (!valid) {
 *     // Bloquear voto y mostrar error
 * }
 * 
 * // Verificar si ya votó
 * boolean canVote = helper.checkVotingStatus("567890123");
 * 
 * // Validar mesa/zona correcta
 * boolean correctMesa = helper.validateMesaZonaAsignada("567890123", 1, 2);
 * 
 * // Cerrar conexión
 * helper.close();
 */
public class SecurityServiceHelper {
    private static final Logger logger = LoggerFactory.getLogger(SecurityServiceHelper.class);
    
    private static final String SECURITY_SERVICE_PROXY = "SecurityService:tcp -h 127.0.0.1 -p 10005";
    
    private Communicator communicator;
    private SecurityServicePrx securityService;
    private boolean connected;
    
    public SecurityServiceHelper() {
        connect();
    }
    
    /**
     * Conectar al nodo de seguridad
     */
    private void connect() {
        try {
            logger.debug("Conectando al nodo de seguridad...");
            
            communicator = Util.initialize();
            ObjectPrx base = communicator.stringToProxy(SECURITY_SERVICE_PROXY);
            securityService = SecurityServicePrx.checkedCast(base);
            
            if (securityService == null) {
                logger.error("No se pudo conectar al nodo de seguridad en puerto 10005");
                connected = false;
                return;
            }
            
            connected = true;
            logger.info("Conectado exitosamente al nodo de seguridad");
            
        } catch (Exception e) {
            logger.error("Error conectando al nodo de seguridad", e);
            connected = false;
        }
    }
    
    /**
     * Validar la seguridad de un ciudadano antes de permitir el voto.
     * Implementa E12 (antecedentes) y E13 (doble votación).
     * 
     * @param document Cédula del ciudadano
     * @return true si puede votar, false si debe ser bloqueado
     */
    public boolean validateSecurity(String document) {
        if (!connected) {
            logger.warn("No hay conexión al nodo de seguridad, permitiendo voto por defecto");
            return true; // Failsafe: permitir voto si seguridad no disponible
        }
        
        try {
            boolean isValid = securityService.validateSecurity(document);
            logger.debug("Validación de seguridad para {}: {}", document, isValid);
            return isValid;
            
        } catch (Exception e) {
            logger.error("Error validando seguridad para: " + document, e);
            return true; // Failsafe en caso de error
        }
    }
    
    /**
     * Verificar si un ciudadano ya votó (para E13).
     * 
     * @param document Cédula del ciudadano
     * @return true si puede votar, false si ya votó
     */
    public boolean checkVotingStatus(String document) {
        if (!connected) {
            logger.warn("No hay conexión al nodo de seguridad, asumiendo que puede votar");
            return true;
        }
        
        try {
            boolean canVote = securityService.checkVotingStatus(document);
            logger.debug("Estado de votación para {}: {}", document, canVote ? "puede votar" : "ya votó");
            return canVote;
            
        } catch (Exception e) {
            logger.error("Error verificando estado de votación para: " + document, e);
            return true; // Failsafe
        }
    }
    
    /**
     * Validar que el ciudadano esté votando en su mesa/zona asignada (E8).
     * 
     * @param document Cédula del ciudadano
     * @param mesaId ID de la mesa donde intenta votar
     * @param zonaId ID de la zona donde intenta votar
     * @return true si es su mesa/zona correcta, false si no
     */
    public boolean validateMesaZonaAsignada(String document, int mesaId, int zonaId) {
        if (!connected) {
            logger.warn("No hay conexión al nodo de seguridad, permitiendo voto en cualquier mesa");
            return true;
        }
        
        try {
            boolean isCorrectMesa = securityService.validateMesaZonaAsignada(document, mesaId, zonaId);
            logger.debug("Validación mesa/zona para {} en mesa={}, zona={}: {}", 
                        document, mesaId, zonaId, isCorrectMesa);
            return isCorrectMesa;
            
        } catch (Exception e) {
            logger.error("Error validando mesa/zona para: " + document, e);
            return true; // Failsafe
        }
    }
    
    /**
     * Verificar si el nodo de seguridad está disponible
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Reconectar al nodo de seguridad si se perdió la conexión
     */
    public void reconnect() {
        if (communicator != null) {
            try {
                communicator.destroy();
            } catch (Exception e) {
                logger.debug("Error cerrando comunicador anterior", e);
            }
        }
        connect();
    }
    
    /**
     * Cerrar la conexión al nodo de seguridad
     */
    public void close() {
        if (communicator != null) {
            try {
                communicator.destroy();
                connected = false;
                logger.debug("Conexión al nodo de seguridad cerrada");
            } catch (Exception e) {
                logger.warn("Error cerrando conexión al nodo de seguridad", e);
            }
        }
    }
    
    /**
     * Validación completa de seguridad (combinando todos los checks)
     * 
     * @param document Cédula del ciudadano
     * @param mesaId Mesa donde intenta votar
     * @param zonaId Zona donde intenta votar
     * @return true si pasa todas las validaciones, false si alguna falla
     */
    public boolean validateCompleteSecurityCheck(String document, int mesaId, int zonaId) {
        // 1. Verificar antecedentes y doble votación
        if (!validateSecurity(document)) {
            logger.warn("Ciudadano {} bloqueado por validación de seguridad", document);
            return false;
        }
        
        // 2. Verificar que no haya votado ya
        if (!checkVotingStatus(document)) {
            logger.warn("Ciudadano {} ya votó anteriormente", document);
            return false;
        }
        
        // 3. Verificar mesa/zona correcta
        if (!validateMesaZonaAsignada(document, mesaId, zonaId)) {
            logger.warn("Ciudadano {} intentando votar en mesa/zona incorrecta: mesa={}, zona={}", 
                       document, mesaId, zonaId);
            return false;
        }
        
        logger.info("Ciudadano {} pasó todas las validaciones de seguridad", document);
        return true;
    }
} 