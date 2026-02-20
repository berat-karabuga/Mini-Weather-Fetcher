package com.stargazer.miniweatherfetcher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stargazer.miniweatherfetcher.data.RetrofitClient
import com.stargazer.miniweatherfetcher.model.CurrentWeather
import com.stargazer.miniweatherfetcher.model.LocationResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompareViewModel : ViewModel() {
    private val _queryA = MutableStateFlow("")
    val queryA = _queryA.asStateFlow()
    private val _resultsA = MutableStateFlow<List<LocationResult>>(emptyList())
    val resultsA = _resultsA.asStateFlow()
    private val _isSearchingA = MutableStateFlow(false)
    val isSearchingA = _isSearchingA.asStateFlow()
    private val _selectedCityA = MutableStateFlow<LocationResult?>(null)
    val selectedCityA = _selectedCityA.asStateFlow()
    private val _weatherA = MutableStateFlow<CurrentWeather?>(null)
    val weatherA = _weatherA.asStateFlow()
    private var jobA: Job? = null
    private val _queryB = MutableStateFlow("")
    val queryB = _queryB.asStateFlow()
    private val _resultsB = MutableStateFlow<List<LocationResult>>(emptyList())
    val resultsB = _resultsB.asStateFlow()
    private val _isSearchingB = MutableStateFlow(false)
    val isSearchingB = _isSearchingB.asStateFlow()
    private val _selectedCityB = MutableStateFlow<LocationResult?>(null)
    val selectedCityB = _selectedCityB.asStateFlow()
    private val _weatherB = MutableStateFlow<CurrentWeather?>(null)
    val weatherB = _weatherB.asStateFlow()
    private var jobB: Job? = null
    private val _isCompared = MutableStateFlow(false)
    val isCompared = _isCompared.asStateFlow()

    fun onQueryAChange(query: String) {
        _queryA.value = query
        if (query.length < 2) {
            jobA?.cancel(); _resultsA.value = emptyList(); _isSearchingA.value = false; return
        }
        jobA?.cancel()
        jobA = viewModelScope.launch {
            delay(500); _isSearchingA.value = true
            try { _resultsA.value = RetrofitClient.api.searchCity(query).results ?: emptyList() }
            catch (e: Exception) { _resultsA.value = emptyList() }
            finally { _isSearchingA.value = false }
        }
    }

    fun onQueryBChange(query: String) {
        _queryB.value = query
        if (query.length < 2) {
            jobB?.cancel(); _resultsB.value = emptyList(); _isSearchingB.value = false; return
        }
        jobB?.cancel()
        jobB = viewModelScope.launch {
            delay(500); _isSearchingB.value = true
            try { _resultsB.value = RetrofitClient.api.searchCity(query).results ?: emptyList() }
            catch (e: Exception) { _resultsB.value = emptyList() }
            finally { _isSearchingB.value = false }
        }
    }

    fun selectCityA(city: LocationResult) {
        _queryA.value = ""; _resultsA.value = emptyList()
        _selectedCityA.value = city
    }

    fun selectCityB(city: LocationResult) {
        _queryB.value = ""; _resultsB.value = emptyList()
        _selectedCityB.value = city
    }

    fun compareCities() {
        if (_selectedCityA.value != null && _selectedCityB.value != null) {
            _isCompared.value = true

            viewModelScope.launch {
                try { _weatherA.value = RetrofitClient.api.getWeather(_selectedCityA.value!!.latitude, _selectedCityA.value!!.longitude).current } catch (e: Exception) {}
            }
            viewModelScope.launch {
                try { _weatherB.value = RetrofitClient.api.getWeather(_selectedCityB.value!!.latitude, _selectedCityB.value!!.longitude).current } catch (e: Exception) {}
            }
        }
    }

    fun reset() {
        _selectedCityA.value = null
        _selectedCityB.value = null
        _weatherA.value = null
        _weatherB.value = null
        _isCompared.value = false
        clearA()
        clearB()
    }

    fun clearA() { _queryA.value = ""; _resultsA.value = emptyList() }
    fun clearB() { _queryB.value = ""; _resultsB.value = emptyList() }
}
