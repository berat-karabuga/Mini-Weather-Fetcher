package com.stargazer.miniweatherfetcher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stargazer.miniweatherfetcher.data.RetrofitClient
import com.stargazer.miniweatherfetcher.model.CurrentWeather
import com.stargazer.miniweatherfetcher.model.LocationResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val weather: CurrentWeather, val cityName: String) : WeatherState()
    data class Error(val message: String) : WeatherState()
}

class HomeViewModel : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    private val _searchResults = MutableStateFlow<List<LocationResult>>(emptyList())
    val searchResults = _searchResults.asStateFlow()
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query

        if (query.length < 2) {
            searchJob?.cancel()
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(100)
            _isSearching.value = true

            try {
                val response = RetrofitClient.api.searchCity(query)
                _searchResults.value = response.results ?: emptyList()
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    fun fetchWeather(lat: Double, long: Double, cityName: String) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            try {
                val response = RetrofitClient.api.getWeather(lat, long)
                _weatherState.value = WeatherState.Success(response.current, cityName)
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error("Hava durumu alınamadı. İnternetinizi kontrol edin.")
            }
        }
    }
}