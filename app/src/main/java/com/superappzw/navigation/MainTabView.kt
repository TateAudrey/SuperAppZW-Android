package com.superappzw.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.model.DailyLanguageModel
import com.superappzw.ui.favourites.FavouritesView
import com.superappzw.ui.lisitngs.MyListingsView
import com.superappzw.ui.home.HomeView
import com.superappzw.ui.store.StoreListing
import com.superappzw.ui.store.StoreListingDetailView
import com.superappzw.ui.store.StoreProfileView
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun MainTabView(
    onLogout: () -> Unit,
    dailyLanguage: DailyLanguageModel? = null,
    currentUserName: String? = null,
    currentUserPhotoUrl: String? = null,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }

    // Inner NavController handles screen-level pushes (e.g. listing detail)
    // while selectedTab handles bottom nav tab switching independently.
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryColor,
                            selectedTextColor = PrimaryColor,
                            indicatorColor = PrimaryColor.copy(alpha = 0.12f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "tabs",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Tab container ─────────────────────────────────────────────────
            composable("tabs") {
                when (selectedTab) {
                    MainTab.HOME -> HomeView(
                        onLogout = onLogout,
                        dailyLanguage = dailyLanguage,
                        currentUserName = currentUserName,
                        currentUserPhotoUrl = currentUserPhotoUrl,
                        onCategorySelect = { category ->
                            // TODO: navController.navigate("categoryDetail/${category.name}")
                        },
                        onListingTap = { itemCode, ownerUserID ->
                            val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
                            if (ownerUserID == currentUserID) {
                                // Owner tapped their own listing — switch to My Listings tab
                                selectedTab = MainTab.MY_LISTINGS
                            } else {
                                // Other user's listing — navigate to their full store profile
                                navController.navigate("storeProfile/$ownerUserID")
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                    MainTab.MY_LISTINGS -> MyListingsView(
                        navController = navController,
                        modifier = Modifier.fillMaxSize()
                    )
                    MainTab.FAVOURITES -> FavouritesView(modifier = Modifier.fillMaxSize())
                }
            }

            // ── Store profile ─────────────────────────────────────────────────
            composable(
                route = "storeProfile/{ownerUserID}",
                arguments = listOf(
                    navArgument("ownerUserID") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                val ownerUserID = backStackEntry.arguments?.getString("ownerUserID")
                    ?: return@composable

                StoreProfileView(
                    storeID = ownerUserID,
                    onNavigateToListing = { listing ->
                        // User tapped a product card inside a store — go to listing detail
                        navController.navigate("listingDetail/${listing.itemCode}/${listing.ownerUserID}")
                    },
                )
            }

            // ── Listing detail ────────────────────────────────────────────────
            composable(
                route = "listingDetail/{itemCode}/{ownerUserID}",
                arguments = listOf(
                    navArgument("itemCode") { type = NavType.StringType },
                    navArgument("ownerUserID") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                val itemCode = backStackEntry.arguments?.getString("itemCode") ?: return@composable
                val ownerUserID = backStackEntry.arguments?.getString("ownerUserID") ?: return@composable

                StoreListingDetailView(
                    itemCode = itemCode,
                    ownerUserID = ownerUserID,
                )
            }
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