-- =====================================================
-- SISTEMA DE VOTACIONES - SCRIPT COMPLETO
-- =====================================================
-- Este script contiene toda la estructura de tablas y datos
-- necesarios para replicar el sistema de votaciones
-- 
-- Fecha de creación: 2025-06-17
-- Base de datos: sistema_votaciones
-- =====================================================

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

INSERT INTO ciudades (id, nombre, codigo) VALUES
(1, 'Cali', 'CALI001'),
(2, 'Bogotá', 'BOG001'),
(3, 'Medellín', 'MED001');

-- =====================================================
-- SECCIÓN 3: DATOS DE ZONAS ELECTORALES
-- =====================================================

INSERT INTO zonas_electorales (id, nombre, codigo, ciudad_id) VALUES
(1, 'Zona Norte', 'ZN001', 1),
(2, 'Zona Sur', 'ZS001', 1),
(3, 'Zona Centro', 'ZC001', 1);

-- =====================================================
-- SECCIÓN 4: DATOS DE COLEGIOS
-- =====================================================

INSERT INTO colegios (id, nombre, direccion, ciudad_id, zona_id) VALUES
(1, 'Colegio San Pedro', 'Calle 15 # 23-45, Zona Norte', 1, 1),
(2, 'Instituto Técnico Sur', 'Carrera 45 # 12-34, Zona Sur', 1, 2),
(3, 'Liceo Central', 'Avenida 8 # 56-78, Zona Centro', 1, 3);

-- =====================================================
-- SECCIÓN 5: DATOS DE MESAS DE VOTACIÓN
-- =====================================================

INSERT INTO mesas_votacion (id, numero, colegio_id, activa) VALUES
(1, 1, 1, false),
(2, 2, 1, false),
(3, 1, 2, false),
(4, 2, 2, false),
(5, 1, 3, false),
(6, 2, 3, false);

-- =====================================================
-- SECCIÓN 6: DATOS DE CIUDADANOS
-- =====================================================

INSERT INTO ciudadanos (id, documento, nombre, ciudad_id) VALUES
(1, '123456789', 'Pedro González', 1),
(2, '987654321', 'Ana Martínez', 1),
(3, '567890123', 'Juan Pérez', 1),
(4, '111111111', 'María Herrera', 1),
(5, '345678901', 'Miguel Sánchez', 1),
(6, '234567890', 'Carmen Ruiz', 1),
(7, '456789012', 'Roberto Silva', 1);

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