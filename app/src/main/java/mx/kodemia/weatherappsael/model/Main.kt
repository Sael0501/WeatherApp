package mx.kodemia.weatherappsael.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Main(
    val temp: Double,
    val feelsLike: Double,
    val tempMax: Double,
    val tempMin: Double,
    val pressure: Int,
    val humidity: Int,
): Serializable