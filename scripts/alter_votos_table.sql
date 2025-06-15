-- Agregar columna ciudadano_id a la tabla votos
ALTER TABLE votos ADD COLUMN ciudadano_id INTEGER REFERENCES ciudadanos(id);

-- Actualizar los votos existentes con ciudadanos válidos
UPDATE votos SET ciudadano_id = 1 WHERE mesa_id = 1;
UPDATE votos SET ciudadano_id = 2 WHERE mesa_id = 2;
UPDATE votos SET ciudadano_id = 3 WHERE mesa_id = 3;
UPDATE votos SET ciudadano_id = 4 WHERE mesa_id = 4; 