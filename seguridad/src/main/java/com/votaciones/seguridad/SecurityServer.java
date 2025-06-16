package com.votaciones.seguridad;

import com.votaciones.seguridad.service.*;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityServer {
    private static final Logger logger = LoggerFactory.getLogger(SecurityServer.class);
    private Communicator communicator;
    private ObjectAdapter adapter;
    
    public static void main(String[] args) {
        SecurityServer server = new SecurityServer();
        server.start();
    }
    
    public void start() {
        try {
            logger.info("Iniciando Servidor de Seguridad...");
            
            // Inicializar ICE
            String[] iceArgs = {
                "--Ice.Default.Protocol=tcp",
                "--Ice.Default.Host=127.0.0.1",
                "--Ice.Default.Port=10005"
            };
            
            communicator = Util.initialize(iceArgs);
            
            // Crear adapter
            adapter = communicator.createObjectAdapterWithEndpoints(
                "SecurityServer", "tcp -h 127.0.0.1 -p 10005");
            
            // Inicializar servicios
            ControladorDatos controladorDatos = new ControladorDatos();
            ProxyCacheFiscalia cacheFiscalia = new ProxyCacheFiscalia();
            ConexionSistemaFiscal conexionFiscalia = new ConexionSistemaFiscal();
            SistemaAlertas sistemaAlertas = new SistemaAlertas();
            
            // Crear e instalar el servicio principal
            SecurityServiceI securityService = new SecurityServiceI(
                controladorDatos, cacheFiscalia, conexionFiscalia, sistemaAlertas);
            
            adapter.add(securityService, Util.stringToIdentity("SecurityService"));
            
            // Activar adapter
            adapter.activate();
            
            logger.info("Servidor de Seguridad iniciado en puerto 10005");
            logger.info("Servicios disponibles:");
            logger.info("- SecurityService: tcp -h 127.0.0.1 -p 10005");
            
            // Esperar shutdown
            communicator.waitForShutdown();
            
        } catch (Exception e) {
            logger.error("Error al iniciar servidor de seguridad", e);
        } finally {
            if (communicator != null) {
                communicator.destroy();
            }
        }
    }
} 