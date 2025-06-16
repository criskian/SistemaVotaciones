-- Limpiar datos existentes de ciudadanos y asignaciones
TRUNCATE TABLE votos CASCADE;
TRUNCATE TABLE asignaciones_ciudadanos CASCADE;
TRUNCATE TABLE ciudadanos CASCADE;

-- Reiniciar secuencias
ALTER SEQUENCE ciudadanos_id_seq RESTART WITH 1;
ALTER SEQUENCE asignaciones_ciudadanos_id_seq RESTART WITH 1;
ALTER SEQUENCE votos_id_seq RESTART WITH 1;

-- Insertar ciudadanos para Zona Norte (zona_id = 1)
INSERT INTO ciudadanos (documento, nombres, apellidos, ciudad_id, zona_id) VALUES
    ('100000001', 'Juan', 'Pérez', 1, 1),
    ('100000002', 'María', 'López', 1, 1),
    ('100000003', 'Carlos', 'González', 1, 1),
    ('100000004', 'Ana', 'Martínez', 1, 1),
    ('100000005', 'Pedro', 'Rodríguez', 1, 1),
    ('100000006', 'Laura', 'Sánchez', 1, 1);

-- Insertar ciudadanos para Zona Sur (zona_id = 2)
INSERT INTO ciudadanos (documento, nombres, apellidos, ciudad_id, zona_id) VALUES
    ('200000001', 'Roberto', 'Torres', 1, 2),
    ('200000002', 'Carmen', 'Ramírez', 1, 2),
    ('200000003', 'Jorge', 'Díaz', 1, 2),
    ('200000004', 'Patricia', 'Morales', 1, 2),
    ('200000005', 'Fernando', 'Castro', 1, 2),
    ('200000006', 'Sandra', 'Vargas', 1, 2);

-- Insertar ciudadanos para Zona Centro (zona_id = 3)
INSERT INTO ciudadanos (documento, nombres, apellidos, ciudad_id, zona_id) VALUES
    ('300000001', 'Miguel', 'Hernández', 2, 3),
    ('300000002', 'Isabel', 'Gómez', 2, 3),
    ('300000003', 'Ricardo', 'Silva', 2, 3),
    ('300000004', 'Elena', 'Mendoza', 2, 3),
    ('300000005', 'Alberto', 'Rojas', 2, 3),
    ('300000006', 'Lucía', 'Paredes', 2, 3);

-- Insertar ciudadanos para Zona Este (zona_id = 4)
INSERT INTO ciudadanos (documento, nombres, apellidos, ciudad_id, zona_id) VALUES
    ('400000001', 'Daniel', 'Quintero', 3, 4),
    ('400000002', 'Valentina', 'Ortiz', 3, 4),
    ('400000003', 'Andrés', 'Jiménez', 3, 4),
    ('400000004', 'Carolina', 'Navarro', 3, 4),
    ('400000005', 'Felipe', 'Molina', 3, 4),
    ('400000006', 'Diana', 'Cortés', 3, 4);

-- Insertar asignaciones activas para todos los ciudadanos
INSERT INTO asignaciones_ciudadanos (ciudadano_id, zona_id, estado)
SELECT id, zona_id, 'ACTIVA' FROM ciudadanos;

-- Verificar los ciudadanos insertados
SELECT 
    c.documento,
    c.nombres || ' ' || c.apellidos as nombre_completo,
    z.nombre as zona,
    ci.nombre as ciudad,
    a.estado as estado_asignacion
FROM ciudadanos c
JOIN zonas_electorales z ON c.zona_id = z.id
JOIN ciudades ci ON c.ciudad_id = ci.id
JOIN asignaciones_ciudadanos a ON c.id = a.ciudadano_id
ORDER BY c.zona_id, c.documento; 