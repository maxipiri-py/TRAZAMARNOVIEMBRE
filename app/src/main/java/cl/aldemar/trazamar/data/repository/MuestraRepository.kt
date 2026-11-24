package cl.aldemar.trazamar.data.repository

import android.content.Context
import cl.aldemar.trazamar.data.local.AppDatabase
import cl.aldemar.trazamar.data.local.entities.MuestraEntity
import cl.aldemar.trazamar.data.model.dto.*
import cl.aldemar.trazamar.data.remote.ApiService
import cl.aldemar.trazamar.data.remote.MuestraRemota
import cl.aldemar.trazamar.data.remote.MuestraRemotaRequest
import cl.aldemar.trazamar.data.remote.RetrofitInstance
import cl.aldemar.trazamar.data.remote.RevisionRemotaRequest
import cl.aldemar.trazamar.data.session.SessionManager
import cl.aldemar.trazamar.util.Reglas
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.UUID

/**
 * Repositorio de muestras: decide de DÓNDE salen y a DÓNDE van los datos.
 *
 * Estrategia "offline first" (primero el teléfono):
 *  1. Todo se guarda primero en Room (SQLite del teléfono) marcado como sincronizada = false.
 *  2. Luego se intenta subir al backend Spring Boot con Retrofit (sincronizarPendientes()).
 *  3. Si no hay internet, la muestra queda "Pendiente de sincronización" y se sube después.
 * Las pantallas (ViewModels) no saben nada de esto: solo le piden datos al repositorio.
 */
