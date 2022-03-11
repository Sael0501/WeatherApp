package mx.kodemia.weatherappsael.model

import mx.kodemia.weatherapp.model.Daily
import java.io.Serializable

data class OneCall(
    val current: Current,
    val daily:List<Daily>,
    val hourly: List<Current>,
    var cityEntity: CityEntity?
): Serializable