package com.example.weatherapp.server

import com.example.weatherapp.model.CurrentResponseApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("data/2.5/weather")
    fun getCurrentWeatger(
       @Query("lat") lat:Double,
       @Query("lon") lon:Double,
       @Query("units") units:String,
       @Query("app") ApiKey:String,
    ): Call<CurrentResponseApi>
}