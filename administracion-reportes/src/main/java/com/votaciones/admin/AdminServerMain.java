package com.votaciones.admin;

import com.zeroc.Ice.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AdminServerMain {
    private static final Logger logger = LoggerFactory.getLogger(AdminServerMain.class);
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/sistema_votaciones";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";
    private static final int PORT = 10007; // Puerto para el servidor de administración

    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            // Configurar el adaptador
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "AdminAdapter", "default -p " + PORT);

            // Crear e instanciar el servidor
            AdminServerI adminServer = new AdminServerI();
            adapter.add(adminServer, Util.stringToIdentity("AdminServer"));

            // Activar el adaptador
            adapter.activate();
            logger.info("Servidor de Administración y Reportes iniciado en puerto {}", PORT);

            // Mantener el servidor activo
            communicator.waitForShutdown();
        } catch (java.lang.Exception e) {
            logger.error("Error al iniciar el servidor: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    // Método para obtener conexión a la base de datos
    public static Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
} 