-- 1. Añadimos la columna.
ALTER TABLE users ADD COLUMN role VARCHAR(20);

-- 2. Rellenamos datos para que no falle.
UPDATE users SET role = 'USER' WHERE role IS NULL;

-- 3. Hacemos la columna obligatoria.
ALTER TABLE users ALTER COLUMN role SET NOT NULL;