class MuestraRepository(
    private val context: Context,
    private val api: ApiService = RetrofitInstance.api,
) {
    private val db = AppDatabase.getInstance(context)
    private val dao = db.muestraDao()
    private val catalogos = db.catalogoDao()
    private val session = SessionManager(context)

    fun observarCache(): Flow<List<MuestraEntity>> = dao.observarTodas()

    // Cuántas muestras faltan por subir (para mostrar "Pendiente de sincronización")
    fun observarPendientes(): Flow<Int> = dao.observarPendientes()

    // ======================= SINCRONIZACIÓN CON EL BACKEND =======================

    /**
     * Sube al backend todas las muestras pendientes. Devuelve cuántas se sincronizaron.
     * Si no hay conexión, simplemente quedan pendientes (no se pierde nada).
     * Esta misma función la usará WorkManager para sincronizar al volver internet.
     */
    suspend fun sincronizarPendientes(): Int {
        var sincronizadas = 0
        val token = "Bearer ${session.sesionActual().token ?: ""}"
        for (local in dao.pendientes()) {
            if (local.codigo == null) continue // muestras antiguas, creadas antes de existir el backend
            try {
                // 1) Si nunca se subió, se crea en el backend (si ya existía ese código, el backend devuelve la misma)
                var remota: MuestraRemota = if (local.servidorId == null) {
                    api.crearMuestra(local.aRequest())
                } else {
                    api.muestra(local.servidorId)
                }
                val idServidor = remota.id

                // 2) Si en el backend todavía se puede editar, se suben los datos actualizados (conteo, peso, etc.)
                if (remota.estado == "borrador" || remota.estado == "observada") {
                    remota = api.actualizarMuestra(idServidor, local.aRequest())
                }

                // 3) Se igualan los estados de revisión
                val enviadaEnTelefono = local.estado == "pendiente" || local.estado == "corregida"
                if (enviadaEnTelefono && (remota.estado == "borrador" || remota.estado == "observada")) {
                    remota = api.enviarMuestra(token, idServidor)
                }
                val revisadaEnTelefono = local.estado == "validada" || local.estado == "observada"
                if (revisadaEnTelefono && (remota.estado == "pendiente" || remota.estado == "corregida")) {
                    remota = api.revisarMuestra(token, idServidor, RevisionRemotaRequest(local.estado, local.comentarioSupervisor))
                }

                dao.insertar(local.copy(servidorId = idServidor, sincronizada = true))
                sincronizadas++
            } catch (e: Exception) {
                // Sin internet o el backend rechazó el cambio: la muestra sigue pendiente
            }
        }
        return sincronizadas
    }

    /** Trae las muestras del backend y las guarda/actualiza en Room (sin pisar cambios locales pendientes). */
    private suspend fun descargarDelBackend() {
        try {
            val centros = catalogos.obtenerCentros().associateBy { it.id }
            val trenes = catalogos.obtenerTrenes().associateBy { it.id }
            val lineas = catalogos.obtenerLineas().associateBy { it.id }
            for (r in api.muestras()) {
                val local = dao.porCodigo(r.codigo)
                if (local != null && !local.sincronizada) continue // hay cambios locales sin subir: se respetan
                dao.insertar(
                    MuestraEntity(
                        id = local?.id ?: dao.siguienteId(),
                        centroId = r.centroId, centroNombre = centros[r.centroId]?.nombre,
                        trenId = r.trenId, trenNombre = trenes[r.trenId]?.nombre,
                        lineaId = r.lineaId, lineaNombre = lineas[r.lineaId]?.nombre,
                        fecha = r.fecha, hora = r.hora, tramoMetros = r.tramoMetros,
                        operadorId = r.operadorId, operadorNombre = r.operadorNombre,
                        conteoIndividuos = r.conteoIndividuos, observaciones = r.observaciones,
                        calibrePromedio = r.calibrePromedio, calibreMin = r.calibreMin, calibreMax = r.calibreMax,
                        pesoMuestra = r.pesoMuestra,
                        fotoUrl = local?.fotoUrl ?: r.fotoUrl, // la foto vive en el teléfono
                        estado = r.estado, comentarioSupervisor = r.comentarioSupervisor,
                        supervisorNombre = local?.supervisorNombre,
                        sincronizada = true, codigo = r.codigo, servidorId = r.id,
                    )
                )
            }
        } catch (e: Exception) {
            // Sin conexión: se muestra lo que ya está guardado en el teléfono
        }
    }

    // ======================= OPERACIONES QUE USAN LAS PANTALLAS =======================

    suspend fun sincronizarHistorial(
        centroId: Int? = null, trenId: Int? = null, lineaId: Int? = null,
        estado: String? = null, operadorId: Int? = null,
        fechaDesde: String? = null, fechaHasta: String? = null,
    ): ResultadoApi<List<MuestraDto>> = try {
        sincronizarPendientes()   // primero subimos lo pendiente
        descargarDelBackend()     // luego bajamos lo nuevo del backend
        val resultado = dao.obtenerTodasSync().filter { m ->
            (centroId == null || m.centroId == centroId) &&
            (trenId == null || m.trenId == trenId) &&
            (lineaId == null || m.lineaId == lineaId) &&
            (estado.isNullOrBlank() || m.estado.equals(estado, true)) &&
            (operadorId == null || m.operadorId == operadorId) &&
            (fechaDesde.isNullOrBlank() || m.fecha >= fechaDesde) &&
            (fechaHasta.isNullOrBlank() || m.fecha <= fechaHasta)
        }.map { it.aDto() }
        ResultadoApi.Exito(resultado)
    } catch (e: Exception) { ResultadoApi.Error("No se pudo consultar el historial: ${e.message}") }

    suspend fun obtenerMuestra(id: Int): ResultadoApi<MuestraDto> =
        dao.obtenerPorId(id)?.let { ResultadoApi.Exito(it.aDto()) } ?: ResultadoApi.Error("Muestra no encontrada.")

    suspend fun crearMuestra(request: CrearMuestraRequest): ResultadoApi<MuestraDto> { return try {
        val centro = catalogos.obtenerCentros().firstOrNull { it.id == request.centroId }
        val tren = catalogos.obtenerTrenes().firstOrNull { it.id == request.trenId }
        val linea = catalogos.obtenerLineas().firstOrNull { it.id == request.lineaId }
        if (centro == null || tren == null || linea == null || tren.centroId != centro.id || linea.trenId != tren.id)
            return ResultadoApi.Error("Selecciona un centro, tren y línea válidos.")
        val sesion = session.sesionActual()
        val entity = MuestraEntity(
            id = dao.siguienteId(), centroId = request.centroId, centroNombre = centro.nombre,
            trenId = request.trenId, trenNombre = tren.nombre, lineaId = request.lineaId,
            lineaNombre = linea.nombre, fecha = request.fecha, hora = request.hora,
            tramoMetros = request.tramoMetros, operadorId = request.operadorId,
            operadorNombre = sesion.nombreCompleto, conteoIndividuos = request.conteoIndividuos,
            observaciones = request.observaciones, calibrePromedio = request.calibrePromedio,
            calibreMin = request.calibreMin, calibreMax = request.calibreMax,
            pesoMuestra = request.pesoMuestra, fotoUrl = null, estado = "borrador",
            comentarioSupervisor = null, supervisorNombre = null,
            sincronizada = false, codigo = UUID.randomUUID().toString(), servidorId = null,
        )
        dao.insertar(entity)       // 1) se guarda en el teléfono
        sincronizarPendientes()    // 2) se intenta subir al backend
        ResultadoApi.Exito((dao.obtenerPorId(entity.id) ?: entity).aDto())
    } catch (e: Exception) { ResultadoApi.Error("No se pudo guardar la muestra: ${e.message}") } }

    suspend fun actualizarMuestra(id: Int, request: ActualizarMuestraRequest): ResultadoApi<MuestraDto> { return try {
        val old = dao.obtenerPorId(id) ?: return ResultadoApi.Error("No se encontró la muestra local.")
        val updated = old.copy(
            conteoIndividuos = request.conteoIndividuos ?: old.conteoIndividuos,
            observaciones = request.observaciones ?: old.observaciones,
            calibrePromedio = request.calibrePromedio ?: old.calibrePromedio,
            calibreMin = request.calibreMin ?: old.calibreMin,
            calibreMax = request.calibreMax ?: old.calibreMax,
            pesoMuestra = request.pesoMuestra ?: old.pesoMuestra,
            tramoMetros = request.tramoMetros ?: old.tramoMetros,
            sincronizada = false,
        )
        dao.insertar(updated)
        sincronizarPendientes()
        ResultadoApi.Exito((dao.obtenerPorId(id) ?: updated).aDto())
    } catch (e: Exception) { ResultadoApi.Error("No se pudo actualizar la muestra: ${e.message}") } }

    // Usa la regla del negocio: si estaba "observada" pasa a "corregida"; si no, a "pendiente"
    suspend fun enviarARevision(id: Int): ResultadoApi<MuestraDto> {
        val old = dao.obtenerPorId(id) ?: return ResultadoApi.Error("Muestra no encontrada.")
        val updated = old.copy(estado = Reglas.estadoAlEnviar(old.estado), sincronizada = false)
        dao.insertar(updated)
        sincronizarPendientes()
        return ResultadoApi.Exito((dao.obtenerPorId(id) ?: updated).aDto())
    }

    suspend fun subirFoto(id: Int, archivo: File): ResultadoApi<String> {
        val old = dao.obtenerPorId(id) ?: return ResultadoApi.Error("Muestra no encontrada.")
        if (!archivo.exists()) return ResultadoApi.Error("No se encontró el archivo de fotografía.")
        // La foto se guarda en el almacenamiento privado del teléfono; Room guarda su ruta
        dao.insertar(old.copy(fotoUrl = archivo.absolutePath))
        return ResultadoApi.Exito(archivo.absolutePath)
    }

    suspend fun revisar(id: Int, estado: String, comentario: String?): ResultadoApi<MuestraDto> { return try {
        if (estado !in setOf("observada", "corregida", "validada", "pendiente")) return ResultadoApi.Error("Estado de revisión no válido.")
        val old = dao.obtenerPorId(id) ?: return ResultadoApi.Error("Muestra no encontrada.")
        // El supervisor que revisa es el que inició sesión
        val supervisor = session.sesionActual().nombreCompleto
        val updated = old.copy(estado = estado, comentarioSupervisor = comentario, supervisorNombre = supervisor, sincronizada = false)
        dao.insertar(updated)
        sincronizarPendientes()
        ResultadoApi.Exito((dao.obtenerPorId(id) ?: updated).aDto())
    } catch (e: Exception) { ResultadoApi.Error("No se pudo revisar la muestra: ${e.message}") } }
}

