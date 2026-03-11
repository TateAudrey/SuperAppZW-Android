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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import com.superappzw.ui.store.StoreListingDetailView
import com.superappzw.ui.store.StoreProfileView
import com.superappzw.ui.theme.IOSSystemBackground
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
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = IOSSystemBackground
            ) {
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
            composable("tabs") { backStackEntry ->
                // Scope all ViewModels to the "tabs" destination so they survive navigation
                val tabsEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("tabs")
                }

                when (selectedTab) {
                    MainTab.HOME -> HomeView(
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
                                selectedTab = MainTab.MY_LISTINGS
                            } else {
                                navController.navigate("storeProfile/$ownerUserID")
                            }
                        },
                        onStoreTap = { userID ->
                            val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
                            if (userID == currentUserID) {
                                selectedTab = MainTab.MY_LISTINGS
                            } else {
                                navController.navigate("storeProfile/$userID")
                            }
                        },
                        // ── Scoped ViewModels ─────────────────────────────────────────
                        provinceViewModel = viewModel(tabsEntry),
                        billboardViewModel = viewModel(tabsEntry),
                        homeViewModel = viewModel(tabsEntry),
                        modifier = Modifier.fillMaxSize(),
                    )
                    MainTab.MY_LISTINGS -> MyListingsView(
                        navController = navController,
                        viewModel = viewModel(tabsEntry),  // ← add this
                        modifier = Modifier.fillMaxSize(),
                    )
                    MainTab.FAVOURITES -> FavouritesView(modifier = Modifier.fillMaxSize())
                }
            }

            // ── Category detail ───────────────────────────────────────────────
            composable(
                route = "categoryDetail/{categoryIndex}",
                arguments = listOf(
                    navArgument("categoryIndex") { type = NavType.IntType },
                ),
            ) { backStackEntry ->
                val index = backStackEntry.arguments?.getInt("categoryIndex") ?: return@composable
                val category = CategoryItem.all.getOrNull(index) ?: return@composable
                CategoryDetailView(
                    category = category,
                    onListingTap = { itemCode, ownerUserID ->
                        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
                        if (ownerUserID == currentUserID) {
                            // Own listing — pop back to tabs and switch to My Listings
                            navController.popBackStack("tabs", inclusive = false)
                            selectedTab = MainTab.MY_LISTINGS
                        } else {
                            // Another user's listing — go to their store profile
                            navController.navigate("storeProfile/$ownerUserID")
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            // ── Account ───────────────────────────────────────────────────────
            composable("account") {
                AccountView(navController = navController)
            }

            // ── Profile detail ────────────────────────────────────────────────
            composable("profileDetail") {
                ProfileDetailView(
                    onDismiss = { navController.popBackStack() },
                )
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
                        navController.navigate("listingDetail/${listing.itemCode}/${listing.ownerUserID}")
                    },
                )
            }

            //Policies

            composable("policies") {
                AppPoliciesView()
            }

            //Support
            composable("support") {
                SupportView(
                    currentUserName = currentUserName
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

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainTabViewPreview() {
    SuperAppZWTheme {
        MainTabView(onLogout = {})
    }
}