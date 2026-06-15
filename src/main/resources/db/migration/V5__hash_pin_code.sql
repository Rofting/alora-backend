-- Ampliar columna para soportar hashes BCrypt (60 caracteres)
ALTER TABLE profiles ALTER COLUMN pin_code TYPE VARCHAR(72);

-- Los PINs de los datos de prueba (V2) eran texto plano; se anulan para que
-- el cuidador los reasigne a través de la API, que ya aplica BCrypt.
UPDATE profiles SET pin_code = NULL WHERE qr_token IN ('QR_ABC123', 'QR_DEF456');
