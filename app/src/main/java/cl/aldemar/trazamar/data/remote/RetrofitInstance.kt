package cl.aldemar.trazamar.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton con la configuración de Retrofit (igual que RetrofitInstance de la Guía 14).
 * "by lazy" = el cliente se crea UNA sola vez, la primera vez que se usa.
 */
object RetrofitInstance {

    /**
     * Dirección del backend Spring Boot.
     * - Emulador Android: 10.0.2.2 es el "localhost" de tu PC.
     * - Teléfono físico: cambia por la IP de tu PC en la red WiFi (ej: http://192.168.1.15:8080/api/).
     */
    const val BASE_URL = "http://10.0.2.2:8080/api/"

    // Tiempos cortos: en terreno sin señal no queremos que el usuario espere mucho
    private val cliente = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create()) // JSON <-> objetos Kotlin
            .build()
            .create(ApiService::class.java)
    }
}
