package com.superappzw.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.superappzw.ui.components.utils.LoadingView
import com.superappzw.ui.onboarding.GetStartedScreen
import com.superappzw.ui.onboarding.OnboardingScreen
import com.superappzw.ui.screens.ForgotPasswordView
import com.superappzw.ui.screens.SignInView
import com.superappzw.ui.screens.SignUpView

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
        composable("loading") {
            LoadingView()
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
                navigateToGoogle = { /* Google sign-in is handled internally by GetStartedScreen */ },
                navigateBack = { navController.popBackStack() },
                onSignInSuccess = {
                    // AuthStateManager will trigger navigation via LaunchedEffect above
                }
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
            )
        }

        composable("reset") {
            ForgotPasswordView(
                navigateBack = { navController.popBackStack() }
            )
        }

        // AUTHENTICATED screens
        composable("home") {
            val dailyLanguage by authStateManager.dailyLanguage.collectAsState()
            val currentUserName by authStateManager.currentUserName.collectAsState()
            val currentUserPhotoUrl by authStateManager.currentUserPhotoUrl.collectAsState()

            MainTabView(
                onLogout = { authStateManager.logout() },
                dailyLanguage = dailyLanguage,
                currentUserName = currentUserName,
                currentUserPhotoUrl = currentUserPhotoUrl,
            )
        }
    }
}
