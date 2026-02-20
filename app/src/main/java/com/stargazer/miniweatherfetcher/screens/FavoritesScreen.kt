package com.stargazer.miniweatherfetcher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stargazer.miniweatherfetcher.data.local.FavoriteCity
import com.stargazer.miniweatherfetcher.model.CurrentWeather
import com.stargazer.miniweatherfetcher.utils.*
import com.stargazer.miniweatherfetcher.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = viewModel(),
    onCityClick: (FavoriteCity) -> Unit = {}
) {
    val favorites by viewModel.favoriteCities.collectAsStateWithLifecycle()
    val weatherMap by viewModel.weatherMap.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Favorilerim", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.DarkGray)
            if (favorites.isNotEmpty()) {
                Text(text = "${favorites.size} Şehir", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 6.dp))
            }
        }

        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🌍", fontSize = 80.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Buralar Çok Issız...", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Ana sayfadaki arama çubuğunu kullanarak\nhemen yeni şehirler keşfet!", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center, lineHeight = 20.sp)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favorites) { city ->
                    val weather = weatherMap[city.cityName]

                    PremiumFavoriteCard(
                        cityName = city.cityName,
                        weather = weather,
                        onDeleteClick = { viewModel.removeFavorite(city) },
                        onCardClick = { onCityClick(city) }
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumFavoriteCard(
    cityName: String,
    weather: CurrentWeather?,
    onDeleteClick: () -> Unit,
    onCardClick: () -> Unit
) {
    val cardBackground = if (weather != null) {
        when (weather.weatherCode) {
            0 -> Brush.verticalGradient(listOf(Color(0xFF4FC3F7), Color(0xFF0288D1)))
            1, 2, 3 -> Brush.verticalGradient(listOf(Color(0xFF90A4AE), Color(0xFF607D8B)))
            else -> Brush.verticalGradient(listOf(Color(0xFF5C6BC0), Color(0xFF3949AB)))
        }
    } else {
        Brush.verticalGradient(listOf(Color(0xFFB0BEC5), Color(0xFF78909C)))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardBackground)
                .clickable { onCardClick() }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = cityName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.2f))
                        .clickable { onDeleteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Sil", tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart),
                verticalArrangement = Arrangement.Bottom
            ) {
                if (weather != null) {
                    val emoji = getWeatherEmoji(weather.weatherCode)
                    Text(text = emoji, fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${weather.temperature}°C", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(text = getWeatherDescription(weather.weatherCode), fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                } else {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Güncelleniyor...", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                }
            }
        }
    }
}