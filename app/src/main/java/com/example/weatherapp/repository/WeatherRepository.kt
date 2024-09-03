package com.example.weatherapp.repository

import com.example.weatherapp.server.ApiServices

class WeatherRepository(val api: ApiServices) {

    fun getCurrentWeather(lat: Double, lng: Double, unit: String) =
        api.getCurrentWeatger(lat,lng,unit, "d322ab245e818083b5e6a021eda1908b")

    companion object{
        const val API_KEY = "d322ab245e818083b5e6a021eda1908b"
    }
}