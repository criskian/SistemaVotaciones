-- Crear la base de datos
CREATE DATABASE sistema_votaciones;

-- Conectar a la base de datos
--\c sistema_votaciones;

-- Crear tabla de ciudades
CREATE TABLE ciudades (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigo VARCHAR(10) NOT NULL UNIQUE
);

-- Crear tabla de colegios
CREATE TABLE colegios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion TEXT NOT NULL,
    ciudad_id INTEGER REFERENCES ciudades(id)
);

-- Crear tabla de mesas de votación
CREATE TABLE mesas_votacion (
    id SERIAL PRIMARY KEY,
    numero INTEGER NOT NULL,
    colegio_id INTEGER REFERENCES colegios(id),
    UNIQUE(numero, colegio_id)
);

-- Crear tabla de ciudadanos
CREATE TABLE ciudadanos (
    id SERIAL PRIMARY KEY,
    documento VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    ciudad_id INTEGER REFERENCES ciudades(id),
    mesa_id INTEGER REFERENCES mesas_votacion(id)
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
    ciudadano_id INTEGER REFERENCES ciudadanos(id),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
); 