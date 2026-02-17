package com.stargazer.miniweatherfetcher.screens

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.stargazer.miniweatherfetcher.components.CitySearchBar
import com.stargazer.miniweatherfetcher.components.WeatherCard
import com.stargazer.miniweatherfetcher.viewmodel.HomeViewModel
import com.stargazer.miniweatherfetcher.viewmodel.WeatherState

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.weatherState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var permissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            permissionGranted = isGranted

            if (isGranted) {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            viewModel.fetchWeather(location.latitude, location.longitude, "Konumunuz")
                        } else {
                            viewModel.fetchWeather(41.0082, 28.9784, "İstanbul (GPS Bulunamadı)")
                        }
                    }
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!permissionGranted) {
                Text(text = "Hava durumunu görmek için konum izni gereklidir.", modifier = Modifier.padding(16.dp))
                Button(onClick = {
                    permissionLauncher.launch(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                }) {
                    Text("İzin Ver")
                }
            } else {
                when (state) {
                    is WeatherState.Loading -> {
                        CircularProgressIndicator()
                        Text(text = "Veriler getiriliyor...", modifier = Modifier.padding(top = 8.dp))
                    }
                    is WeatherState.Success -> {
                        val successState = state as WeatherState.Success
                        WeatherCard(
                            cityName = successState.cityName,
                            weather = successState.weather
                        )
                    }
                    is WeatherState.Error -> {
                        val errorMessage = (state as WeatherState.Error).message
                        Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            CitySearchBar(
                query = searchQuery,
                isSearching = isSearching,
                searchResults = searchResults,
                onQueryChange = { newText -> viewModel.onSearchQueryChange(newText) },
                onClearClick = { viewModel.clearSearch() },
                onCitySelect = { selectedCity ->
                    viewModel.fetchWeather(
                        lat = selectedCity.latitude,
                        long = selectedCity.longitude,
                        cityName = selectedCity.name
                    )
                    viewModel.clearSearch()
                }
            )
        }
    }
}