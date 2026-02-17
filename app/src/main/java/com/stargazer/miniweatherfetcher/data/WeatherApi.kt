package com.stargazer.miniweatherfetcher.data

import com.stargazer.miniweatherfetcher.model.GeocodingResponse
import com.stargazer.miniweatherfetcher.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") long: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,wind_speed_10m,weather_code",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse


    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun searchCity(
        @Query("name") query: String,
        @Query("count") count: Int = 5,
        @Query("language") language: String = "tr" // şimdilik şehirler türkçe önerilicek
    ): GeocodingResponse
}