// Convierte la muestra del teléfono en el JSON que espera el backend
private fun MuestraEntity.aRequest() = MuestraRemotaRequest(
    codigo = codigo ?: "", centroId = centroId, trenId = trenId, lineaId = lineaId,
    fecha = fecha, hora = hora, tramoMetros = tramoMetros, operadorId = operadorId,
    operadorNombre = operadorNombre, conteoIndividuos = conteoIndividuos, observaciones = observaciones,
    calibrePromedio = calibrePromedio, calibreMin = calibreMin, calibreMax = calibreMax,
    pesoMuestra = pesoMuestra, fotoUrl = null,
)

private fun MuestraEntity.aDto() = MuestraDto(
    id = id, centroId = centroId, trenId = trenId, lineaId = lineaId,
    fecha = fecha, hora = hora, tramoMetros = tramoMetros, operadorId = operadorId,
    conteoIndividuos = conteoIndividuos, observaciones = observaciones,
    calibrePromedio = calibrePromedio, calibreMin = calibreMin, calibreMax = calibreMax,
    pesoMuestra = pesoMuestra, fotoUrl = fotoUrl, estado = estado,
    comentarioSupervisor = comentarioSupervisor, supervisorId = null, fechaRevision = null,
    centro = centroNombre?.let { CentroDto(centroId, it, "", true, null) },
    tren = trenNombre?.let { TrenDto(trenId, it, centroId, true, null) },
    linea = lineaNombre?.let { LineaDto(lineaId, it, trenId, true) },
    operador = operadorNombre?.let { UsuarioDto(operadorId, "", it, "operador", true, null) },
    supervisor = supervisorNombre?.let { UsuarioDto(0, "", it, "supervisor", true, null) },
)
