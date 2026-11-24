# TrazaMar — AquaSample (ALDEMAR SpA)

App móvil y backend para el **registro, conteo y trazabilidad de muestras de choritos** por centro, tren, línea y fecha.
DSY1105 · Sección 04V · Equipo **NotFound-Devs**. Todos los datos son ficticios.

## Integrantes
| Integrante | Rol en el proyecto |
|---|---|
| Matías Sandoval (Matias-SR) | Líder · Desarrollo FullStack (app Android) |
| Yonathan Machado (Yona36) | Backend y documentación |
| Maximiliano Cáceres (maxipiri-py) | Base de datos · backend Spring Boot · integración Retrofit |

## Problemática
El operador cuenta choritos a mano y registra los datos en planillas, fotos y notas por separado. Eso genera diferencias de conteo, registros incompletos y reportes lentos de armar.
TrazaMar centraliza el registro con fotografía, el flujo de revisión por rol y el historial por línea de cultivo, y funciona sin conexión en terreno.

## Arquitectura
```
App Android (Kotlin + Jetpack Compose + Material 3, MVVM)
   │  Room (SQLite local, offline first)
   │  Retrofit + Gson
   ▼
Backend Spring Boot 3 (REST + Swagger/OpenAPI)
   │  Spring Data JPA
   ▼
SQLite (trazamar.db)
```
- **Offline first:** la app guarda primero en Room y luego sincroniza con el backend. Sin red, la muestra queda pendiente y se sube después, sin duplicarse gracias a su `codigo` único.
- **Roles:** operador (registra y envía), supervisor (valida u observa), analista (consulta) y admin (usuarios y catálogos).
- **Estados de una muestra:** borrador → pendiente → observada → corregida → validada.

## Estructura del repositorio
```
app/                 App Android (cl.aldemar.trazamar)
trazamar-backend/    Backend Spring Boot + SQLite
docs/                Documentación del proyecto
```

## Backend: cómo ejecutarlo
Requisitos: Java 17 o superior.
1. Abrir `trazamar-backend/` en VS Code o IntelliJ.
2. Ejecutar `TrazamarBackendApplication` (o `mvn spring-boot:run`).
3. **Swagger UI:** http://localhost:8080/swagger-ui.html

La base `trazamar.db` se crea automáticamente con datos de ejemplo.
Usuarios demo (contraseña `1234`): `OP-01`, `OP-02`, `SUP-01`, `AN-01`, `ADMIN-01`.

### Endpoints
| Método | Endpoint | Rol requerido |
|---|---|---|
| POST | /api/auth/login | — |
| GET | /api/auth/me | token |
| GET | /api/catalogos/centros · /trenes · /lineas | — |
| POST | /api/catalogos/centros · /trenes · /lineas | admin |
| GET | /api/muestras (filtros: estado, centroId, lineaId) | — |
| GET | /api/muestras/{id} | — |
| POST | /api/muestras | — |
| PUT | /api/muestras/{id} | — (solo en borrador u observada) |
| DELETE | /api/muestras/{id} | operador, admin |
| POST | /api/muestras/{id}/enviar | operador, admin |
| PATCH | /api/muestras/{id}/revision | supervisor, admin |
| GET / POST | /api/usuarios | admin |
| PATCH | /api/usuarios/{id}/activo | admin |

El token se envía como `Authorization: Bearer <token>` (botón **Authorize** en Swagger).

## App Android: cómo ejecutarla
1. Levantar primero el backend.
2. Abrir la raíz del repositorio en Android Studio y ejecutar ▶.
3. En el emulador, la app se conecta a `http://10.0.2.2:8080/api/`. Para un teléfono físico, cambiar `BASE_URL` en `app/src/main/java/cl/aldemar/trazamar/data/remote/RetrofitInstance.kt` por la IP del PC.

### Instalar la APK
1. Descargar la APK release desde AVA o desde la sección Releases.
2. En el teléfono, permitir "instalar apps desconocidas" e instalarla.

## Pruebas
- Pruebas unitarias en `app/src/test` (`./gradlew test`).
- Evidencia de cobertura: *(pendiente)*.

## Tecnologías
Kotlin · Jetpack Compose · Material 3 · MVVM · Room · DataStore · Retrofit · Gson · Coil · Spring Boot · Spring Data JPA · SQLite · springdoc-openapi (Swagger)
