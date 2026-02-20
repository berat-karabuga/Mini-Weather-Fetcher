package com.stargazer.miniweatherfetcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.stargazer.miniweatherfetcher.data.local.DatabaseProvider
import com.stargazer.miniweatherfetcher.screens.MainScreen
import com.stargazer.miniweatherfetcher.ui.theme.MiniWeatherFetcherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseProvider.init(this)
        enableEdgeToEdge()
        setContent {
            MiniWeatherFetcherTheme {
                MainScreen()
            }
        }
    }
}