package com.stargazer.miniweatherfetcher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stargazer.miniweatherfetcher.data.RetrofitClient
import com.stargazer.miniweatherfetcher.data.local.DatabaseProvider
import com.stargazer.miniweatherfetcher.data.local.FavoriteCity
import com.stargazer.miniweatherfetcher.model.CurrentWeather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {
    private val dao = DatabaseProvider.db.favoriteDao()

    val favoriteCities: StateFlow<List<FavoriteCity>> = dao.getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    private val _weatherMap = MutableStateFlow<Map<String, CurrentWeather>>(emptyMap())
    val weatherMap = _weatherMap.asStateFlow()

    init {
        viewModelScope.launch {
            favoriteCities.collect { cities ->
                fetchWeathersForFavorites(cities)
            }
        }
    }

    private suspend fun fetchWeathersForFavorites(cities: List<FavoriteCity>) {
        val currentMap = _weatherMap.value.toMutableMap()
        var mapChanged = false

        for (city in cities) {
            if (!currentMap.containsKey(city.cityName)) {
                try {
                    val response = RetrofitClient.api.getWeather(city.latitude, city.longitude)
                    currentMap[city.cityName] = response.current
                    mapChanged = true
                } catch (e: Exception) {
                }
            }
        }

        if (mapChanged) {
            _weatherMap.value = currentMap
        }
    }

    fun removeFavorite(city: FavoriteCity) {
        viewModelScope.launch {
            dao.removeFavorite(city)

            val currentMap = _weatherMap.value.toMutableMap()
            currentMap.remove(city.cityName)
            _weatherMap.value = currentMap
        }
    }
}