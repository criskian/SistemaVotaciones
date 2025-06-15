-- Insertar ciudades
INSERT INTO ciudades (nombre, codigo) VALUES
    ('Bogotá', 'BOG'),
    ('Medellín', 'MED'),
    ('Cali', 'CAL'),
    ('Barranquilla', 'BAQ');

-- Insertar colegios
INSERT INTO colegios (nombre, direccion, ciudad_id) VALUES
    ('Colegio San José', 'Calle 1 #1-1', 1),
    ('Colegio Santa María', 'Carrera 2 #2-2', 1),
    ('Colegio San Juan', 'Calle 3 #3-3', 2),
    ('Colegio Santa Ana', 'Carrera 4 #4-4', 3);

-- Insertar mesas de votación
INSERT INTO mesas_votacion (numero, colegio_id) VALUES
    (1, 1), (2, 1), (3, 1),
    (1, 2), (2, 2),
    (1, 3), (2, 3),
    (1, 4), (2, 4);

-- Insertar ciudadanos
INSERT INTO ciudadanos (documento, nombres, apellidos, ciudad_id, mesa_id) VALUES
    ('123456789', 'Juan', 'Pérez', 1, 1),
    ('234567890', 'María', 'López', 1, 2),
    ('345678901', 'Pedro', 'González', 2, 6),
    ('456789012', 'Ana', 'Martínez', 3, 8);

-- Insertar candidatos
INSERT INTO candidatos (documento, nombres, apellidos, partido_politico) VALUES
    ('111111111', 'Carlos', 'Rodríguez', 'Partido A'),
    ('222222222', 'Laura', 'García', 'Partido B'),
    ('333333333', 'Miguel', 'Sánchez', 'Partido C');

-- Insertar algunos votos de prueba
INSERT INTO votos (candidato_id, mesa_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
    (2, 1), (2, 2), (2, 3),
    (3, 1), (3, 2); 