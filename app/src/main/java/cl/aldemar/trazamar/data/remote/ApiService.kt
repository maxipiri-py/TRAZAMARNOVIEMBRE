package cl.aldemar.trazamar.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * ApiService = el "menú" de endpoints del backend Spring Boot (igual que en la Guía 14).
 * Cada función es una petición HTTP. Son "suspend" porque se ejecutan en una corrutina
 * (en segundo plano), sin congelar la pantalla.
 * Si el backend responde con error (401, 403, 409...), Retrofit lanza una HttpException.
 */
interface ApiService {

    // ---------- Autenticación ----------
    @POST("auth/login")
    suspend fun login(@Body request: LoginRemotoRequest): LoginRemotoResponse

    // ---------- Catálogos ----------
    @GET("catalogos/centros")
    suspend fun centros(): List<CentroRemoto>

    @GET("catalogos/trenes")
    suspend fun trenes(): List<TrenRemoto>

    @GET("catalogos/lineas")
    suspend fun lineas(): List<LineaRemota>

    // ---------- Muestras ----------
    @GET("muestras")
    suspend fun muestras(): List<MuestraRemota>

    @GET("muestras/{id}")
    suspend fun muestra(@Path("id") id: Int): MuestraRemota

    @POST("muestras")
    suspend fun crearMuestra(@Body request: MuestraRemotaRequest): MuestraRemota

    @PUT("muestras/{id}")
    suspend fun actualizarMuestra(@Path("id") id: Int, @Body request: MuestraRemotaRequest): MuestraRemota

    @POST("muestras/{id}/enviar")
    suspend fun enviarMuestra(@Header("Authorization") token: String, @Path("id") id: Int): MuestraRemota

    @PATCH("muestras/{id}/revision")
    suspend fun revisarMuestra(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: RevisionRemotaRequest,
    ): MuestraRemota
}
