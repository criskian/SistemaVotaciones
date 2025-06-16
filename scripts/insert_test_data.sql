-- Limpiar datos existentes
TRUNCATE TABLE votos CASCADE;
TRUNCATE TABLE asignaciones_ciudadanos CASCADE;
TRUNCATE TABLE ciudadanos CASCADE;
TRUNCATE TABLE mesas_votacion CASCADE;
TRUNCATE TABLE colegios CASCADE;
TRUNCATE TABLE zonas_electorales CASCADE;
TRUNCATE TABLE ciudades CASCADE;
TRUNCATE TABLE candidatos CASCADE;

-- Reiniciar secuencias
ALTER SEQUENCE ciudades_id_seq RESTART WITH 1;
ALTER SEQUENCE zonas_electorales_id_seq RESTART WITH 1;
ALTER SEQUENCE colegios_id_seq RESTART WITH 1;
ALTER SEQUENCE mesas_votacion_id_seq RESTART WITH 1;
ALTER SEQUENCE ciudadanos_id_seq RESTART WITH 1;
ALTER SEQUENCE candidatos_id_seq RESTART WITH 1;
ALTER SEQUENCE asignaciones_ciudadanos_id_seq RESTART WITH 1;
ALTER SEQUENCE votos_id_seq RESTART WITH 1;

-- Insertar ciudades
INSERT INTO ciudades (nombre, codigo) VALUES 
    ('Bogotá', 'BOG'),
    ('Medellín', 'MED'),
    ('Cali', 'CAL'),
    ('Barranquilla', 'BAQ');

-- Insertar zonas electorales
INSERT INTO zonas_electorales (nombre, codigo, ciudad_id) VALUES
    ('Zona Norte', 'ZN-BOG', 1),
    ('Zona Sur', 'ZS-BOG', 1),
    ('Zona Centro', 'ZC-MED', 2),
    ('Zona Este', 'ZE-CAL', 3);

-- Insertar colegios
INSERT INTO colegios (nombre, direccion, ciudad_id, zona_id) VALUES
    ('Colegio San José', 'Calle 1 #1-1', 1, 1),
    ('Colegio Santa María', 'Carrera 2 #2-2', 1, 2),
    ('Colegio San Juan', 'Calle 3 #3-3', 2, 3),
    ('Colegio Santa Ana', 'Carrera 4 #4-4', 3, 4);

-- Insertar mesas de votación
INSERT INTO mesas_votacion (numero, colegio_id) VALUES
    (101, 1), (102, 1), (103, 1),  -- Mesas en Colegio San José
    (201, 2), (202, 2),          -- Mesas en Colegio Santa María
    (301, 3), (302, 3),          -- Mesas en Colegio San Juan
    (401, 4), (402, 4);          -- Mesas en Colegio Santa Ana

-- Insertar candidatos
INSERT INTO candidatos (documento, nombres, apellidos, partido_politico) VALUES
    ('111111111', 'Carlos', 'Rodríguez', 'Partido Verde'),
    ('222222222', 'Laura', 'García', 'Partido Azul'),
    ('333333333', 'Miguel', 'Sánchez', 'Partido Rojo'),
    ('444444444', 'Ana', 'Martínez', 'Partido Amarillo'),
    ('555555555', 'Voto', 'Blanco', 'Voto en Blanco');

-- Insertar ciudadanos con sus zonas asignadas
INSERT INTO ciudadanos (documento, nombres, apellidos, ciudad_id, zona_id) VALUES
    ('123456789', 'Juan', 'Pérez', 1, 1),
    ('234567890', 'María', 'López', 1, 2),
    ('345678901', 'Pedro', 'González', 2, 3),
    ('456789012', 'Ana', 'Martínez', 3, 4);

-- Insertar asignaciones de ciudadanos a zonas
INSERT INTO asignaciones_ciudadanos (ciudadano_id, zona_id) VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4); 