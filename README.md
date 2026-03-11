# Alora Backend API

> El "cerebro" central del sistema Alora, una plataforma de ayuda diseñada para dar tranquilidad a las familias y seguridad a las personas a su cargo.

Esta API RESTful gestiona la persistencia de datos, la seguridad y la comunicación bidireccional entre la App Android para familiares y la Página Web de Emergencia (PWA).

## Tecnologías Principales

El proyecto está construido bajo una arquitectura robusta y moderna, pensada para ser escalable y segura:

* **Lenguaje & Framework:** Java 21 y Spring Boot 3.x.
* **Base de Datos:** PostgreSQL 16 con migraciones automatizadas mediante Flyway.
* **Seguridad:** Spring Security implementando tokens JWT.
* **ORM:** JPA / Hibernate 6.
* **Documentación:** OpenAPI 3.1 / Swagger-UI.
* **Gestor de dependencias:** Gradle.
* **Entorno:** Docker Compose para facilitar el despliegue local de la base de datos.

## Arquitectura y Módulos

El backend está organizado en módulos funcionales para mantener el código ordenado y fácil de mantener:

* **`auth` (Autenticación):** Gestiona el registro, el login con JWT y actúa como el "portero" que protege el acceso a los datos.
* **`perfil` (Perfiles):** El corazón del proyecto. Maneja los datos de contacto, información médica (alergias, medicación) y los PIN de seguridad de las personas cuidadas.
* **`bitacora` (Diario):** Administra el diario de cuidados y la programación de los recordatorios de medicación.
* **`publico` (Acceso PWA):** Módulo expuesto sin login, encargado de atender las peticiones al escanear el QR (`/public/qr/{token}`) y gestionar el desbloqueo con PIN (`/public/unlock`).

## Variables de Entorno

Para levantar el proyecto en local, necesitarás configurar las siguientes variables de entorno (puedes crear un archivo `.env` o configurarlas en tu IDE):

```env
# Ejemplo de configuración
DB_URL=jdbc:postgresql://localhost:5432/alora_db
DB_USER=tu_usuario
DB_PASSWORD=tu_contraseña
JWT_SECRET=tu_clave_secreta_super_segura
