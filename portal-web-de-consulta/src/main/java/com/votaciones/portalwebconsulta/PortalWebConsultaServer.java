package com.votaciones.portalwebconsulta;

import com.votaciones.common.db.DatabaseConnection;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class PortalWebConsultaServer {
    private static final Logger logger = LoggerFactory.getLogger(PortalWebConsultaServer.class);

    public static void main(String[] args) {
        int status = 0;
        Communicator communicator = null;

        try {
            // Obtener la fuente de datos
            DataSource dataSource = DatabaseConnection.getDataSource();

            // Inicializar el comunicador Ice
            communicator = Util.initialize(args);

            // Crear el adaptador
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                    "PortalWebConsulta", "default -p 10000");

            // Crear el servant
            PortalWebConsultaI servant = new PortalWebConsultaI();

            // Activar el adaptador y el servant
            adapter.add(servant, new Identity("PortalWebConsulta", ""));
            adapter.activate();

            logger.info("Servidor iniciado en el puerto 10000");

            // Esperar a que se cierre el servidor
            communicator.waitForShutdown();

        } catch (Exception e) {
            logger.error("Error en el servidor", e);
            status = 1;
        } finally {
            if (communicator != null) {
                try {
                    communicator.destroy();
                } catch (Exception e) {
                    logger.error("Error al cerrar el comunicador", e);
                    status = 1;
                }
            }
        }

        System.exit(status);
    }
}