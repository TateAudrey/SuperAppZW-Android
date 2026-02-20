package com.superappzw.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.superappzw.ui.onboarding.GetStartedScreen
import com.superappzw.ui.onboarding.OnboardingScreen
import com.superappzw.ui.screens.ForgotPasswordView
import com.superappzw.ui.screens.HomeView
import com.superappzw.ui.screens.SignInView
import com.superappzw.ui.screens.SignUpView
import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superappzw.ui.components.utils.LoadingView

@Composable
fun AppNavigation(
    authStateManager: AuthStateManager = viewModel()  // or hiltViewModel()
) {
    val navController = rememberNavController()
    val authState by authStateManager.authState.collectAsState()

    // Auto-navigate based on auth state
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate("onboarding") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Loading -> {
                // Do nothing - stay on loading
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "loading"
    ) {
        // Use YOUR existing LoadingView
        composable("loading") {
            LoadingView()  // ✅ Your custom LoadingView
        }

        // UNAUTHENTICATED screens
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
                navigateBack = { navController.popBackStack() }
            )
        }

        composable("signIn") {
            SignInView(
                onResetClick = { navController.navigate("reset") },
                navigateBack = { navController.popBackStack() }
                // Removed onSignInSuccess - handled by auth listener
            )
        }

        composable("reset") {
            ForgotPasswordView(
                navigateBack = { navController.popBackStack() }
            )
        }

        composable("googleAuth") {
            Text("Google Auth - Coming Soon")
        }

        // AUTHENTICATED screen only
        composable("home") {
            HomeView(
                onLogout = {
                    authStateManager.logout()  // Auto-navigates to onboarding
                }
            )
        }
    }
}
