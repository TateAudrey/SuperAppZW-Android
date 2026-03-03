package com.superappzw.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(
    val label: String,
    val icon: ImageVector,
) {
    HOME(label = "Home", icon = Icons.Filled.Home),
    MY_LISTINGS(label = "My Listings", icon = Icons.Filled.List),
    FAVOURITES(label = "Favourites", icon = Icons.Filled.Favorite),
}