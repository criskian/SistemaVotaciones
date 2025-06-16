package com.votaciones.seguridad.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class SecurityDatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(SecurityDatabaseConnection.class);
    
    private static volatile HikariDataSource dataSource;
    
    // Configuración de la base de datos
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/sistema_votaciones";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";  // Usando la nueva contraseña
    
    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (SecurityDatabaseConnection.class) {
                if (dataSource == null) {
                    initializeDataSource();
                }
            }
        }
        return dataSource;
    }
    
    private static void initializeDataSource() {
        try {
            logger.info("Inicializando conexión a base de datos para Seguridad...");
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USER);
            config.setPassword(DB_PASSWORD);
            config.setDriverClassName("org.postgresql.Driver");
            
            // Configuración optimizada para seguridad
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000); // 30 segundos
            config.setIdleTimeout(600000); // 10 minutos
            config.setMaxLifetime(1800000); // 30 minutos
            config.setLeakDetectionThreshold(60000); // 1 minuto
            
            // Configuración específica para PostgreSQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            
            config.setPoolName("SecurityPool");
            
            dataSource = new HikariDataSource(config);
            
            // Probar la conexión
            testConnection();
            
            // Crear tabla de sospechosos si no existe
            createTablesIfNeeded();
            
            logger.info("Conexión a base de datos de Seguridad inicializada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error inicializando conexión a base de datos", e);
            throw new RuntimeException("No se pudo conectar a la base de datos de seguridad", e);
        }
    }
    
    private static void testConnection() {
        try (var connection = dataSource.getConnection()) {
            logger.info("Conexion a PostgreSQL exitosa - Base: {}", connection.getCatalog());
        } catch (Exception e) {
            logger.error("Error probando conexion a base de datos", e);
            throw new RuntimeException("Error de conexión a PostgreSQL", e);
        }
    }
    
    private static void createTablesIfNeeded() {
        try (var connection = dataSource.getConnection();
             var stmt = connection.createStatement()) {
            
            // Crear tabla de sospechosos si no existe
            String createSospechososTable = "CREATE TABLE IF NOT EXISTS sospechosos (" +
                "id SERIAL PRIMARY KEY, " +
                "documento VARCHAR(20) UNIQUE NOT NULL, " +
                "motivo VARCHAR(500) NOT NULL, " +
                "fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(), " +
                "estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO', " +
                "observaciones TEXT, " +
                "created_at TIMESTAMP DEFAULT NOW(), " +
                "updated_at TIMESTAMP DEFAULT NOW())";
            
            stmt.execute(createSospechososTable);
            
            // Crear índices para optimizar consultas
            String createIndexes = "CREATE INDEX IF NOT EXISTS idx_sospechosos_documento ON sospechosos(documento); " +
                "CREATE INDEX IF NOT EXISTS idx_sospechosos_estado ON sospechosos(estado); " +
                "CREATE INDEX IF NOT EXISTS idx_sospechosos_fecha ON sospechosos(fecha_registro);";
            
            stmt.execute(createIndexes);
            
            logger.info("Tablas de seguridad verificadas/creadas exitosamente");
            
        } catch (Exception e) {
            logger.error("Error creando tablas de seguridad", e);
            // No lanzar excepción aquí, las tablas pueden existir
        }
    }
    
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Conexión a base de datos de Seguridad cerrada");
        }
    }
    
    public static boolean isHealthy() {
        try (var connection = dataSource.getConnection()) {
            return connection.isValid(5); // 5 segundos timeout
        } catch (Exception e) {
            logger.warn("Health check de base de datos falló", e);
            return false;
        }
    }
} 