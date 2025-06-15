-- Eliminar tablas si existen (en orden inverso de dependencias)
DROP TABLE IF EXISTS votos CASCADE;
DROP TABLE IF EXISTS asignaciones_ciudadanos CASCADE;
DROP TABLE IF EXISTS ciudadanos CASCADE;
DROP TABLE IF EXISTS mesas_votacion CASCADE;
DROP TABLE IF EXISTS colegios CASCADE;
DROP TABLE IF EXISTS zonas_electorales CASCADE;
DROP TABLE IF EXISTS ciudades CASCADE;
DROP TABLE IF EXISTS candidatos CASCADE;

-- Crear tabla de ciudades
CREATE TABLE ciudades (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigo VARCHAR(10) NOT NULL UNIQUE
);

-- Crear tabla de zonas electorales
CREATE TABLE zonas_electorales (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    ciudad_id INTEGER REFERENCES ciudades(id)
);

-- Crear tabla de colegios
CREATE TABLE colegios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion TEXT NOT NULL,
    ciudad_id INTEGER REFERENCES ciudades(id),
    zona_id INTEGER REFERENCES zonas_electorales(id)
);

-- Crear tabla de mesas de votación
CREATE TABLE mesas_votacion (
    id SERIAL PRIMARY KEY,
    numero INTEGER NOT NULL,
    colegio_id INTEGER REFERENCES colegios(id),
    estado VARCHAR(20) DEFAULT 'INACTIVA',
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(numero, colegio_id)
);

-- Crear tabla de ciudadanos
CREATE TABLE ciudadanos (
    id SERIAL PRIMARY KEY,
    documento VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    ciudad_id INTEGER REFERENCES ciudades(id),
    zona_id INTEGER REFERENCES zonas_electorales(id),
    mesa_id INTEGER REFERENCES mesas_votacion(id),
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de asignaciones_ciudadanos (historial de asignaciones)
CREATE TABLE asignaciones_ciudadanos (
    id SERIAL PRIMARY KEY,
    ciudadano_id INTEGER REFERENCES ciudadanos(id),
    zona_id INTEGER REFERENCES zonas_electorales(id),
    mesa_id INTEGER REFERENCES mesas_votacion(id),
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'ACTIVA',
    observaciones TEXT
);

-- Crear tabla de candidatos
CREATE TABLE candidatos (
    id SERIAL PRIMARY KEY,
    documento VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    partido_politico VARCHAR(100) NOT NULL
);

-- Crear tabla de votos
CREATE TABLE votos (
    id SERIAL PRIMARY KEY,
    candidato_id INTEGER REFERENCES candidatos(id),
    mesa_id INTEGER REFERENCES mesas_votacion(id),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'VALIDO'
);

-- Insertar datos de prueba
INSERT INTO ciudades (nombre, codigo) VALUES 
    ('Bogotá', 'BOG'),
    ('Medellín', 'MED'),
    ('Cali', 'CAL');

-- Insertar zonas electorales
INSERT INTO zonas_electorales (nombre, codigo, ciudad_id) VALUES
    ('Zona Norte', 'ZN-BOG', 1),
    ('Zona Sur', 'ZS-BOG', 1),
    ('Zona Centro', 'ZC-MED', 2);

-- Insertar colegios
INSERT INTO colegios (nombre, direccion, ciudad_id, zona_id) VALUES
    ('Colegio San José', 'Calle 1 #1-1', 1, 1),
    ('Colegio Santa María', 'Carrera 2 #2-2', 1, 2),
    ('Colegio San Juan', 'Calle 3 #3-3', 2, 3);

-- Insertar mesas de votación
INSERT INTO mesas_votacion (numero, colegio_id) VALUES
    (101, 1), (102, 1), (103, 1),
    (201, 2), (202, 2),
    (301, 3), (302, 3);

-- Insertar candidatos
INSERT INTO candidatos (documento, nombres, apellidos, partido_politico) VALUES
    ('111111111', 'Juan', 'Pérez', 'Partido Verde'),
    ('222222222', 'María', 'López', 'Partido Azul'),
    ('333333333', 'Carlos', 'Rodríguez', 'Partido Rojo'),
    ('444444444', 'Ana', 'Martínez', 'Partido Amarillo'),
    ('555555555', 'Voto', 'Blanco', 'Voto en Blanco');

-- Insertar ciudadanos de prueba
INSERT INTO ciudadanos (documento, nombres, apellidos, ciudad_id, zona_id, mesa_id) VALUES
    ('123456789', 'Pedro', 'González', 1, 1, 1),
    ('234567890', 'Laura', 'Ramírez', 1, 2, 4),
    ('345678901', 'Miguel', 'Sánchez', 2, 3, 6),
    ('456789012', 'Carmen', 'Torres', 3, 1, 1);

-- Insertar asignaciones de ciudadanos
INSERT INTO asignaciones_ciudadanos (ciudadano_id, zona_id, mesa_id) VALUES
    (1, 1, 1),
    (2, 2, 4),
    (3, 3, 6),
    (4, 1, 1);

-- Reiniciar secuencias
SELECT setval('ciudades_id_seq', 4, false);
SELECT setval('zonas_electorales_id_seq', 4, false);
SELECT setval('colegios_id_seq', 4, false);
SELECT setval('mesas_votacion_id_seq', 8, false);
SELECT setval('candidatos_id_seq', 6, false);
SELECT setval('ciudadanos_id_seq', 5, false);
SELECT setval('asignaciones_ciudadanos_id_seq', 5, false); 