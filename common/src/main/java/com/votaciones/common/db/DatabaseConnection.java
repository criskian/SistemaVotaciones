package com.votaciones.common.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final HikariDataSource dataSource;

    static {
        try {
            // Cargar el driver de PostgreSQL
            Class.forName("org.postgresql.Driver");

            Properties props = loadDatabaseProperties();
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(20000);
            config.setAutoCommit(true);

            dataSource = new HikariDataSource(config);
            logger.info("Database connection pool initialized successfully");
        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL driver not found", e);
            throw new RuntimeException("PostgreSQL driver not found", e);
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Could not initialize database connection", e);
        }
    }

    private static Properties loadDatabaseProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("Unable to find database.properties");
            }
            props.load(input);
            logger.info("Loaded database properties from file");
        }
        return props;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    private DatabaseConnection() {
        // Private constructor to prevent instantiation
    }
}