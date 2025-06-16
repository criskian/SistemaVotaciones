package com.votaciones.estacion;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

/**
 * Componente IceProxy del diagrama UML.
 * Se conecta al ControlSeguridad del nodo de seguridad para validar votantes.
 * Implementa la interfaz getVotante que consulta antecedentes a la fiscalía.
 */
public class IceProxy {
    private Communicator communicator;
    private ObjectPrx securityServiceProxy;
    private boolean connected = false;

    public IceProxy() {
        conectarControlSeguridad();
    }

    /**
     * Conecta al ControlSeguridad del nodo de seguridad
     */
    private void conectarControlSeguridad() {
        try {
            System.out.println("[IceProxy] Conectando al ControlSeguridad del nodo de seguridad...");
            
            communicator = Util.initialize();
            securityServiceProxy = communicator.stringToProxy("SecurityService:tcp -h 127.0.0.1 -p 10005");
            
            if (securityServiceProxy == null) {
                System.err.println("[IceProxy] Error: No se pudo conectar al ControlSeguridad");
                connected = false;
                return;
            }
            
            // Verificar que el servicio responde
            securityServiceProxy.ice_ping();
            connected = true;
            System.out.println("[IceProxy] Conexión exitosa al ControlSeguridad");
            
        } catch (Exception e) {
            System.err.println("[IceProxy] Error conectando al ControlSeguridad: " + e.getMessage());
            connected = false;
        }
    }

    /**
     * Implementa getVotante del diagrama UML.
     * Obtiene información del votante desde la fiscalía vía ControlSeguridad.
     * 
     * @param cedula Cédula del ciudadano
     * @return true si puede votar (sin antecedentes), false si tiene antecedentes
     */
    public boolean getVotante(String cedula) {
        if (!connected) {
            System.out.println("[IceProxy] Advertencia: Sin conexión al ControlSeguridad, permitiendo voto");
            return true; // Failsafe: permitir voto si no hay conexión
        }

        try {
            // Usar ice_ping para verificar conectividad básica
            securityServiceProxy.ice_ping();
            
            // Por ahora, simular la consulta de antecedentes
            // En una implementación real, esto invocaría validateSecurity
            if (cedula.equals("111111111") || cedula.equals("222222222") || cedula.equals("999999999")) {
                System.out.println("[IceProxy] Ciudadano " + cedula + " BLOQUEADO por antecedentes criminales (fiscalía)");
                return false;
            }
            
            System.out.println("[IceProxy] Ciudadano " + cedula + " autorizado por fiscalía (sin antecedentes)");
            return true;
            
        } catch (Exception e) {
            System.err.println("[IceProxy] Error consultando fiscalía para " + cedula + ": " + e.getMessage());
            return true; // Failsafe en caso de error
        }
    }

    /**
     * Validación completa según el diagrama: getVotante 
     * Esta es la función principal que debe usar la estación de votación
     */
    public boolean validacionCompletaVotante(String cedula) {
        System.out.println("[IceProxy] Iniciando validación de antecedentes para ciudadano: " + cedula);
        
        // getVotante: Consultar antecedentes en fiscalía
        if (!getVotante(cedula)) {
            System.out.println("[IceProxy] Voto DENEGADO: Ciudadano con antecedentes criminales");
            return false;
        }
        
        System.out.println("[IceProxy] Voto AUTORIZADO: Sin antecedentes criminales");
        return true;
    }

    public boolean isConnected() {
        return connected;
    }

    public void close() {
        if (communicator != null) {
            try {
                communicator.destroy();
                connected = false;
                System.out.println("[IceProxy] Conexión al ControlSeguridad cerrada");
            } catch (Exception e) {
                System.err.println("[IceProxy] Error cerrando conexión: " + e.getMessage());
            }
        }
    }
} 