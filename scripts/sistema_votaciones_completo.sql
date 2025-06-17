-- =====================================================
-- SISTEMA DE VOTACIONES - SCRIPT COMPLETO
-- =====================================================
-- Este script contiene toda la estructura de tablas y datos
-- necesarios para replicar el sistema de votaciones
-- 
-- Fecha de creación: 2025-06-17
-- Base de datos: sistema_votaciones
-- =====================================================

-- Limpiar datos existentes
TRUNCATE TABLE votos CASCADE;
TRUNCATE TABLE sospechosos CASCADE;
TRUNCATE TABLE asignaciones_ciudadanos CASCADE;
TRUNCATE TABLE ciudadanos CASCADE;
TRUNCATE TABLE mesas_votacion CASCADE;
TRUNCATE TABLE colegios CASCADE;
TRUNCATE TABLE zonas_electorales CASCADE;
TRUNCATE TABLE candidatos CASCADE;
TRUNCATE TABLE logs CASCADE;
TRUNCATE TABLE ciudades CASCADE;

-- Resetear las secuencias
ALTER SEQUENCE ciudades_id_seq RESTART WITH 1;
ALTER SEQUENCE zonas_electorales_id_seq RESTART WITH 1;
ALTER SEQUENCE colegios_id_seq RESTART WITH 1;
ALTER SEQUENCE mesas_votacion_id_seq RESTART WITH 1;
ALTER SEQUENCE ciudadanos_id_seq RESTART WITH 1;
ALTER SEQUENCE asignaciones_ciudadanos_id_seq RESTART WITH 1;
ALTER SEQUENCE candidatos_id_seq RESTART WITH 1;
ALTER SEQUENCE votos_id_seq RESTART WITH 1;
ALTER SEQUENCE sospechosos_id_seq RESTART WITH 1;
ALTER SEQUENCE logs_id_seq RESTART WITH 1;

-- Crear la base de datos si no existe
-- CREATE DATABASE sistema_votaciones;

-- Conectar a la base de datos
-- \c sistema_votaciones;

-- =====================================================
-- SECCIÓN 1: ESTRUCTURA DE TABLAS
-- =====================================================

-- Tabla de ciudades
CREATE TABLE IF NOT EXISTS ciudades (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigo VARCHAR(10) UNIQUE NOT NULL
);

-- Tabla de zonas electorales
CREATE TABLE IF NOT EXISTS zonas_electorales (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigo VARCHAR(10) UNIQUE NOT NULL,
    ciudad_id INTEGER REFERENCES ciudades(id)
);

-- Tabla de colegios
CREATE TABLE IF NOT EXISTS colegios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    direccion TEXT,
    ciudad_id INTEGER REFERENCES ciudades(id),
    zona_id INTEGER REFERENCES zonas_electorales(id)
);

-- Tabla de mesas de votación
CREATE TABLE IF NOT EXISTS mesas_votacion (
    id SERIAL PRIMARY KEY,
    numero INTEGER NOT NULL,
    colegio_id INTEGER REFERENCES colegios(id),
    activa BOOLEAN DEFAULT false,
    estado VARCHAR(20) DEFAULT 'INACTIVA',
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(numero, colegio_id)
);

-- Tabla de ciudadanos
CREATE TABLE IF NOT EXISTS ciudadanos (
    id SERIAL PRIMARY KEY,
    documento VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    ciudad_id INTEGER REFERENCES ciudades(id)
);

-- Tabla de asignaciones de ciudadanos a zonas
CREATE TABLE IF NOT EXISTS asignaciones_ciudadanos (
    id SERIAL PRIMARY KEY,
    ciudadano_id INTEGER REFERENCES ciudadanos(id),
    zona_id INTEGER REFERENCES zonas_electorales(id),
    activo BOOLEAN DEFAULT true
);

-- Tabla de candidatos
CREATE TABLE IF NOT EXISTS candidatos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    partido VARCHAR(100),
    propuestas TEXT
);

