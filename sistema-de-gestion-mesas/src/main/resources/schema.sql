-- Create database
CREATE DATABASE votaciones;

-- Connect to the database
\c votaciones;

-- Create tables
CREATE TABLE ciudades (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE zonas_electorales (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigo VARCHAR(20) NOT NULL,
    ciudad_id INTEGER REFERENCES ciudades(id)
);

CREATE TABLE mesas_votacion (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    estado VARCHAR(20) DEFAULT 'INACTIVA',
    zona_id INTEGER REFERENCES zonas_electorales(id)
);

CREATE TABLE ciudadanos (
    id SERIAL PRIMARY KEY,
    documento VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL
);

CREATE TABLE asignaciones_ciudadanos (
    id SERIAL PRIMARY KEY,
    ciudadano_id INTEGER REFERENCES ciudadanos(id),
    zona_id INTEGER REFERENCES zonas_electorales(id),
    mesa_id INTEGER REFERENCES mesas_votacion(id)
);

CREATE TABLE candidatos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    partido VARCHAR(100) NOT NULL
);

CREATE TABLE votos (
    id SERIAL PRIMARY KEY,
    ciudadano_id INTEGER REFERENCES ciudadanos(id),
    mesa_id INTEGER REFERENCES mesas_votacion(id),
    candidato_id INTEGER REFERENCES candidatos(id),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE alertas (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    mensaje TEXT NOT NULL,
    mesa_id INTEGER REFERENCES mesas_votacion(id),
    severidad VARCHAR(20) NOT NULL,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO ciudades (nombre) VALUES ('Bogotá'), ('Medellín'), ('Cali');

INSERT INTO zonas_electorales (nombre, codigo, ciudad_id) VALUES
    ('Zona Norte', 'ZN001', 1),
    ('Zona Sur', 'ZS001', 1),
    ('Zona Centro', 'ZC001', 2);

INSERT INTO mesas_votacion (nombre, zona_id) VALUES
    ('Mesa 1', 1),
    ('Mesa 2', 1),
    ('Mesa 3', 2),
    ('Mesa 4', 3);

INSERT INTO ciudadanos (documento, nombre, apellido) VALUES
    ('1234567890', 'Juan', 'Pérez'),
    ('0987654321', 'María', 'González'),
    ('1122334455', 'Carlos', 'Rodríguez');

INSERT INTO asignaciones_ciudadanos (ciudadano_id, zona_id, mesa_id) VALUES
    (1, 1, 1),
    (2, 1, 2),
    (3, 2, 3);

INSERT INTO candidatos (nombre, partido) VALUES
    ('Candidato A', 'Partido X'),
    ('Candidato B', 'Partido Y'),
    ('Candidato C', 'Partido Z'); 