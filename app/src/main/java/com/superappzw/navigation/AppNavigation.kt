package com.superappzw.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.superappzw.ui.onboarding.GetStartedScreen
import com.superappzw.ui.onboarding.OnboardingScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onGetStartedClick = {
                    navController.navigate("getStarted")
                }
            )
        }

        composable("getStarted") {
            GetStartedScreen(
                navigateToEmail = { navController.navigate("signup") },
                navigateToGoogle = { navController.navigate("googleAuth") },
                navigateBack = {
                    navController.popBackStack()  // Go back to onboarding
                }
            )
        }
    }
}
