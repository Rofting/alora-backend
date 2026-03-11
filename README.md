# 🧠 Alora Backend API

> [cite_start]El "cerebro" central del sistema Alora, una plataforma de ayuda diseñada para dar tranquilidad a las familias y seguridad a las personas a su cargo[cite: 11, 23].

[cite_start]Esta API RESTful gestiona la persistencia de datos, la seguridad y la comunicación bidireccional entre la App Android para familiares y la Página Web de Emergencia (PWA)[cite: 24].

## 🚀 Tecnologías Principales

El proyecto está construido bajo una arquitectura robusta y moderna, pensada para ser escalable y segura:

* [cite_start]**Lenguaje & Framework:** Java 21 y Spring Boot 3.x[cite: 61].
* [cite_start]**Base de Datos:** PostgreSQL 16 [cite: 63] [cite_start]con migraciones automatizadas mediante Flyway[cite: 64].
* [cite_start]**Seguridad:** Spring Security implementando tokens JWT[cite: 62].
* [cite_start]**ORM:** JPA / Hibernate 6[cite: 62].
* [cite_start]**Documentación:** OpenAPI 3.1 / Swagger-UI.
* [cite_start]**Gestor de dependencias:** Gradle.
* [cite_start]**Entorno:** Docker Compose para facilitar el despliegue local de la base de datos[cite: 77].

## 🏗️ Arquitectura y Módulos

[cite_start]El backend está organizado en módulos funcionales para mantener el código ordenado y fácil de mantener[cite: 83, 84]:

* [cite_start]**`auth` (Autenticación):** Gestiona el registro, el login con JWT y actúa como el "portero" que protege el acceso a los datos[cite: 86, 87].
* **`perfil` (Perfiles):** El corazón del proyecto. [cite_start]Maneja los datos de contacto, información médica (alergias, medicación) y los PIN de seguridad de las personas cuidadas[cite: 88].
* [cite_start]**`bitacora` (Diario):** Administra el diario de cuidados y la programación de los recordatorios de medicación[cite: 89].
* [cite_start]**`publico` (Acceso PWA):** Módulo expuesto sin login, encargado de atender las peticiones al escanear el QR (`/public/qr/{token}`) y gestionar el desbloqueo con PIN (`/public/unlock`)[cite: 90, 91, 123].

## ⚙️ Variables de Entorno

Para levantar el proyecto en local, necesitarás configurar las siguientes variables de entorno (puedes crear un archivo `.env` o configurarlas en tu IDE):

```env
# Ejemplo de configuración
DB_URL=jdbc:postgresql://localhost:5432/alora_db
DB_USER=tu_usuario
DB_PASSWORD=tu_contraseña
JWT_SECRET=tu_clave_secreta_super_segura
