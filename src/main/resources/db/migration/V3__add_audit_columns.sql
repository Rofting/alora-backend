-- 1. Añadir columnas a la tabla care_logs
-- 'created_at' ya existía, pero agregamos 'updated_at'
ALTER TABLE care_logs ADD COLUMN updated_at TIMESTAMP;

-- 2. Añadir columnas a la tabla reminders
ALTER TABLE reminders ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE reminders ADD COLUMN updated_at TIMESTAMP;

ALTER TABLE profiles ADD COLUMN updated_at TIMESTAMP;