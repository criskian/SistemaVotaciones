-- Eliminar votos duplicados manteniendo solo uno por combinación de candidato_id, mesa_id y ciudadano_id
DELETE FROM votos a USING (
    SELECT MIN(id) as id, candidato_id, mesa_id, ciudadano_id
    FROM votos
    GROUP BY candidato_id, mesa_id, ciudadano_id
) b
WHERE a.candidato_id = b.candidato_id 
AND a.mesa_id = b.mesa_id 
AND a.ciudadano_id = b.ciudadano_id
AND a.id != b.id; 