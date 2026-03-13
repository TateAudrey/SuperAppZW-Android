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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.appPolicy.AppPoliciesView
import com.superappzw.model.DailyLanguageModel
import com.superappzw.support.SupportView
import com.superappzw.ui.account.AccountView
import com.superappzw.ui.account.ProfileDetailView
import com.superappzw.ui.categories.CategoryDetailView
import com.superappzw.ui.categories.CategoryItem
import com.superappzw.ui.favourites.FavouritesView
import com.superappzw.ui.home.HomeView
import com.superappzw.ui.lisitngs.MyListingsView
import com.superappzw.ui.reviews.MyReviewsView
import com.superappzw.ui.store.StoreListingDetailView
import com.superappzw.ui.store.StoreProfileView
import com.superappzw.ui.theme.IOSSystemBackground
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

// Routes where the bottom bar should be visible
private val ROOT_ROUTES = setOf("home", "myListings", "favourites")

@Composable
fun MainTabView(
    onLogout: () -> Unit,
    dailyLanguage: DailyLanguageModel? = null,
    currentUserName: String? = null,
    currentUserPhotoUrl: String? = null,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    // Show bottom bar only on root tab screens — mirrors iOS hiding on push
    val showBottomBar = currentRoute in ROOT_ROUTES

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = IOSSystemBackground) {
                    MainTab.entries.forEach { tab ->
                        val tabRoute = tab.route
                        NavigationBarItem(
                            selected = currentRoute == tabRoute,
                            onClick = {
                                if (currentRoute != tabRoute) {
                                    navController.navigate(tabRoute) {
                                        // Pop up to home so back stack doesn't grow
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
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
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            // ── Root tabs ─────────────────────────────────────────────────────
            composable("home") { backStackEntry ->
                HomeView(
                    onLogout = onLogout,
                    dailyLanguage = dailyLanguage,
                    currentUserName = currentUserName,
                    currentUserPhotoUrl = currentUserPhotoUrl,
                    onProfileTap = { navController.navigate("account") },
                    onCategorySelect = { category ->
                        val index = CategoryItem.all.indexOf(category)
                        if (index >= 0) navController.navigate("categoryDetail/$index")
                    },
                    onListingTap = { itemCode, ownerUserID ->
                        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
                        if (ownerUserID == currentUserID) {
                            navController.navigate("myListings") {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            navController.navigate("storeProfile/$ownerUserID")
                        }
                    },
                    onStoreTap = { userID ->
                        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
                        if (userID == currentUserID) {
                            navController.navigate("myListings") {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            navController.navigate("storeProfile/$userID")
                        }
                    },
                    provinceViewModel = viewModel(),
                    billboardViewModel = viewModel(),
                    homeViewModel = viewModel(),
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable("myListings") {
                MyListingsView(
                    navController = navController,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable("favourites") {
                FavouritesView(
                    navController = navController,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable("myReviews") {
                MyReviewsView()
            }

            // ── Pushed screens — bottom bar hidden ────────────────────────────
            composable(
                route = "categoryDetail/{categoryIndex}",
                arguments = listOf(navArgument("categoryIndex") { type = NavType.IntType }),
            ) { backStackEntry ->
                val index    = backStackEntry.arguments?.getInt("categoryIndex") ?: return@composable
                val category = CategoryItem.all.getOrNull(index) ?: return@composable
                CategoryDetailView(
                    category = category,
                    onListingTap = { itemCode, ownerUserID ->
                        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
                        if (ownerUserID == currentUserID) {
                            navController.navigate("myListings") {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            navController.navigate("storeProfile/$ownerUserID")
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable("account") {
                AccountView(navController = navController)
            }

            composable("profileDetail") {
                ProfileDetailView(onDismiss = { navController.popBackStack() })
            }

            composable(
                route = "storeProfile/{ownerUserID}",
                arguments = listOf(navArgument("ownerUserID") { type = NavType.StringType }),
            ) { backStackEntry ->
                val ownerUserID = backStackEntry.arguments?.getString("ownerUserID")
                    ?: return@composable
                StoreProfileView(
                    storeID = ownerUserID,
                    onNavigateToListing = { listing ->
                        navController.navigate("listingDetail/${listing.itemCode}/${listing.ownerUserID}")
                    },
                )
            }

            composable("policies") { AppPoliciesView() }

            composable("support") {
                SupportView(currentUserName = currentUserName)
            }

            composable(
                route = "listingDetail/{itemCode}/{ownerUserID}",
                arguments = listOf(
                    navArgument("itemCode") { type = NavType.StringType },
                    navArgument("ownerUserID") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                val itemCode    = backStackEntry.arguments?.getString("itemCode") ?: return@composable
                val ownerUserID = backStackEntry.arguments?.getString("ownerUserID") ?: return@composable
                StoreListingDetailView(
                    itemCode    = itemCode,
                    ownerUserID = ownerUserID,
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainTabViewPreview() {
    SuperAppZWTheme {
        MainTabView(onLogout = {})
    }
}
