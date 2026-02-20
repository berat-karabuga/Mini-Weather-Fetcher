package com.stargazer.miniweatherfetcher.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stargazer.miniweatherfetcher.model.CurrentWeather
import com.stargazer.miniweatherfetcher.utils.*

@Composable
fun WeatherCard(
    cityName: String,
    weather: CurrentWeather,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val weatherDescription = getWeatherDescription(weather.weatherCode)

    val cardBackground = when (weather.weatherCode) {
        0 -> Brush.verticalGradient(listOf(Color(0xFF4FC3F7), Color(0xFF0288D1)))
        1, 2, 3 -> Brush.verticalGradient(listOf(Color(0xFF90A4AE), Color(0xFF607D8B)))
        else -> Brush.verticalGradient(listOf(Color(0xFF5C6BC0), Color(0xFF3949AB)))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 400)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(cardBackground)
                .padding(24.dp)
        ) {

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favoriye Ekle",
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = cityName, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))


                Text(text = "${weather.temperature}°C", fontSize = 64.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text(text = weatherDescription, fontSize = 18.sp, color = Color.White.copy(alpha = 0.8f))

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { isExpanded = !isExpanded }) {
                    Text(text = if (isExpanded) "Detayları Gizle" else "Detayları Gör", color = Color.White)
                }

                if (isExpanded) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 12.dp))
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            WeatherDetailItem(title = "Hissedilen", value = "${weather.apparentTemperature}°C")
                            WeatherDetailItem(title = "Nem Oranı", value = "%${weather.humidity}")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            WeatherDetailItem(title = "Rüzgar", value = "${weather.windSpeed} km/s")
                            WeatherDetailItem(title = "Son Güncelleme", value = weather.time.substringAfter("T"))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherDetailItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}