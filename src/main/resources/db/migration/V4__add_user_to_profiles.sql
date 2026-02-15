-- Añadimos la columna 'user_id' a la tabla 'profiles'
ALTER TABLE profiles ADD COLUMN user_id BIGINT;

-- Creamos el vínculo (Foreign Key) con la tabla 'users'
ALTER TABLE profiles
    ADD CONSTRAINT fk_profiles_users
        FOREIGN KEY (user_id) REFERENCES users (id);