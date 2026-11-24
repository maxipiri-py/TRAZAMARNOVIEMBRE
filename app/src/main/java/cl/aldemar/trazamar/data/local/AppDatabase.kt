package cl.aldemar.trazamar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.aldemar.trazamar.data.local.dao.CatalogoDao
import cl.aldemar.trazamar.data.local.dao.MuestraDao
import cl.aldemar.trazamar.data.local.dao.UsuarioDao
import cl.aldemar.trazamar.data.local.entities.CentroEntity
import cl.aldemar.trazamar.data.local.entities.LineaEntity
import cl.aldemar.trazamar.data.local.entities.MuestraEntity
import cl.aldemar.trazamar.data.local.entities.TrenEntity
import cl.aldemar.trazamar.data.local.entities.UsuarioEntity

@Database(
    entities = [MuestraEntity::class, CentroEntity::class, TrenEntity::class, LineaEntity::class, UsuarioEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun muestraDao(): MuestraDao
    abstract fun catalogoDao(): CatalogoDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE centros_cache ADD COLUMN activo INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE trenes_cache ADD COLUMN activo INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE lineas_cache ADD COLUMN activo INTEGER NOT NULL DEFAULT 1")
                db.execSQL("CREATE TABLE IF NOT EXISTS usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, username TEXT NOT NULL, nombreCompleto TEXT NOT NULL, rol TEXT NOT NULL, passwordHash TEXT NOT NULL, activo INTEGER NOT NULL, createdAt TEXT NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_usuarios_username ON usuarios(username)")
            }
        }

        // v3: columnas para sincronizar con el backend (código único e id del servidor)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE muestras_cache ADD COLUMN codigo TEXT")
                db.execSQL("ALTER TABLE muestras_cache ADD COLUMN servidorId INTEGER")
            }
        }

        fun getInstance(context: Context): AppDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "trazamar.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build().also { instancia = it }
        }
    }
}
