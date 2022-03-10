package mx.kodemia.weatherapp.network.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.kodemia.weatherappsael.model.City
import mx.kodemia.weatherappsael.network.CityService
import mx.kodemia.weatherappsael.network.RetrofitInstance
import retrofit2.Response

class GetCity {

    //Se instancia el servicio de retrofit con la peticion de la Ciudad
    private val retrofit = RetrofitInstance.getRetrofit().create(CityService::class.java)

    //Se crea la funcion para mandar la peticion con los parametros necesarios para realizarla
    //Con un tipo de retorno del modelo de la respuesta
    suspend fun getCityService(lat: String, lon: String, appid: String): List<City> {
        return withContext(Dispatchers.IO){
            val response = retrofit.getCitiesByLatLng(lat, lon, appid)
            response
        }
    }

}