-- Tabla de votos
CREATE TABLE IF NOT EXISTS votos (
    id SERIAL PRIMARY KEY,
    ciudadano_id INTEGER REFERENCES ciudadanos(id),
    candidato_id INTEGER REFERENCES candidatos(id),
    mesa_id INTEGER REFERENCES mesas_votacion(id),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de sospechosos
CREATE TABLE IF NOT EXISTS sospechosos (
    id SERIAL PRIMARY KEY,
    ciudadano_id INTEGER REFERENCES ciudadanos(id),
    motivo TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de logs del sistema
CREATE TABLE IF NOT EXISTS logs (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(50),
    mensaje TEXT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- SECCIÓN 2: DATOS DE CIUDADES
-- =====================================================

INSERT INTO ciudades (nombre, codigo) VALUES
('Cali', 'CALI001'),
('Bogotá', 'BOG001'),
('Medellín', 'MED001'),
('Barranquilla', 'BAQ001'),
('Cartagena', 'CTG001');

-- =====================================================
-- SECCIÓN 3: DATOS DE ZONAS ELECTORALES
-- =====================================================

INSERT INTO zonas_electorales (nombre, codigo, ciudad_id) VALUES
('Zona Norte', 'ZN001', 1),
('Zona Sur', 'ZS001', 1),
('Zona Centro', 'ZC001', 1),
('Zona Oriental', 'ZO001', 1),
('Zona Occidental', 'ZOC001', 1),
('Zona Noroccidental', 'ZNO001', 1),
('Zona Nororiental', 'ZNE001', 1),
('Zona Suroriental', 'ZSE001', 1),
('Zona Suroccidental', 'ZSO001', 1),
('Zona Industrial', 'ZI001', 1);

-- =====================================================
-- SECCIÓN 4: DATOS DE COLEGIOS
-- =====================================================

INSERT INTO colegios (nombre, direccion, ciudad_id, zona_id) VALUES
('Colegio San Pedro', 'Calle 15 # 23-45, Zona Norte', 1, 1),
('Instituto Técnico Sur', 'Carrera 45 # 12-34, Zona Sur', 1, 2),
('Liceo Central', 'Avenida 8 # 56-78, Zona Centro', 1, 3),
('Colegio Oriental', 'Calle 25 # 30-15, Zona Oriental', 1, 4),
('Instituto Occidental', 'Carrera 50 # 20-30, Zona Occidental', 1, 5),
('Colegio Noroccidental', 'Avenida 10 # 40-50, Zona Noroccidental', 1, 6),
('Liceo Nororiental', 'Calle 35 # 15-25, Zona Nororiental', 1, 7),
('Instituto Suroriental', 'Carrera 60 # 25-35, Zona Suroriental', 1, 8),
('Colegio Suroccidental', 'Avenida 15 # 45-55, Zona Suroccidental', 1, 9),
('Instituto Industrial', 'Calle 40 # 20-30, Zona Industrial', 1, 10);

-- =====================================================
-- SECCIÓN 5: DATOS DE MESAS DE VOTACIÓN
-- =====================================================

-- Generar 25 mesas para cada colegio
DO $$
DECLARE
    colegio_id INTEGER;
BEGIN
    FOR colegio_id IN 1..10 LOOP
        FOR mesa_num IN 1..25 LOOP
            INSERT INTO mesas_votacion (numero, colegio_id, activa) 
            VALUES (mesa_num, colegio_id, false);
        END LOOP;
    END LOOP;
END $$;

-- Actualizar el estado de las mesas basado en el campo activa
UPDATE mesas_votacion SET estado = CASE WHEN activa THEN 'ACTIVA' ELSE 'INACTIVA' END;

-- =====================================================
-- SECCIÓN 6: DATOS DE CIUDADANOS
-- =====================================================

-- Función para generar nombres aleatorios
CREATE OR REPLACE FUNCTION generar_nombre_aleatorio() RETURNS VARCHAR AS $$
DECLARE
    nombres VARCHAR[] := ARRAY['Juan', 'María', 'Carlos', 'Ana', 'Pedro', 'Laura', 'José', 'Sofía', 'Miguel', 'Isabella',
                              'Luis', 'Valentina', 'Andrés', 'Camila', 'Diego', 'Natalia', 'Fernando', 'Gabriela', 'Ricardo', 'Daniela',
                              'Alejandro', 'Mariana', 'David', 'Carolina', 'Javier', 'Paula', 'Roberto', 'Andrea', 'Eduardo', 'Juliana',
                              'Felipe', 'Catalina', 'Santiago', 'Valeria', 'Cristian', 'Manuela', 'Daniel', 'María José', 'Gustavo', 'Laura'];
    apellidos VARCHAR[] := ARRAY['González', 'Rodríguez', 'Martínez', 'López', 'Pérez', 'Sánchez', 'Ramírez', 'Torres', 'Flores', 'Rivera',
                                'Morales', 'Castro', 'Ortiz', 'Silva', 'Núñez', 'Cruz', 'Medina', 'Reyes', 'Gómez', 'Díaz',
                                'Herrera', 'Vargas', 'Romero', 'Suárez', 'Mendoza', 'Guerrero', 'Rojas', 'Molina', 'Álvarez', 'Jiménez',
                                'Moreno', 'Muñoz', 'Alonso', 'Gutiérrez', 'Navarro', 'Rubio', 'Dominguez', 'Soto', 'Cortés', 'Garrido'];
BEGIN
    RETURN nombres[floor(random() * array_length(nombres, 1)) + 1] || ' ' ||
           apellidos[floor(random() * array_length(apellidos, 1)) + 1] || ' ' ||
           apellidos[floor(random() * array_length(apellidos, 1)) + 1];
END;
$$ LANGUAGE plpgsql;

-- Insertar 10,000 ciudadanos (1,000 por cada zona)
DO $$
DECLARE
    zona_id INTEGER;
    ciudadano_id INTEGER;
    documento_base INTEGER := 1000000000;
    batch_size INTEGER := 1000; -- Tamaño del lote para mejor rendimiento
BEGIN
    -- Para cada zona
    FOR zona_id IN 1..10 LOOP
        -- Insertar 1,000 ciudadanos por zona
        FOR i IN 1..1000 LOOP
            -- Insertar ciudadano
            INSERT INTO ciudadanos (documento, nombre, ciudad_id)
            VALUES (
                (documento_base + (zona_id * 1000) + i)::VARCHAR,
                generar_nombre_aleatorio(),
                1
            )
            RETURNING id INTO ciudadano_id;
            
            -- Asignar ciudadano a la zona
            INSERT INTO asignaciones_ciudadanos (ciudadano_id, zona_id, activo)
            VALUES (ciudadano_id, zona_id, true);
            
            -- Mostrar progreso cada 1000 registros
            IF i % 1000 = 0 THEN
                RAISE NOTICE 'Procesados % ciudadanos para la zona %', i, zona_id;
            END IF;
        END LOOP;
    END LOOP;
END $$;

-- Verificar la distribución de ciudadanos por zona
SELECT 
    ze.nombre as zona,
    COUNT(ac.ciudadano_id) as total_ciudadanos,
    ROUND(COUNT(ac.ciudadano_id)::numeric / 10000 * 100, 2) as porcentaje
FROM zonas_electorales ze
LEFT JOIN asignaciones_ciudadanos ac ON ze.id = ac.zona_id
GROUP BY ze.id, ze.nombre
ORDER BY ze.id;

-- =====================================================
-- SECCIÓN 7: DATOS DE ASIGNACIONES DE CIUDADANOS
-- =====================================================

INSERT INTO asignaciones_ciudadanos (id, ciudadano_id, zona_id, activo) VALUES
(1, 1, 1, true),  -- Pedro González -> Zona Norte
(2, 2, 1, true),  -- Ana Martínez -> Zona Norte
(3, 3, 1, true),  -- Juan Pérez -> Zona Norte
(4, 4, 1, true),  -- María Herrera -> Zona Norte
(5, 5, 2, true),  -- Miguel Sánchez -> Zona Sur
(6, 6, 2, true),  -- Carmen Ruiz -> Zona Sur
(7, 7, 3, true);  -- Roberto Silva -> Zona Centro

-- =====================================================
-- SECCIÓN 8: DATOS DE CANDIDATOS
-- =====================================================

INSERT INTO candidatos (id, nombre, partido, propuestas) VALUES
(1, 'Carlos Mendoza', 'Partido Liberal', 'Educación gratuita, mejoras en salud pública'),
(2, 'Laura Torres', 'Partido Conservador', 'Seguridad ciudadana, desarrollo económico'),
(3, 'Roberto Jiménez', 'Partido Verde', 'Medio ambiente, energías renovables'),
(4, 'Ana Patricia López', 'Partido de la U', 'Infraestructura, empleo juvenil');

-- =====================================================
-- SECCIÓN 9: DATOS DE VOTOS (ejemplos)
-- =====================================================

-- Nota: Esta tabla estará vacía inicialmente, se llenará durante las votaciones
-- INSERT INTO votos (ciudadano_id, candidato_id, mesa_id, fecha_hora) VALUES
-- (1, 1, 1, '2025-06-17 23:00:00');

-- =====================================================
-- SECCIÓN 10: DATOS DE SOSPECHOSOS (vacía inicialmente)
-- =====================================================

-- Nota: Esta tabla estará vacía inicialmente
-- INSERT INTO sospechosos (ciudadano_id, motivo, fecha_registro) VALUES
-- (1, 'Voto duplicado detectado', '2025-06-17 23:00:00');

-- =====================================================
-- SECCIÓN 11: DATOS DE LOGS (ejemplos)
-- =====================================================

INSERT INTO logs (id, tipo, mensaje, fecha_hora) VALUES
(1, 'SISTEMA', 'Base de datos inicializada correctamente', '2025-06-17 23:00:00'),
(2, 'MESA', 'Mesa 1 activada en Colegio San Pedro', '2025-06-17 23:00:00');

-- =====================================================
-- SECCIÓN 12: ÍNDICES PARA OPTIMIZACIÓN
-- =====================================================

-- Índices para mejorar el rendimiento de las consultas
CREATE INDEX IF NOT EXISTS idx_ciudadanos_documento ON ciudadanos(documento);
CREATE INDEX IF NOT EXISTS idx_asignaciones_ciudadano ON asignaciones_ciudadanos(ciudadano_id);
CREATE INDEX IF NOT EXISTS idx_asignaciones_zona ON asignaciones_ciudadanos(zona_id);
CREATE INDEX IF NOT EXISTS idx_votos_ciudadano ON votos(ciudadano_id);
CREATE INDEX IF NOT EXISTS idx_votos_mesa ON votos(mesa_id);
CREATE INDEX IF NOT EXISTS idx_mesas_colegio ON mesas_votacion(colegio_id);
CREATE INDEX IF NOT EXISTS idx_colegios_zona ON colegios(zona_id);

-- =====================================================
-- SECCIÓN 13: VERIFICACIÓN DE DATOS
-- =====================================================

-- Consultas para verificar que los datos se insertaron correctamente
SELECT 'Ciudades' as tabla, COUNT(*) as total FROM ciudades
UNION ALL
SELECT 'Zonas Electorales', COUNT(*) FROM zonas_electorales
UNION ALL
SELECT 'Colegios', COUNT(*) FROM colegios
UNION ALL
SELECT 'Mesas de Votación', COUNT(*) FROM mesas_votacion
UNION ALL
SELECT 'Ciudadanos', COUNT(*) FROM ciudadanos
UNION ALL
SELECT 'Asignaciones', COUNT(*) FROM asignaciones_ciudadanos
UNION ALL
SELECT 'Candidatos', COUNT(*) FROM candidatos
UNION ALL
SELECT 'Votos', COUNT(*) FROM votos
UNION ALL
SELECT 'Sospechosos', COUNT(*) FROM sospechosos
UNION ALL
SELECT 'Logs', COUNT(*) FROM logs;

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
-- 
-- Para ejecutar este script:
-- 1. Crear la base de datos: CREATE DATABASE sistema_votaciones;
-- 2. Conectar a la base de datos: \c sistema_votaciones;
-- 3. Ejecutar este script: \i scripts/sistema_votaciones_completo.sql
--
-- Nota: Este script incluye toda la estructura y datos necesarios
-- para que el sistema de votaciones funcione correctamente.
-- ===================================================== 