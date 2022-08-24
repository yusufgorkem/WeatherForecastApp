package com.theappland.weatherforecastapp.model

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("name")
    var location: String? = null,
    var weather: List<WeatherX>? = null,
    @SerializedName("main")
    var degree: Degree? = null,
)