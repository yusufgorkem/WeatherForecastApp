package com.theappland.weatherforecastapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theappland.weatherforecastapp.model.Weather
import com.theappland.weatherforecastapp.repository.WeatherRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository : WeatherRepositoryInterface) : ViewModel() {
    var weather = MutableLiveData<Weather>()
    val loading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun getWeather(endPoint : String) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = repository.getWeather(endPoint)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        weather.value = it
                        loading.value = false
                        errorMessage.value = ""
                    }
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }

    fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    fun date(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}