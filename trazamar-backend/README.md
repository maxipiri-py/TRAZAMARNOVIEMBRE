# TrazaMar Backend (Spring Boot + SQLite + Swagger)

Backend de NotFound-Devs para AquaSample (ALDEMAR SpA). Datos ficticios.

## Requisitos
- Java 17 o superior
- IntelliJ IDEA (trae Maven) o Maven instalado

## Ejecutar
1. Abrir la carpeta en IntelliJ → esperar que descargue las dependencias de Maven.
2. Ejecutar `TrazamarBackendApplication` (botón ▶).
   - O por terminal: `mvn spring-boot:run`
3. Swagger UI: http://localhost:8080/swagger-ui.html
4. Desde el emulador Android: `http://10.0.2.2:8080/api/`

La base `trazamar.db` (SQLite) se crea sola y carga 3 muestras de ejemplo.

## Endpoints de muestras
| Método | URL | Qué hace |
|---|---|---|
| GET | /api/muestras?estado=&centroId=&lineaId= | Listar (filtros opcionales) |
| GET | /api/muestras/{id} | Obtener una |
| POST | /api/muestras | Crear (queda en borrador) |
| PUT | /api/muestras/{id} | Editar (solo borrador u observada) |
| DELETE | /api/muestras/{id} | Eliminar (solo borrador) |
| POST | /api/muestras/{id}/enviar | Enviar a revisión |
| PATCH | /api/muestras/{id}/revision | Supervisor valida u observa |

Flujo de estados: borrador → pendiente → observada → corregida → validada.
