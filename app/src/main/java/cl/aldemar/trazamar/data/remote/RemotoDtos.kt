package cl.aldemar.trazamar.data.remote

/**
 * DTOs REMOTOS = la "forma" exacta del JSON que envía y recibe el backend Spring Boot.
 * Gson convierte automáticamente el JSON en estos objetos (y al revés),
 * siempre que los nombres de los campos coincidan con los del backend.
 */

// ---------- Login ----------
data class LoginRemotoRequest(val username: String, val password: String)

data class UsuarioRemoto(
    val id: Int,
    val username: String,
    val nombreCompleto: String,
    val rol: String,
    val activo: Boolean = true,
)

data class LoginRemotoResponse(val token: String, val usuario: UsuarioRemoto)

// ---------- Catálogos ----------
data class CentroRemoto(val id: Int, val nombre: String, val codigo: String, val activo: Boolean = true)
data class TrenRemoto(val id: Int, val nombre: String, val centroId: Int, val activo: Boolean = true)
data class LineaRemota(val id: Int, val nombre: String, val trenId: Int, val activo: Boolean = true)

// ---------- Muestras ----------
data class MuestraRemota(
    val id: Int,
    val codigo: String,
    val centroId: Int,
    val trenId: Int,
    val lineaId: Int,
    val fecha: String,
    val hora: String?,
    val tramoMetros: Double?,
    val operadorId: Int,
    val operadorNombre: String?,
    val conteoIndividuos: Int?,
    val observaciones: String?,
    val calibrePromedio: Double?,
    val calibreMin: Double?,
    val calibreMax: Double?,
    val pesoMuestra: Double?,
    val fotoUrl: String?,
    val estado: String,
    val comentarioSupervisor: String?,
)

// Lo que se envía al backend al crear o editar una muestra
data class MuestraRemotaRequest(
    val codigo: String,
    val centroId: Int,
    val trenId: Int,
    val lineaId: Int,
    val fecha: String,
    val hora: String?,
    val tramoMetros: Double?,
    val operadorId: Int,
    val operadorNombre: String?,
    val conteoIndividuos: Int?,
    val observaciones: String?,
    val calibrePromedio: Double?,
    val calibreMin: Double?,
    val calibreMax: Double?,
    val pesoMuestra: Double?,
    val fotoUrl: String?,
)

data class RevisionRemotaRequest(val estado: String, val comentario: String?)
