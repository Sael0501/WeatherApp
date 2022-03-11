package mx.kodemia.weatherappsael.network

import mx.kodemia.weatherappsael.model.OneCall
import mx.kodemia.weatherappsael.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Weather {
    @GET("data/2.5/onecall")
    suspend fun getWeatherById(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String?,
        @Query("lang") lang: String?, //Para el idioma
        @Query("appid") appid: String
    ): Response<OneCall>
}