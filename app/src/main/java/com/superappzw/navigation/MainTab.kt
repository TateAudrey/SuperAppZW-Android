package com.superappzw.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    HOME(label = "Home", icon = Icons.Filled.Home, route = "home"),
    MY_LISTINGS(label = "My Listings", icon = Icons.Filled.List, route = "myListings"),
    FAVOURITES(label = "Favourites", icon = Icons.Filled.Favorite, route = "favourites"),
}