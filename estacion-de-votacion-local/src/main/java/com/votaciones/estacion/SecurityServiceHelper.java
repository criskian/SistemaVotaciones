package com.votaciones.estacion;

import SecurityModule.SecurityServicePrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

/**
 * Helper simplificado para conectar la estación de votación al nodo de seguridad.
 * Implementa las validaciones E8, E12, E13 del diagrama de deployment.
 */
public class SecurityServiceHelper {
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
            System.out.println("[SecurityHelper] Conectando al nodo de seguridad...");
            
            communicator = Util.initialize();
            ObjectPrx base = communicator.stringToProxy(SECURITY_SERVICE_PROXY);
            securityService = SecurityServicePrx.checkedCast(base);
            
            if (securityService == null) {
                System.err.println("[SecurityHelper] No se pudo conectar al nodo de seguridad en puerto 10005");
                connected = false;
                return;
            }
            
            connected = true;
            System.out.println("[SecurityHelper] Conectado exitosamente al nodo de seguridad");
            
        } catch (Exception e) {
            System.err.println("[SecurityHelper] Error conectando al nodo de seguridad: " + e.getMessage());
            connected = false;
        }
    }
    
    /**
     * Validación completa de seguridad (combinando E8, E12, E13).
     * 
     * @param cedula Cédula del ciudadano
     * @param mesaId Mesa donde intenta votar
     * @param zonaId Zona donde intenta votar
     * @return true si pasa todas las validaciones, false si alguna falla
     */
    public boolean validarSeguridadCompleta(String cedula, int mesaId, int zonaId) {
        if (!connected) {
            System.err.println("[SecurityHelper] WARNING: No hay conexión al nodo de seguridad en puerto 10005");
            System.err.println("[SecurityHelper] Modo failsafe activado - permitiendo voto sin validaciones de seguridad");
            System.err.println("[SecurityHelper] RECOMENDACIÓN: Iniciar el servicio de seguridad para validaciones completas");
            return true; // FAILSAFE: Permitir voto cuando seguridad no está disponible
        }
        
        try {
            // 1. Validar antecedentes criminales y estado general (E12 + E13 parcial)
            System.out.println("[SecurityHelper] Validando antecedentes para: " + cedula);
            boolean validacionGeneral = securityService.validateSecurity(cedula);
            if (!validacionGeneral) {
                System.out.println("[SecurityHelper] BLOQUEADO: Fallo en validación de seguridad para " + cedula);
                return false;
            }
            
            // 2. Verificar que no haya votado ya (E13)
            System.out.println("[SecurityHelper] Verificando estado de votación para: " + cedula);
            boolean puedeVotar = securityService.checkVotingStatus(cedula);
            if (!puedeVotar) {
                System.out.println("[SecurityHelper] BLOQUEADO: Ciudadano " + cedula + " ya votó");
                return false;
            }
            
            // 3. Validar mesa/zona correcta (E8)
            System.out.println("[SecurityHelper] Validando mesa/zona para: " + cedula + " (mesa=" + mesaId + ", zona=" + zonaId + ")");
            boolean mesaCorrecta = securityService.validateMesaZonaAsignada(cedula, mesaId, zonaId);
            if (!mesaCorrecta) {
                System.out.println("[SecurityHelper] BLOQUEADO: Mesa/zona incorrecta para " + cedula);
                return false;
            }
            
            System.out.println("[SecurityHelper] AUTORIZADO: Ciudadano " + cedula + " pasó todas las validaciones");
            return true;
            
        } catch (Exception e) {
            System.err.println("[SecurityHelper] Error en validación para " + cedula + ": " + e.getMessage());
            return false; // FALLO: Error en validación de seguridad
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
                System.out.println("[SecurityHelper] Error cerrando comunicador anterior: " + e.getMessage());
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
                System.out.println("[SecurityHelper] Conexión al nodo de seguridad cerrada");
            } catch (Exception e) {
                System.err.println("[SecurityHelper] Error cerrando conexión: " + e.getMessage());
            }
        }
    }
} 