package com.votaciones.common.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static DatabaseConnection instance;
    private final HikariDataSource dataSource;

    private DatabaseConnection() {
        try {
            Properties props = loadProperties();
            HikariConfig config = new HikariConfig();
            
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.user"));
            config.setPassword(props.getProperty("db.password"));
            config.setDriverClassName(props.getProperty("db.driver"));
            
            // Configuración del pool
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.size")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minIdle")));
            config.setMaxLifetime(Long.parseLong(props.getProperty("db.pool.maxLifetime")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.connectionTimeout")));
            config.setIdleTimeout(Long.parseLong(props.getProperty("db.pool.idleTimeout")));
            
            // Configuración adicional
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            dataSource = new HikariDataSource(config);
            logger.info("Conexión a la base de datos inicializada correctamente");
        } catch (Exception e) {
            logger.error("Error al inicializar la conexión a la base de datos: {}", e.getMessage());
            throw new RuntimeException("Error al inicializar la conexión a la base de datos", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Conexión a la base de datos cerrada correctamente");
        }
    }

    private Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("No se pudo encontrar database.properties");
            }
            props.load(input);
        }
        return props;
    }
} 