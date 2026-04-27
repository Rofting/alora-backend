CREATE TABLE reminders (
                           id SERIAL PRIMARY KEY,
                           title VARCHAR(255) NOT NULL,
                           time TIME NOT NULL,
                           is_active BOOLEAN DEFAULT TRUE,
                           profile_id BIGINT NOT NULL,
                           CONSTRAINT fk_reminder_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE
);