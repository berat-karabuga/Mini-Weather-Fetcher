package com.stargazer.miniweatherfetcher.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem<T : Any>(
    val name: String,
    val route: T,
    val icon: ImageVector
)