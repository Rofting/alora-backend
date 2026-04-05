<div align="center">

#  Alora Backend API

**El ecosistema inteligente para el cuidado de personas dependientes.**

[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL 16](https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker Ready](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![JWT Security](https://img.shields.io/badge/Security-JWT-black?style=for-the-badge&logo=jsonwebtokens)](https://jwt.io/)

*Alora externaliza la urgencia y centraliza el cuidado mediante una API RESTful segura, conectando una App nativa Android (offline-first) con un visor web de emergencias (PWA).*

[Explorar Documentación](#-documentación-api) · [Reportar Bug](#) · [Probar Localmente](#-despliegue-local)

</div>

---

##  Sobre el Proyecto

**Alora** nace con la premisa de humanizar el cuidado geriátrico y de personas dependientes. Esta API actúa como el cerebro central que orquesta la persistencia de datos médicos sensibles, la autenticación criptográfica y la generación de una **"Tarjeta Vital" (QR)**.

El sistema resuelve un problema crítico: permitir que los servicios de emergencia accedan a datos vitales (alergias, medicación) al instante, sin sacrificar la privacidad del paciente, mediante un sistema de desbloqueo multinivel.

###  Funcionalidades Core
- **Seguridad Criptográfica:** Autenticación mediante JSON Web Tokens (JWT) y protección de rutas.
- **Tarjeta Vital Dinámica:** Generación de tokens únicos para códigos QR que enlazan al perfil público/privado del paciente.
- **Bitácora Multi-cuidador:** Endpoints para la sincronización transaccional de hitos diarios y toma de medicación.
- **Soporte Offline-First:** API diseñada para resolver sincronizaciones destructivas desde clientes móviles con almacenamiento en caché (Room/SQLite).

---

## Stack Tecnológico

La arquitectura ha sido diseñada priorizando la solidez, la integridad referencial y la escalabilidad vertical:

| Categoría | Tecnologías |
| --- | --- |
| **Core & Framework** | Java 21, Spring Boot 3.x, Spring Web |
| **Persistencia** | PostgreSQL 16, Spring Data JPA, Hibernate 6, Flyway (Migraciones) |
| **Seguridad** | Spring Security, JWT (HMAC-SHA256) |
| **Testing** | JUnit 5, Mockito, MockMvc |
| **DevOps & Docs** | Docker Compose, Gradle, OpenAPI 3.1 (Swagger UI) |

---

## Arquitectura Modular

El dominio de la aplicación está estrictamente separado por responsabilidades para evitar el acoplamiento:

* **`auth`**: Controlador de acceso. Emisión y validación de tokens JWT.
* **`perfil`**: Gestión CRUD del historial clínico, contactos de emergencia y establecimiento del PIN criptográfico.
* **`bitacora`**: Lógica de negocio para los registros diarios y cronograma de medicación.
* **`publico`**: Módulo sin estado (stateless) y sin autenticación JWT, dedicado exclusivamente a renderizar los datos del QR y manejar el *challenge* del PIN.

*(Opcional: Reemplaza este texto por una imagen de tu diagrama de arquitectura)*
> `![Diagrama de Arquitectura](./docs/arquitectura.png)`

---

## Despliegue Local (Quick Start)

Levantar la infraestructura de Alora toma menos de 5 minutos gracias a Docker.

### 1. Prerrequisitos
- [Java 21 JDK](https://adoptium.net/)
- [Docker & Docker Compose](https://www.docker.com/)

### 2. Configuración del Entorno
Clona el repositorio y crea un archivo `.env` en el directorio raíz basándote en el archivo de ejemplo:

```bash
git clone [https://github.com/tu-usuario/alora-backend.git](https://github.com/tu-usuario/alora-backend.git)
cd alora-backend
