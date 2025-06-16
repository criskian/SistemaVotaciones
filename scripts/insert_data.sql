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
    (101, 1), (102, 1), (103, 1),
    (201, 2), (202, 2),
    (301, 3), (302, 3),
    (401, 4), (402, 4);

-- Insertar ciudadanos (ahora solo con zona electoral)
INSERT INTO ciudadanos (documento, nombres, apellidos, ciudad_id, zona_id) VALUES
    ('123456789', 'Juan', 'Pérez', 1, 1),
    ('234567890', 'María', 'López', 1, 2),
    ('345678901', 'Pedro', 'González', 2, 3),
    ('456789012', 'Ana', 'Martínez', 3, 4);

-- Insertar candidatos
INSERT INTO candidatos (documento, nombres, apellidos, partido_politico) VALUES
    ('111111111', 'Carlos', 'Rodríguez', 'Partido A'),
    ('222222222', 'Laura', 'García', 'Partido B'),
    ('333333333', 'Miguel', 'Sánchez', 'Partido C');

-- Insertar asignaciones de ciudadanos a zonas
INSERT INTO asignaciones_ciudadanos (ciudadano_id, zona_id) VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4);

-- Ejemplo de registro de votos (cuando los ciudadanos votan)
INSERT INTO votos (ciudadano_id, candidato_id, mesa_id) VALUES
    (1, 1, 1), -- Juan Pérez vota por Carlos Rodríguez en mesa 101
    (2, 1, 4), -- María López vota por Carlos Rodríguez en mesa 201
    (3, 2, 6), -- Pedro González vota por Laura García en mesa 301
    (4, 3, 8); -- Ana Martínez vota por Miguel Sánchez en mesa 401 