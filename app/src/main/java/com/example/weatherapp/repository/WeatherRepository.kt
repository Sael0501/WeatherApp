package com.example.weatherapp.repository

import com.example.weatherapp.server.ApiServices

class WeatherRepository(val api: ApiServices) {

    fun getCurrentWeather(lat: Double, lon: Double, unit: String) =
        api.getCurrentWeatger(lat,lon,unit, API_KEY)

    companion object{
        const val API_KEY = "fc860cc3326714bd7649985227d269c2"
    }
}