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
INSERT INTO votos (candidato_id, mesa_id, ciudadano_id) VALUES
    (1, 1, 1), -- Juan Pérez vota por Carlos Rodríguez en mesa 1
    (1, 2, 2), -- María López vota por Carlos Rodríguez en mesa 2
    (1, 3, 3), -- Pedro González vota por Carlos Rodríguez en mesa 6
    (1, 4, 4), -- Ana Martínez vota por Carlos Rodríguez en mesa 8
    (2, 1, 1), -- Juan Pérez vota por Laura García en mesa 1
    (2, 2, 2), -- María López vota por Laura García en mesa 2
    (3, 1, 1); -- Juan Pérez vota por Miguel Sánchez en mesa 1

-- Insertar ciudadanos sin votar
INSERT INTO ciudadanos (documento, nombres, apellidos, ciudad_id, mesa_id) VALUES
    ('567890123', 'Carlos', 'Gómez', 1, 3),  -- Mesa 3 en Colegio San José
    ('678901234', 'Laura', 'Torres', 2, 6),  -- Mesa 2 en Colegio San Juan
    ('789012345', 'Roberto', 'Sánchez', 3, 7), -- Mesa 1 en Colegio Santa Ana
    ('890123456', 'Patricia', 'López', 4, 8);  -- Mesa 2 en Colegio Santa Ana 