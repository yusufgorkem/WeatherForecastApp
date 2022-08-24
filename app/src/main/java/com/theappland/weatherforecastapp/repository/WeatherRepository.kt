package com.theappland.weatherforecastapp.repository

import com.theappland.weatherforecastapp.api.ApiService
import com.theappland.weatherforecastapp.model.Weather
import retrofit2.Response
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val apiService: ApiService) : WeatherRepositoryInterface {

    override suspend fun getWeather(endPoint: String): Response<Weather> {
        return apiService.getWeather(endPoint)
    }
}