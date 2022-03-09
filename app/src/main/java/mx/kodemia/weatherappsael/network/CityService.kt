package mx.kodemia.weatherappsael.network

import mx.kodemia.weatherappsael.model.City
import retrofit2.http.GET
import retrofit2.http.Query

interface CityService {
    @GET("geo/1.0/reverse")
    suspend fun getCitiesByLatLng(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String
    ): List<City>
}