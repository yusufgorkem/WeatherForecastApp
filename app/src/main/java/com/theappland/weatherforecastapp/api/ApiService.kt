package com.theappland.weatherforecastapp.api

import com.theappland.weatherforecastapp.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    suspend fun getWeather(@Url endPoint: String) : Response<Weather>
}