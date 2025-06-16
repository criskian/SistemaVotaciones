-- Eliminar votos duplicados manteniendo solo uno por combinación de ciudadano_id, candidato_id y mesa_id
DELETE FROM votos a USING (
    SELECT MIN(id) as id, ciudadano_id, candidato_id, mesa_id
    FROM votos
    GROUP BY ciudadano_id, candidato_id, mesa_id
) b
WHERE a.ciudadano_id = b.ciudadano_id 
AND a.candidato_id = b.candidato_id
AND a.mesa_id = b.mesa_id
AND a.id != b.id; 