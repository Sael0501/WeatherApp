package mx.kodemia.weatherappsael.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Current(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val wind_speed: Double,
    val weather: List<Weather>
) :Serializable
