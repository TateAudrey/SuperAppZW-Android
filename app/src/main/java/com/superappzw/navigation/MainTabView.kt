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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.superappzw.ui.onboarding.GuestPromptReason
import com.superappzw.ui.onboarding.GuestSignInPromptSheet

// Routes where the bottom bar should be visible
private val ROOT_ROUTES = setOf("home", "myListings", "favourites")

@Composable
fun MainTabView(
    onLogout: () -> Unit,
    dailyLanguage: DailyLanguageModel? = null,
    currentUserName: String? = null,
    currentUserPhotoUrl: String? = null,
    isGuest: Boolean = false,
    onSignInRequired: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val showBottomBar = currentRoute in ROOT_ROUTES

    // ── Guest prompt state ────────────────────────────────────────────────────
    var showGuestPrompt by remember { mutableStateOf(false) }
    var guestPromptReason by remember { mutableStateOf(GuestPromptReason.MY_LISTINGS) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = IOSSystemBackground) {
                    MainTab.entries.forEach { tab ->
                        val tabRoute = tab.route
                        val isSelected = currentRoute == tabRoute && !isGuest ||
                                currentRoute == tabRoute && tab == MainTab.HOME

                        NavigationBarItem(
                            selected = if (isGuest) tabRoute == "home" && currentRoute == "home"
                            else currentRoute == tabRoute,
                            onClick = {
                                when {
                                    // Guests tapping My Listings or Favourites see prompt
                                    isGuest && tabRoute == "myListings" -> {
                                        guestPromptReason = GuestPromptReason.MY_LISTINGS
                                        showGuestPrompt = true
                                    }
                                    isGuest && tabRoute == "favourites" -> {
                                        guestPromptReason = GuestPromptReason.FAVOURITES
                                        showGuestPrompt = true
                                    }
                                    currentRoute != tabRoute -> {
                                        navController.navigate(tabRoute) {
                                            popUpTo("home") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
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
            composable("home") {
                HomeView(
                    onLogout = onLogout,
                    dailyLanguage = dailyLanguage,
                    currentUserName = currentUserName,
                    currentUserPhotoUrl = currentUserPhotoUrl,
                    onProfileTap = {
                        if (isGuest) {
                            guestPromptReason = GuestPromptReason.ACCOUNT
                            showGuestPrompt = true
                        } else {
                            navController.navigate("account")
                        }
                    },
                    onCategorySelect = { category ->
                        val index = CategoryItem.all.indexOf(category)
                        if (index >= 0) navController.navigate("categoryDetail/$index")
                    },
                    onListingTap = { itemCode, ownerUserID ->
                        // Guests can view store profiles freely
                        val currentUserID = try { FirebaseAuth.getInstance().currentUser?.uid } catch (e: Exception) { null }
                        if (!isGuest && ownerUserID == currentUserID) {
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
                        val currentUserID = try { FirebaseAuth.getInstance().currentUser?.uid } catch (e: Exception) { null }
                        if (!isGuest && userID == currentUserID) {
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
                // Guests should never reach here — guarded at tab tap level
                if (!isGuest) {
                    MyListingsView(
                        navController = navController,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            composable("favourites") {
                // Guests should never reach here — guarded at tab tap level
                if (!isGuest) {
                    FavouritesView(
                        navController = navController,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
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
                        val currentUserID = try { FirebaseAuth.getInstance().currentUser?.uid } catch (e: Exception) { null }
                        if (!isGuest && ownerUserID == currentUserID) {
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
                // Guard — guests should never reach this via navigation
                if (!isGuest) {
                    AccountView(navController = navController)
                }
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
                    isGuest = isGuest,
                    onGuestSignInRequired = { reason ->
                        guestPromptReason = reason
                        showGuestPrompt = true
                    },
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
                    isGuest = isGuest,
                    onGuestSignInRequired = { reason ->
                        guestPromptReason = reason
                        showGuestPrompt = true
                    },
                )
            }
        }
    }

    // ── Guest sign-in prompt sheet ────────────────────────────────────────────
    if (showGuestPrompt) {
        GuestSignInPromptSheet(
            reason = guestPromptReason,
            onSignIn = {
                showGuestPrompt = false
                onSignInRequired()
            },
            onDismiss = { showGuestPrompt = false },
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainTabViewPreview() {
    SuperAppZWTheme {
        MainTabView(onLogout = {})
    }
}
