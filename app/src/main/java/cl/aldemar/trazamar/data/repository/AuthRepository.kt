package cl.aldemar.trazamar.data.repository

import android.content.Context
import cl.aldemar.trazamar.data.local.AppDatabase
import cl.aldemar.trazamar.data.local.entities.UsuarioEntity
import cl.aldemar.trazamar.data.model.dto.UsuarioDto
import cl.aldemar.trazamar.data.remote.ApiService
import cl.aldemar.trazamar.data.remote.LoginRemotoRequest
import cl.aldemar.trazamar.data.remote.RetrofitInstance
import cl.aldemar.trazamar.data.session.SessionManager
import cl.aldemar.trazamar.data.session.SesionUsuario
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import java.security.MessageDigest
import java.time.LocalDateTime

sealed class ResultadoApi<out T> {
    data class Exito<T>(val datos: T) : ResultadoApi<T>()
    data class Error(val mensaje: String) : ResultadoApi<Nothing>()
}

internal fun hashPassword(password: String): String = MessageDigest.getInstance("SHA-256")
    .digest(password.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

internal fun UsuarioEntity.aUsuarioDto() = UsuarioDto(id, username, nombreCompleto, rol, activo, createdAt)

/**
 * Login por rol.
 * 1) Primero intenta iniciar sesión en el backend Spring Boot (Retrofit) y guarda el token.
 * 2) Si NO hay conexión (IOException), entra con los usuarios guardados en el teléfono (modo terreno offline).
 * 3) Si el backend responde 401 (clave mala), NO se usa el modo offline: se muestra el error.
 */
class AuthRepository(
    private val context: Context,
    private val api: ApiService = RetrofitInstance.api,
) {
    private val dao = AppDatabase.getInstance(context).usuarioDao()
    private val session = SessionManager(context)

    suspend fun prepararDatosIniciales() {
        if (dao.contar() == 0) {
            val now = LocalDateTime.now().toString()
            listOf(
                Triple("OP-01", "Operador Uno", "operador"),
                Triple("OP-02", "Operador Dos", "operador"),
                Triple("SUP-01", "Supervisor Uno", "supervisor"),
                Triple("AN-01", "Analista Uno", "analista"),
                Triple("ADMIN-01", "Administrador Uno", "admin"),
            ).forEach { (username, nombre, rol) ->
                dao.insertar(UsuarioEntity(username = username, nombreCompleto = nombre, rol = rol, passwordHash = hashPassword("1234"), createdAt = now))
            }
        }
    }

    suspend fun login(username: String, password: String): ResultadoApi<String> {
        if (username.isBlank() || password.isBlank()) return ResultadoApi.Error("Ingresa usuario y contraseña.")
        return try {
            // ---------- 1) Login en el backend ----------
            val respuesta = api.login(LoginRemotoRequest(username.trim(), password))
            val u = respuesta.usuario
            session.guardarSesion(respuesta.token, u.id, u.username, u.nombreCompleto, u.rol)
            ResultadoApi.Exito(u.rol)
        } catch (e: HttpException) {
            // El backend respondió, pero rechazó el login (401 = credenciales incorrectas)
            if (e.code() == 401) ResultadoApi.Error("Usuario o contraseña incorrectos, o cuenta desactivada.")
            else ResultadoApi.Error("El servidor respondió con un error (${e.code()}).")
        } catch (e: IOException) {
            // ---------- 2) Sin conexión: login local (offline) ----------
            loginLocal(username, password)
        }
    }

    private suspend fun loginLocal(username: String, password: String): ResultadoApi<String> {
        prepararDatosIniciales()
        val usuario = dao.porUsername(username.trim())
        if (usuario == null || !usuario.activo || usuario.passwordHash != hashPassword(password)) {
            return ResultadoApi.Error("Sin conexión: usuario o contraseña incorrectos en el modo offline.")
        }
        session.guardarSesion("local_${usuario.id}", usuario.id, usuario.username, usuario.nombreCompleto, usuario.rol)
        return ResultadoApi.Exito(usuario.rol)
    }

    suspend fun cerrarSesion() = session.cerrarSesion()
    val sesion: Flow<SesionUsuario> get() = session.sesion
}
