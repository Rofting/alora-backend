-- Tabla principal de perfiles (lo crítico del flujo QR+PIN)
create table if not exists profiles (
                                        id bigserial primary key,
                                        full_name varchar(100) not null,
                                        email varchar(100),
                                        birth_date date,
                                        gender varchar(20),
                                        medical_conditions text,
                                        allergies text,
                                        medications text,
                                        approx_address varchar(150),
                                        city varchar(80),

                                        emergency_contact_name varchar(100),
                                        emergency_contact_phone varchar(30),
                                        emergency_contact_email varchar(100),
                                        relationship varchar(100),

                                        qr_token varchar(64) not null unique,
                                        pin_code varchar(12),

                                        created_at timestamp default now()
);
create index if not exists idx_profiles_qr_token on profiles(qr_token);

-- Bitácora (care notes)
create table if not exists care_note (
                                         id bigserial primary key,
                                         profile_id bigint not null references profiles(id) on delete cascade,
                                         text text not null,
                                         created_at timestamp not null default now()
);
create index if not exists idx_care_note_profile_created
    on care_note(profile_id, created_at desc);

-- (Reserva para recordatorios si los vas a persistir pronto)
-- create table reminder ( ... );

-- --------- DATOS DE PRUEBA PARA DEMO ---------
insert into profiles (
    full_name, email, birth_date, gender, medical_conditions, allergies, medications,
    approx_address, city, emergency_contact_name, emergency_contact_phone,
    emergency_contact_email, relationship, qr_token, pin_code
) values
      ('María López', 'maria@example.com', '1945-04-12', 'F',
       'Hipertensión', 'Penicilina', 'Enalapril 10mg',
       'C/ Olivo 12', 'Málaga', 'Ana López', '+34 600 111 222',
       'ana@example.com', 'Hija', 'QR_ABC123', '4321'),

      ('José Ruiz', 'jose@example.com', '1939-10-03', 'M',
       'Diabetes tipo 2', 'Nueces', 'Metformina 850mg',
       'Av. del Parque s/n', 'Sevilla', 'Carlos Ruiz', '+34 600 333 444',
       'carlos@example.com', 'Hijo', 'QR_DEF456', '2468');

insert into care_note (profile_id, text) values
                                             ((select id from profiles where qr_token='QR_ABC123'),
                                              '14:30 – Comió bien y tomó la medicación'),
                                             ((select id from profiles where qr_token='QR_DEF456'),
                                              '09:00 – Glucemia correcta antes del desayuno');
