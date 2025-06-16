-- Crear la base de datos
DROP DATABASE IF EXISTS sistema_votaciones;
CREATE DATABASE sistema_votaciones;

\c sistema_votaciones;

-- Crear las tablas
CREATE TABLE ciudades (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE colegios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    ciudad_id INTEGER REFERENCES ciudades(id)
);

CREATE TABLE mesas_votacion (
    id SERIAL PRIMARY KEY,
    numero INTEGER NOT NULL,
    colegio_id INTEGER REFERENCES colegios(id)
);

CREATE TABLE ciudadanos (
    documento VARCHAR(20) PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    ciudad_id INTEGER REFERENCES ciudades(id),
    mesa_id INTEGER REFERENCES mesas_votacion(id)
);

CREATE TABLE candidatos (
    id SERIAL PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    partido_politico VARCHAR(100) NOT NULL
);

CREATE TABLE votos (
    id SERIAL PRIMARY KEY,
    mesa_id INTEGER REFERENCES mesas_votacion(id),
    candidato_id INTEGER REFERENCES candidatos(id),
    fecha_hora TIMESTAMP NOT NULL,
    documento_votante VARCHAR(20) REFERENCES ciudadanos(documento)
);

-- Insertar datos de ejemplo
INSERT INTO ciudades (id, nombre) VALUES (1, 'Cali');

INSERT INTO colegios (id, nombre, direccion, ciudad_id)
VALUES (1, 'Colegio San Juan', 'Calle 5 # 23-45', 1);

INSERT INTO mesas_votacion (id, numero, colegio_id)
VALUES (1, 101, 1);

INSERT INTO ciudadanos (documento, nombres, apellidos, direccion, ciudad_id, mesa_id)
VALUES ('1234567890', 'Juan Carlos', 'Pérez Gómez', 'Carrera 15 # 34-56', 1, 1);

-- Reiniciar las secuencias
ALTER SEQUENCE ciudades_id_seq RESTART WITH 2;
ALTER SEQUENCE colegios_id_seq RESTART WITH 2;
ALTER SEQUENCE mesas_votacion_id_seq RESTART WITH 2;
ALTER SEQUENCE candidatos_id_seq RESTART WITH 1; 