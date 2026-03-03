package com.superappzw.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.superappzw.ui.favourites.FavouritesView
import com.superappzw.ui.lisitngs.MyListingsView
import com.superappzw.ui.home.HomeView
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun MainTabView(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                            )
                        },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryColor,
                            selectedTextColor = PrimaryColor,
                            indicatorColor = PrimaryColor.copy(alpha = 0.12f), // subtle pill behind icon
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        when (selectedTab) {
            MainTab.HOME -> HomeView(
                onLogout = onLogout,
                modifier = Modifier.fillMaxSize(),
            )
            MainTab.MY_LISTINGS -> MyListingsView(
                modifier = Modifier.fillMaxSize(),
            )
            MainTab.FAVOURITES -> FavouritesView(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainTabViewPreview() {
    SuperAppZWTheme {
        MainTabView(onLogout = {})
    }
}