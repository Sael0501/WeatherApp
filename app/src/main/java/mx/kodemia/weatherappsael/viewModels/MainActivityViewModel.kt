package mx.kodemia.weatherappsael.viewModels

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.kodemia.weatherapp.network.service.GetCity
import mx.kodemia.weatherapp.network.service.GetWeather
import mx.kodemia.weatherappsael.model.CityEntity
import mx.kodemia.weatherappsael.model.OneCall
import java.lang.Exception

class MainActivityViewModel  : ViewModel(){
    //Service
    lateinit var serviceGetWeather: GetWeather
    lateinit var serviceGetCity: GetCity

    //LiveDatas
    val getWeatherResponse = MutableLiveData<OneCall>()
    val getCityResponse = MutableLiveData<List<CityEntity>>()

    fun onCreate(){
        serviceGetWeather = GetWeather()
        serviceGetCity = GetCity()
    }

    //Funcion
    fun getWeather(lat: String, lon: String, units: String?, lang: String?, appid: String, activity: Activity){
        viewModelScope.launch {
            val response = serviceGetWeather.getWeatherService(lat, lon, units, lang, appid)
            try {
                if (response.isSuccessful){
                    getWeatherResponse.postValue(response.body())
                }else {
                    Toast.makeText(activity, "A ocurrido un error al obtener los datos del clima", Toast.LENGTH_SHORT).show()
                }
            } catch (exception :Exception){
                Toast.makeText(activity, "A ocurrido un error al obtener los datos del clima", Toast.LENGTH_SHORT).show()

            }

        }
    }

    fun getCity(lon:String, appid: String,lat: String, activity: Activity ){
        viewModelScope.launch {
            val response = serviceGetCity.getCityService(lon, appid,lat, )
            try {
                if (response.isSuccessful){
                    getCityResponse.postValue(response.body())
                }else{
                    Toast.makeText(activity, "A ocurrido un error localizando la ciudad", Toast.LENGTH_SHORT).show()
                }
            }   catch (exception: Exception){
                Toast.makeText(activity, "A ocurrido un error localizando la ciudad", Toast.LENGTH_SHORT).show()
            }

        }
    }

}