-- 1. Limpieza de seguridad
DROP TABLE IF EXISTS care_logs;

-- 2. Crear la tabla
CREATE TABLE care_logs (
                           id BIGSERIAL PRIMARY KEY,
                           profile_id BIGINT NOT NULL,
                           log_type VARCHAR(50) NOT NULL,
                           note TEXT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT fk_care_logs_profile
                               FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE
);