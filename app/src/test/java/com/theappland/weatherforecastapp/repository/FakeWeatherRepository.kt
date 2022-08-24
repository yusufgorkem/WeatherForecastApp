package com.theappland.weatherforecastapp.repository

import com.theappland.weatherforecastapp.model.Degree
import com.theappland.weatherforecastapp.model.Weather
import com.theappland.weatherforecastapp.model.WeatherX
import retrofit2.Response

class FakeWeatherRepository : WeatherRepositoryInterface {

    override suspend fun getWeather(endPoint: String): Response<Weather> {
        return Response.success(Weather("Gaziantep", listOf<WeatherX>(),Degree(27.27)))
    }
}