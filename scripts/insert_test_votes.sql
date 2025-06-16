-- Insertar votos de prueba
-- Asegurarse de que los ciudadanos, candidatos y mesas existan primero

-- Insertar votos para ciudadanos en diferentes mesas
INSERT INTO votos (ciudadano_id, candidato_id, mesa_id, fecha_hora, estado) VALUES
    -- Votos en Colegio San José (Mesa 101)
    (1, 1, 1, NOW(), 'VALIDO'),  -- Juan Pérez vota por Carlos Rodríguez
    (2, 2, 1, NOW(), 'VALIDO'),  -- María López vota por Laura García
    
    -- Votos en Colegio Santa María (Mesa 201)
    (3, 1, 4, NOW(), 'VALIDO'),  -- Pedro González vota por Carlos Rodríguez
    (4, 3, 4, NOW(), 'VALIDO'),  -- Ana Martínez vota por Miguel Sánchez
    
    -- Votos en Colegio San Juan (Mesa 301)
    (1, 2, 6, NOW(), 'VALIDO'),  -- Juan Pérez vota por Laura García
    (2, 1, 6, NOW(), 'VALIDO'),  -- María López vota por Carlos Rodríguez
    
    -- Votos en Colegio Santa Ana (Mesa 401)
    (3, 3, 8, NOW(), 'VALIDO'),  -- Pedro González vota por Miguel Sánchez
    (4, 2, 8, NOW(), 'VALIDO');  -- Ana Martínez vota por Laura García

-- Actualizar el estado de las mesas a ACTIVA
UPDATE mesas_votacion SET estado = 'ACTIVA' WHERE id IN (1, 4, 6, 8);

-- Verificar los votos insertados
SELECT 
    c.documento,
    c.nombres || ' ' || c.apellidos as votante,
    ca.nombres || ' ' || ca.apellidos as candidato,
    m.numero as mesa,
    col.nombre as colegio,
    v.fecha_hora
FROM votos v
JOIN ciudadanos c ON v.ciudadano_id = c.id
JOIN candidatos ca ON v.candidato_id = ca.id
JOIN mesas_votacion m ON v.mesa_id = m.id
JOIN colegios col ON m.colegio_id = col.id
ORDER BY v.fecha_hora; 