package com.superappzw.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.superappzw.ui.onboarding.GetStartedScreen
import com.superappzw.ui.onboarding.OnboardingScreen
import com.superappzw.ui.screens.ForgotPasswordView
import com.superappzw.ui.screens.SignInView
import com.superappzw.ui.screens.SignUpView

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onGetStartedClick = { navController.navigate("getStarted") }
            )
        }

        composable("getStarted") {
            GetStartedScreen(
                navigateToEmail = { navController.navigate("signup") },
                navigateToGoogle = { navController.navigate("googleAuth") },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable("signup") {
            SignUpView(
                onSignInClick = { navController.navigate("signIn") },
                onSignUpSuccess = { navController.navigate("home")},
                navigateBack = { navController.popBackStack() }
            )
        }

        // Add these stubs to avoid crashes:
        composable("signIn") {
            SignInView(
                onSignInSuccess = { navController.navigate("home") },
                onResetClick = { navController.navigate("reset") },
                navigateBack = { navController.popBackStack() }
            )
        }
        composable("reset") {
            ForgotPasswordView(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}

