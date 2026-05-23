-- 1. Tabla de Usuarios (Cuidadores) con el campo ROLE incluido
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla de Perfiles con la relación directa a User (user_id) y campo de foto
CREATE TABLE profiles (
                          id BIGSERIAL PRIMARY KEY,
                          full_name VARCHAR(100) NOT NULL,
                          email VARCHAR(100),
                          birth_date DATE,
                          gender VARCHAR(20),
                          medical_conditions TEXT,
                          allergies TEXT,
                          medications TEXT,
                          approx_address VARCHAR(150),
                          city VARCHAR(80),
                          emergency_contact_name VARCHAR(100),
                          emergency_contact_phone VARCHAR(30),
                          emergency_contact_email VARCHAR(100),
                          relationship VARCHAR(100),
                          qr_token VARCHAR(64) NOT NULL UNIQUE,
                          pin_code VARCHAR(12),
                          photo_url VARCHAR(255),
                          user_id BIGINT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_profiles_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_profiles_qr_token ON profiles(qr_token);

-- 3. Tabla de Bitácora (Care Logs) unificada
CREATE TABLE care_logs (
                           id BIGSERIAL PRIMARY KEY,
                           profile_id BIGINT NOT NULL,
                           log_type VARCHAR(50) NOT NULL,
                           note TEXT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT fk_care_logs_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE
);

-- 4. Tabla de Recordatorios con ID BIGINT
CREATE TABLE reminders (
                           id BIGSERIAL PRIMARY KEY,
                           title VARCHAR(255) NOT NULL,
                           time TIME NOT NULL,
                           is_active BOOLEAN DEFAULT TRUE,
                           profile_id BIGINT NOT NULL,

                           CONSTRAINT fk_reminder_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE
);