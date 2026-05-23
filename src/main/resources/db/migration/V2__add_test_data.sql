-- 1. Insertar usuario Admin (Contraseña: 'admin123' con Hash real de 60 caracteres)
INSERT INTO users (email, password, full_name, role)
VALUES ('admin@alora.com', '$2a$10$z1og2ob36pX/gpJ9VvxNk.u3wZyPSOIkGZwfY6rsVdoDta/A/tuTi', 'User Admin', 'USER');

-- 2. Insertar perfiles de prueba asociados al admin
INSERT INTO profiles (
    full_name, email, birth_date, gender, medical_conditions, allergies, medications,
    approx_address, city, emergency_contact_name, emergency_contact_phone,
    emergency_contact_email, relationship, qr_token, pin_code, user_id
) VALUES
      ('María López', 'maria@example.com', '1945-04-12', 'F', 'Hipertensión', 'Penicilina', 'Enalapril 10mg',
       'C/ Olivo 12', 'Málaga', 'Ana López', '+34 600 111 222', 'ana@example.com', 'Hija',
       'QR_ABC123', '4321', (SELECT id FROM users WHERE email='admin@alora.com')),

      ('José Ruiz', 'jose@example.com', '1939-10-03', 'M', 'Diabetes tipo 2', 'Nueces', 'Metformina 850mg',
       'Av. del Parque s/n', 'Sevilla', 'Carlos Ruiz', '+34 600 333 444', 'carlos@example.com', 'Hijo',
       'QR_DEF456', '2468', (SELECT id FROM users WHERE email='admin@alora.com'));

-- 3. Insertar registros de bitácora
INSERT INTO care_logs (profile_id, log_type, note) VALUES
                                                       ((SELECT id FROM profiles WHERE qr_token='QR_ABC123'), 'MEDICACION', '14:30 – Comió bien y tomó la medicación'),
                                                       ((SELECT id FROM profiles WHERE qr_token='QR_DEF456'), 'CONTROL', '09:00 – Glucemia correcta antes del desayuno');

-- 4. Insertar recordatorios de prueba
INSERT INTO reminders (title, time, is_active, profile_id) VALUES
                                                               ('Pastilla de la tensión', '09:00:00', true, (SELECT id FROM profiles WHERE qr_token='QR_ABC123')),
                                                               ('Paseo vespertino', '18:30:00', true, (SELECT id FROM profiles WHERE qr_token='QR_ABC123'));