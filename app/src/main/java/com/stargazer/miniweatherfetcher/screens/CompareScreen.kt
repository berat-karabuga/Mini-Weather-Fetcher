package com.stargazer.miniweatherfetcher.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stargazer.miniweatherfetcher.components.CitySearchBar
import com.stargazer.miniweatherfetcher.viewmodel.CompareViewModel
import com.stargazer.miniweatherfetcher.utils.*

@Composable
fun CompareScreen(
    viewModel: CompareViewModel = viewModel()
) {
    val queryA by viewModel.queryA.collectAsStateWithLifecycle()
    val resultsA by viewModel.resultsA.collectAsStateWithLifecycle()
    val isSearchingA by viewModel.isSearchingA.collectAsStateWithLifecycle()
    val selectedCityA by viewModel.selectedCityA.collectAsStateWithLifecycle()
    val weatherA by viewModel.weatherA.collectAsStateWithLifecycle()
    val queryB by viewModel.queryB.collectAsStateWithLifecycle()
    val resultsB by viewModel.resultsB.collectAsStateWithLifecycle()
    val isSearchingB by viewModel.isSearchingB.collectAsStateWithLifecycle()
    val selectedCityB by viewModel.selectedCityB.collectAsStateWithLifecycle()
    val weatherB by viewModel.weatherB.collectAsStateWithLifecycle()
    val isCompared by viewModel.isCompared.collectAsStateWithLifecycle()
    val isA_Active = selectedCityA == null && !isCompared
    val isB_Active = selectedCityA != null && selectedCityB == null && !isCompared
    val isReadyToFight = selectedCityA != null && selectedCityB != null && !isCompared
    val infiniteTransition = rememberInfiniteTransition()

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(animation = tween(800, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "pulseScale"
    )

    val glowColor by infiniteTransition.animateColor(
        initialValue = Color(0xFFFF5252),
        targetValue = Color(0xFFFFD54F),
        animationSpec = infiniteRepeatable(animation = tween(800), repeatMode = RepeatMode.Reverse),
        label = "glowColor"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {

                val bgA = if (isCompared && weatherA != null) getDynamicGradient(weatherA!!.weatherCode)
                else Brush.verticalGradient(listOf(Color(0xFF0F2027), Color(0xFF68EAD7)))

                Box(modifier = Modifier.fillMaxSize().background(bgA))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (isA_Active || isCompared) 1f else 0.4f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!isCompared) {
                        if (selectedCityA != null) {
                            Text(text = "1. Şehir Seçildi:", color = Color.Gray, fontSize = 16.sp)
                            Text(text = selectedCityA!!.name, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        } else {
                            CitySearchBar(
                                query = queryA,
                                isSearching = isSearchingA,
                                searchResults = resultsA,
                                placeholderText = "1. Şehri Ara...",
                                onQueryChange = { viewModel.onQueryAChange(it) },
                                onClearClick = { viewModel.clearA() },
                                onCitySelect = { viewModel.selectCityA(it) }
                            )
                        }
                    } else {
                        if (weatherA != null) {
                            Text(text = selectedCityA!!.name, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "${weatherA!!.temperature}°C", fontSize = 64.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)

                            Text(text = getWeatherDescription(weatherA!!.weatherCode), fontSize = 18.sp, color = Color.White.copy(alpha = 0.8f))

                            Row(horizontalArrangement = Arrangement.spacedBy(32.dp), modifier = Modifier.padding(top = 16.dp)) {
                                MiniStatBadge(title = "Nem", value = "%${weatherA!!.humidity}")
                                MiniStatBadge(title = "Rüzgar", value = "${weatherA!!.windSpeed} km/h")
                            }
                        } else {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {

                val bgB = if (isCompared && weatherB != null) getDynamicGradient(weatherB!!.weatherCode)
                else Brush.verticalGradient(listOf(Color(0xFF203A43), Color(0xFF2C5364)))

                Box(modifier = Modifier.fillMaxSize().background(bgB))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (isB_Active || isCompared || isReadyToFight) 1f else 0.4f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!isCompared) {
                        if (selectedCityB != null) {
                            Text(text = "2. Şehir Seçildi:", color = Color.Gray, fontSize = 16.sp)
                            Text(text = selectedCityB!!.name, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        } else {
                            CitySearchBar(
                                query = queryB,
                                isSearching = isSearchingB,
                                searchResults = resultsB,
                                placeholderText = "2. Şehri Ara...",
                                onQueryChange = { viewModel.onQueryBChange(it) },
                                onClearClick = { viewModel.clearB() },
                                onCitySelect = { viewModel.selectCityB(it) }
                            )
                        }
                    } else {
                        if (weatherB != null) {
                            Text(text = selectedCityB!!.name, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "${weatherB!!.temperature}°C", fontSize = 64.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)

                            Text(text = getWeatherDescription(weatherB!!.weatherCode), fontSize = 18.sp, color = Color.White.copy(alpha = 0.8f))

                            Row(horizontalArrangement = Arrangement.spacedBy(32.dp), modifier = Modifier.padding(top = 16.dp)) {
                                MiniStatBadge(title = "Nem", value = "%${weatherB!!.humidity}")
                                MiniStatBadge(title = "Rüzgar", value = "${weatherB!!.windSpeed} km/h")
                            }
                        } else {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .scale(if (isReadyToFight) pulseScale else 1f)
                .size(72.dp)
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .padding(4.dp)
                .clip(CircleShape)
                .background(
                    if (isCompared) Color.DarkGray
                    else if (isReadyToFight) glowColor
                    else Color.Gray
                )
                .clickable(enabled = isReadyToFight || isCompared) {
                    if (isCompared) viewModel.reset()
                    else viewModel.compareCities()
                },
            contentAlignment = Alignment.Center
        ) {
            if (isCompared) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Sıfırla", tint = Color.White, modifier = Modifier.size(36.dp))
            } else {
                Text(text = "VS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun MiniStatBadge(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

fun getDynamicGradient(weatherCode: Int): Brush {
    return when (weatherCode) {
        0 -> Brush.verticalGradient(listOf(Color(0xFF4FC3F7), Color(0xFF0288D1)))
        1, 2, 3 -> Brush.verticalGradient(listOf(Color(0xFF90A4AE), Color(0xFF607D8B)))
        else -> Brush.verticalGradient(listOf(Color(0xFF5C6BC0), Color(0xFF3949AB)))
    }
}



