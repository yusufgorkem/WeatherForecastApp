package com.theappland.weatherforecastapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.theappland.weatherforecastapp.MainCoroutineRule
import com.theappland.weatherforecastapp.getOrAwaitValueTest
import com.theappland.weatherforecastapp.model.WeatherX
import com.theappland.weatherforecastapp.repository.FakeWeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(FakeWeatherRepository())
    }

    @Test
    fun getWeatherLocationTest() {
        viewModel.getWeather("test")
        val weather = viewModel.weather.getOrAwaitValueTest()
        assertThat(weather.location).isEqualTo("Gaziantep")
    }

    @Test
    fun getWeatherWeatherXTest() {
        viewModel.getWeather("test")
        val weather = viewModel.weather.getOrAwaitValueTest()
        assertThat(weather.weather).isEqualTo(listOf<WeatherX>())
    }

    @Test
    fun getWeatherDegreeTest() {
        viewModel.getWeather("test")
        val weather = viewModel.weather.getOrAwaitValueTest()
        assertThat(weather.degree?.temp).isEqualTo(27.27)
    }

    @Test
    fun `errorMessage in onError fun`() {
        viewModel.onError("test")
        val value = viewModel.errorMessage.getOrAwaitValueTest()
        assertThat(value).isEqualTo("test")
    }

    @Test
    fun `loading in onError fun`() {
        viewModel.onError("test")
        val value = viewModel.loading.getOrAwaitValueTest()
        assertThat(value).isFalse()
    }
}