package com.theappland.weatherforecastapp.di

import com.theappland.weatherforecastapp.api.ApiService
import com.theappland.weatherforecastapp.repository.WeatherRepository
import com.theappland.weatherforecastapp.repository.WeatherRepositoryInterface
import com.theappland.weatherforecastapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit() : ApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun injectRealRepo(api: ApiService) = WeatherRepository(api) as WeatherRepositoryInterface
}