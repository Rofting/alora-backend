# Alora Backend

API REST del sistema **Alora**, una plataforma de gestión de cuidados geriátricos.
Forma parte de un proyecto multiplataforma compuesto por:

- **Este backend** — Spring Boot 3 + PostgreSQL
- **App Android** — cliente móvil para cuidadores
- **PWA** — generación de fichas médicas en PDF para emergencias

---

## Tecnologías

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.5 |
| Seguridad | Spring Security + JWT (jjwt 0.11) |
| Persistencia | Spring Data JPA + PostgreSQL |
| Migraciones | Flyway |
| IA | Google Gemini 2.5 Flash |
| QR | ZXing 3.5 |
| Tests | JUnit 5 + Mockito |
| Documentación | OpenAPI 3.0 (`openapi.yaml`) |

---

## Requisitos previos

- Java 21
- Docker (para levantar PostgreSQL) o una instancia local en el puerto `5432`
- Las dos variables de entorno descritas abajo

---

## Variables de entorno

| Variable | Descripción |
|----------|-------------|
| `JWT_SECRET` | Clave Base64 de mínimo 32 bytes para firmar los JWT |
| `GEMINI_API_KEY` | API key de Google AI Studio para el asistente IA |

### Cómo configurarlas en IntelliJ IDEA

1. `Run` → `Edit Configurations`
2. Selecciona la configuración de `AloraBackendApplication`
3. En el campo **Environment variables** añade:
   ```
   JWT_SECRET=dHUgY2xhdmUgc2VjcmV0YSBkZSBtaW5pbW8gMzIgYnl0ZXM=;GEMINI_API_KEY=tu_clave_aqui
   ```

### Generar un JWT_SECRET válido (PowerShell)

```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }) -as [byte[]])
```

---

## Arranque local

### 1. Levantar PostgreSQL con Docker

```bash
cd postgres
docker compose up -d
```

La base de datos se crea en `localhost:5432/alora` con usuario `postgres` / contraseña `postgres`.
Flyway aplica las migraciones automáticamente al arrancar la app.

### 2. Iniciar la aplicación

```bash
./gradlew bootRun
```

La API queda disponible en `http://localhost:8080`.

---

## Ejecutar los tests

```bash
./gradlew test
```

Los tests unitarios cubren `AuthService` y `CareLogService` con JUnit 5 + Mockito.

---

## Documentación de la API

El archivo `openapi.yaml` en la raíz del proyecto contiene la especificación completa.
Puedes importarlo en:

- **Swagger UI** — `https://editor.swagger.io`
- **Postman** — `Import` → selecciona `openapi.yaml`
- **Insomnia** — `Import/Export` → `Import Data`

### Autenticación

Todos los endpoints protegidos requieren el header:
```
Authorization: Bearer <token>
```

El token se obtiene en `POST /auth/register` o `POST /auth/login`.

---

## Endpoints principales

| Módulo | Ruta base | Auth |
|--------|-----------|------|
| Autenticación | `/auth/**` | Pública |
| Perfiles | `/api/profiles/**` | JWT |
| Registros de cuidado | `/api/profiles/{id}/logs` | JWT |
| Recordatorios | `/api/profiles/{id}/reminders` | JWT |
| Asistente IA | `/api/profiles/{id}/chat` | JWT |
| Perfil público (QR) | `/public/profile/**` | Pública |
| Administración | `/api/admin/**` | JWT + rol ADMIN |

Consulta `openapi.yaml` para la lista completa con parámetros y schemas.

---

## Estructura del proyecto

```
src/main/java/com/alora/
├── auth/           # Autenticación, usuarios, JWT
├── carelog/        # Registro de cuidados + asistente IA
├── config/         # Seguridad, filtros, beans globales
├── exception/      # Manejo centralizado de errores
├── profile/        # Perfiles de pacientes y QR
└── reminder/       # Recordatorios y alarmas
```

---

## Roles

| Rol | Descripción |
|-----|-------------|
| `USER` | Cuidador — puede gestionar sus propios perfiles y datos |
| `ADMIN` | Administrador — acceso a `/api/admin/**` |

Por defecto, todos los registros obtienen el rol `USER`.
Para promover a `ADMIN` usa `PATCH /api/admin/users/{id}/role`.
