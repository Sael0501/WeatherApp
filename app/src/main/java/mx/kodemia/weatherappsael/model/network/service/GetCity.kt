package mx.kodemia.weatherapp.network.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.kodemia.weatherappsael.model.CityEntity
import mx.kodemia.weatherappsael.model.network.City
import mx.kodemia.weatherappsael.model.network.RetrofitInstance
import retrofit2.Response

class GetCity {

    //Se instancia el servicio de retrofit con la peticion de la Ciudad
    private val retrofit = RetrofitInstance.getRetrofit().create(City::class.java)

    suspend fun getCityService(lat: String, lon: String, appid: String): Response<List<CityEntity>> {
        return withContext(Dispatchers.IO){
            val response = retrofit.getCitiesByLatLng(lat, lon, appid)
            response
        }
    }

}