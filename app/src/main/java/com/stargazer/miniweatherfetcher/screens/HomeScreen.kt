package com.stargazer.miniweatherfetcher.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.stargazer.miniweatherfetcher.components.CitySearchBar
import com.stargazer.miniweatherfetcher.components.WeatherCard
import com.stargazer.miniweatherfetcher.model.DailyForecast
import com.stargazer.miniweatherfetcher.model.HourlyForecast
import com.stargazer.miniweatherfetcher.viewmodel.HomeViewModel
import com.stargazer.miniweatherfetcher.viewmodel.WeatherState
import com.stargazer.miniweatherfetcher.utils.*
import java.util.Locale

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.weatherState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasFetchedInitialLocation by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            permissionGranted = permissions.any { it.value }

            if (permissionGranted && !hasFetchedInitialLocation) {
                hasFetchedInitialLocation = true
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            try {
                                val geocoder = Geocoder(context, Locale("tr", "TR"))
                                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                val cityName = addresses?.firstOrNull()?.let {
                                    it.adminArea ?: it.subAdminArea ?: it.locality
                                } ?: "Bulunduğunuz Konum" // Hiçbirini bulamazsa bunu yazsın

                                viewModel.fetchWeather(location.latitude, location.longitude, cityName)
                            } catch (e: Exception) {
                                viewModel.fetchWeather(location.latitude, location.longitude, "Bulunduğunuz Konum")
                            }
                        } else {
                            viewModel.fetchWeather(41.0082, 28.9784, "İstanbul (Konum Bulunamadı)")
                        }
                    }
            } else if (!permissionGranted) {
                hasFetchedInitialLocation = true
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasFetchedInitialLocation) {
            if (!permissionGranted) {
                permissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            } else {
                hasFetchedInitialLocation = true
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            try {
                                val geocoder = Geocoder(context, Locale("tr", "TR"))
                                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                val cityName = addresses?.firstOrNull()?.let {
                                    it.adminArea ?: it.subAdminArea ?: it.locality
                                } ?: "Bulunduğunuz Konum"

                                viewModel.fetchWeather(location.latitude, location.longitude, cityName)
                            } catch (e: Exception) {
                                viewModel.fetchWeather(location.latitude, location.longitude, "Bulunduğunuz Konum")
                            }
                        } else {
                            viewModel.fetchWeather(41.0082, 28.9784, "İstanbul (Konum Bulunamadı)")
                        }
                    }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 80.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!permissionGranted) {
                Spacer(modifier = Modifier.height(100.dp))
                Text(text = "Hava durumunu görmek için konum izni gereklidir.", modifier = Modifier.padding(16.dp))
                Button(onClick = {
                    permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                }) {
                    Text("İzin Ver")
                }
            } else {
                when (state) {
                    is WeatherState.Loading -> {
                        Spacer(modifier = Modifier.height(100.dp))
                        CircularProgressIndicator()
                        Text(text = "Veriler getiriliyor...", modifier = Modifier.padding(top = 8.dp))
                    }
                    is WeatherState.Success -> {
                        val successState = state as WeatherState.Success
                        val response = successState.response

                        WeatherCard(
                            cityName = successState.cityName,
                            weather = response.current,
                            isFavorite = isFavorite,
                            onFavoriteClick = {
                                viewModel.toggleFavorite(successState.cityName, successState.lat, successState.long)
                            }
                        )

                        HourlyForecastSection(hourly = response.hourly, currentTime = response.current.time)

                        DailyForecastSection(daily = response.daily)
                    }
                    is WeatherState.Error -> {
                        Spacer(modifier = Modifier.height(100.dp))
                        val errorMessage = (state as WeatherState.Error).message
                        Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
            CitySearchBar(
                query = searchQuery,
                isSearching = isSearching,
                searchResults = searchResults,
                onQueryChange = { newText -> viewModel.onSearchQueryChange(newText) },
                onClearClick = { viewModel.clearSearch() },
                onCitySelect = { selectedCity ->
                    viewModel.fetchWeather(selectedCity.latitude, selectedCity.longitude, selectedCity.name)
                    viewModel.clearSearch()
                }
            )
        }
    }
}


@Composable
fun HourlyForecastSection(hourly: HourlyForecast?, currentTime: String) {
    if (hourly == null) return

    val currentHourPrefix = currentTime.substringBefore(":")
    val startIndex = hourly.time.indexOfFirst { it.startsWith(currentHourPrefix) }.takeIf { it >= 0 } ?: 0

    val next24Times = hourly.time.drop(startIndex).take(24)
    val next24Temps = hourly.temperature.drop(startIndex).take(24)
    val next24Codes = hourly.weatherCode.drop(startIndex).take(24)

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Text(
            text = "Saatlik Tahmin",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(next24Times.size) { i ->
                val timeOnly = next24Times[i].substringAfter("T")
                val temp = next24Temps[i]
                val emoji = getWeatherEmoji(next24Codes[i])

                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = timeOnly, fontSize = 14.sp, color = Color.DarkGray)
                        Text(text = emoji, fontSize = 24.sp)
                        Text(text = "${temp}°C", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DailyForecastSection(daily: DailyForecast?) {
    if (daily == null) return

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(vertical = 8.dp)) {
        Text(
            text = "7 Günlük Tahmin",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                for (i in daily.time.indices) {
                    val date = formatDateToTurkish(daily.time[i])
                    val min = daily.minTemp[i]
                    val max = daily.maxTemp[i]
                    val emoji = getWeatherEmoji(daily.weatherCode[i])

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = if (i == 0) "Bugün" else date, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Normal)
                        Text(text = emoji, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 24.sp)
                        Text(text = "${min}° / ${max}°", modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }

                    if (i < daily.time.size - 1) {
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}
