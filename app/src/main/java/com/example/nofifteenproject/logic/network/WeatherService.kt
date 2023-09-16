package com.example.nofifteenproject.logic.network

import com.example.nofifteenproject.SunnyWeatherApplication
import com.example.nofifteenproject.logic.model.DailyResponse
import com.example.nofifteenproject.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {

    @GET("v2.6/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String):Call<RealtimeResponse>


    @GET("v2.6/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily?dailysteps=1")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String):Call<DailyResponse>

}