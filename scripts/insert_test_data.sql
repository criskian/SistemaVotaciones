-- Limpiar datos existentes
TRUNCATE TABLE votos CASCADE;
TRUNCATE TABLE ciudadanos CASCADE;
TRUNCATE TABLE mesas_votacion CASCADE;
TRUNCATE TABLE colegios CASCADE;
TRUNCATE TABLE ciudades CASCADE;
ALTER SEQUENCE ciudades_id_seq RESTART WITH 1;
ALTER SEQUENCE colegios_id_seq RESTART WITH 1;
ALTER SEQUENCE mesas_votacion_id_seq RESTART WITH 1;

-- Insertar ciudades
INSERT INTO ciudades (nombre) VALUES 
('Bogotá'),
('Medellín'),
('Cali'),
('Barranquilla');

-- Insertar colegios
INSERT INTO colegios (nombre, direccion, ciudad_id) VALUES
('Colegio San José', 'Calle 1 #1-1', 1),
('Colegio Santa María', 'Carrera 2 #2-2', 1),
('Colegio San Juan', 'Calle 3 #3-3', 2),
('Colegio Santa Ana', 'Carrera 4 #4-4', 3);

-- Insertar mesas de votación
INSERT INTO mesas_votacion (numero, colegio_id) VALUES
(1, 1), (2, 1), (3, 1),  -- Mesas en Colegio San José
(1, 2), (2, 2),          -- Mesas en Colegio Santa María
(1, 3), (2, 3),          -- Mesas en Colegio San Juan
(1, 4), (2, 4);          -- Mesas en Colegio Santa Ana

-- Insertar ciudadanos con sus mesas asignadas
INSERT INTO ciudadanos (documento, nombres, apellidos, direccion, ciudad_id, mesa_id) VALUES
('123456789', 'Juan', 'Pérez', 'Calle 10 #5-15', 1, 1),
('234567890', 'María', 'López', 'Carrera 15 #10-25', 1, 2),
('345678901', 'Pedro', 'González', 'Avenida 20 #15-35', 2, 6),
('456789012', 'Ana', 'Martínez', 'Calle 25 #20-45', 3, 8); 