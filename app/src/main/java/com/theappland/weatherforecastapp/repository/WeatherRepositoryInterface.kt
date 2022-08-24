package com.theappland.weatherforecastapp.repository

import com.theappland.weatherforecastapp.model.Weather
import retrofit2.Response

interface WeatherRepositoryInterface {

    suspend fun getWeather(endPoint : String) : Response<Weather>
}