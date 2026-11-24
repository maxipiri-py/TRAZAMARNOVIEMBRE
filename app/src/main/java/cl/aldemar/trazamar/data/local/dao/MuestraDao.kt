package cl.aldemar.trazamar.data.local.dao

import androidx.room.*
import cl.aldemar.trazamar.data.local.entities.MuestraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MuestraDao {

    @Query("SELECT * FROM muestras_cache ORDER BY fecha DESC, id DESC")
    fun observarTodas(): Flow<List<MuestraEntity>>

    @Query("SELECT * FROM muestras_cache ORDER BY fecha DESC, id DESC")
    suspend fun obtenerTodasSync(): List<MuestraEntity>

    @Query("SELECT * FROM muestras_cache WHERE estado = :estado ORDER BY fecha DESC")
    fun observarPorEstado(estado: String): Flow<List<MuestraEntity>>

    @Query("SELECT COALESCE(MAX(id), 0) + 1 FROM muestras_cache WHERE id > 0")
    suspend fun siguienteId(): Int

    @Query("SELECT * FROM muestras_cache WHERE id = :id")
    suspend fun obtenerPorId(id: Int): MuestraEntity?

    // Muestras guardadas en el teléfono que todavía no llegan al backend
    @Query("SELECT * FROM muestras_cache WHERE sincronizada = 0")
    suspend fun pendientes(): List<MuestraEntity>

    @Query("SELECT COUNT(*) FROM muestras_cache WHERE sincronizada = 0")
    fun observarPendientes(): Flow<Int>

    @Query("SELECT * FROM muestras_cache WHERE codigo = :codigo LIMIT 1")
    suspend fun porCodigo(codigo: String): MuestraEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(muestras: List<MuestraEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(muestra: MuestraEntity)

    @Query("DELETE FROM muestras_cache")
    suspend fun limpiar()
}
