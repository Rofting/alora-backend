-- 1. Tabla para los familiares/cuidadores (Usuarios de la App)
create table if not exists users (
                                     id bigserial primary key,
                                     email varchar(100) not null unique,
                                     password varchar(255) not null, -- Aquí guardaremos la contraseña encriptada
                                     full_name varchar(100) not null,
                                     created_at timestamp default now()
);

-- 2. Tabla intermedia: ¿Quién cuida de quién?
-- Esto permite que un usuario cuide a varios perfiles (ej. padre y madre)
-- y que un perfil sea cuidado por varios usuarios (ej. varios hermanos)
create table if not exists user_profile_access (
                                                   user_id bigint not null references users(id) on delete cascade,
                                                   profile_id bigint not null references profiles(id) on delete cascade,
                                                   role varchar(20) default 'EDITOR',
                                                   primary key (user_id, profile_id)
);

-- 3. Usuario de prueba para el desarrollo
-- La contraseña es 'admin123' pero ya encriptada con BCrypt
insert into users (email, password, full_name)
values ('admin@alora.com', '$2a$10$wS2/7xG4.Q9.2y.F8e/8..', 'Admin Familiar');

-- Asignamos al usuario 'Admin Familiar' el cuidado de 'María López' (que creamos en la V1)
insert into user_profile_access (user_id, profile_id)
values (
           (select id from users where email='admin@alora.com'),
           (select id from profiles where qr_token='QR_ABC123')
       );