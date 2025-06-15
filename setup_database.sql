-- Script simple para insertar datos de prueba
-- Limpiar datos existentes
DELETE FROM votos;
DELETE FROM ciudadanos;
DELETE FROM mesas_votacion;
DELETE FROM colegios;
DELETE FROM ciudades;
DELETE FROM candidatos;

-- Insertar ciudades
INSERT INTO ciudades (id, nombre, codigo) VALUES 
(1, 'Bogotá', 'BOG'),
(2, 'Medellín', 'MED'),
(3, 'Cali', 'CAL');

-- Insertar colegios
INSERT INTO colegios (id, nombre, direccion, ciudad_id) VALUES
(1, 'Colegio San José', 'Calle 1 #1-1', 1),
(2, 'Colegio Santa María', 'Carrera 2 #2-2', 1),
(3, 'Colegio San Juan', 'Calle 3 #3-3', 2);

-- Insertar mesas de votación
INSERT INTO mesas_votacion (id, numero, colegio_id) VALUES
(1, 101, 1),
(2, 102, 1),
(3, 201, 2),
(4, 301, 3);

-- Insertar candidatos
INSERT INTO candidatos (id, documento, nombres, apellidos, partido_politico) VALUES
(1, '111111111', 'Juan', 'Pérez', 'Partido Verde'),
(2, '222222222', 'María', 'López', 'Partido Azul'),
(3, '333333333', 'Carlos', 'Rodríguez', 'Partido Rojo'),
(4, '444444444', 'Ana', 'Martínez', 'Partido Amarillo'),
(5, '555555555', 'Voto', 'Blanco', 'Voto en Blanco');

-- Insertar ciudadanos de prueba
INSERT INTO ciudadanos (documento, nombres, apellidos, ciudad_id, mesa_id) VALUES
('123456789', 'Pedro', 'González', 1, 1),
('234567890', 'Laura', 'Ramírez', 1, 2),
('345678901', 'Miguel', 'Sánchez', 2, 3),
('456789012', 'Carmen', 'Torres', 3, 4),
('1117013298', 'Carlos', 'Felipe', 1, 1);

-- Reiniciar secuencias
SELECT setval('ciudades_id_seq', 4, false);
SELECT setval('colegios_id_seq', 4, false);
SELECT setval('mesas_votacion_id_seq', 5, false);
SELECT setval('candidatos_id_seq', 6, false);

-- Verificar datos insertados
SELECT 'Ciudadanos insertados:' as mensaje, COUNT(*) as cantidad FROM ciudadanos; 