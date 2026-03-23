package com.superappzw.navigation

import androidx.compose.material3.Text
import com.superappzw.ui.accountStatus.PreLoadView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.superappzw.services.AppTermsService.GateStatus
import com.superappzw.ui.accountStatus.BannedAccountView
import com.superappzw.ui.accountStatus.ForceUpdateView
import com.superappzw.ui.accountStatus.SuspendedAccountView
import com.superappzw.ui.accountStatus.UpdatedTermsView
import com.superappzw.ui.components.utils.LoadingView
import com.superappzw.ui.onboarding.GetStartedScreen
import com.superappzw.ui.onboarding.OnboardingScreen
import com.superappzw.ui.screens.ForgotPasswordView
import com.superappzw.ui.screens.SignInView
import com.superappzw.ui.screens.SignUpView

@Composable
fun AppNavigation(
    authStateManager: AuthStateManager = viewModel()
) {
    val navController = rememberNavController()
    val authState  by authStateManager.authState.collectAsState()
    val gateStatus by authStateManager.gateStatus.collectAsState()
    val isGuest    by authStateManager.isGuest.collectAsState()

    // ── Network alert ─────────────────────────────────────────────────────────
    val networkMonitor      = remember { authStateManager.networkMonitor }
    val showNoInternetAlert by networkMonitor.showNoConnectionAlert.collectAsState()

    // ── Navigation driven by auth + guest state ───────────────────────────────
    LaunchedEffect(authState, isGuest) {
        when {
            // Guest takes priority over unauthenticated
            isGuest -> {
                navController.navigate("guest") {
                    popUpTo(0) { inclusive = true }
                }
            }
            authState is AuthState.Authenticated -> {
                navController.navigate("authenticated") {
                    popUpTo(0) { inclusive = true }
                }
            }
            authState is AuthState.Unauthenticated -> {
                navController.navigate("onboarding") {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> Unit
        }
    }

    // ── No internet alert ─────────────────────────────────────────────────────
    if (showNoInternetAlert) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { networkMonitor.dismissAlert() },
            title = { Text("No Internet Connection") },
            text  = { Text("Please check your connection and try again.") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = { networkMonitor.dismissAlert() },
                ) { Text("OK") }
            },
        )
    }

    NavHost(
        navController = navController,
        startDestination = "loading",
    ) {

        // ── Initial loading splash ────────────────────────────────────────────
        composable("loading") {
            LoadingView()
        }

        // ── Unauthenticated screens ───────────────────────────────────────────
        composable("onboarding") {
            OnboardingScreen(
                onGetStartedClick = { navController.navigate("getStarted") },
                onBrowseAsGuest = { authStateManager.continueAsGuest() },
            )
        }

        composable("getStarted") {
            GetStartedScreen(
                navigateToEmail = { navController.navigate("signup") },
                navigateToGoogle = {},
                navigateBack = { navController.popBackStack() },
                onSignInSuccess = {},
            )
        }

        composable("signup") {
            SignUpView(
                onSignInClick = { navController.navigate("signIn") },
                navigateBack = { navController.popBackStack() },
            )
        }

        composable("signIn") {
            SignInView(
                onResetClick = { navController.navigate("reset") },
                navigateBack = { navController.popBackStack() },
            )
        }

        composable("reset") {
            ForgotPasswordView(
                navigateBack = { navController.popBackStack() },
            )
        }

        // ── Guest route — full app without auth-gated features ────────────────
        composable("guest") {
            MainTabView(
                onLogout = { authStateManager.logout() },
                dailyLanguage = null,       // guests get no greeting
                currentUserName = null,
                currentUserPhotoUrl = null,
                isGuest = true,
                onSignInRequired = {
                    // Exit guest mode and go to onboarding
                    authStateManager.exitGuestMode()
                    navController.navigate("onboarding") {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        // ── Authenticated root — gate switch ──────────────────────────────────
        composable("authenticated") {
            when (gateStatus) {
                null -> PreLoadView()

                GateStatus.FORCE_UPDATE -> ForceUpdateView()

                GateStatus.BANNED -> BannedAccountView()

                GateStatus.SUSPENDED -> SuspendedAccountView()

                GateStatus.UPDATED_TERMS -> UpdatedTermsView(
                    onAcknowledged = { authStateManager.checkGate() },
                )

                GateStatus.CLEAR -> {
                    val dailyLanguage       by authStateManager.dailyLanguage.collectAsState()
                    val currentUserName     by authStateManager.currentUserName.collectAsState()
                    val currentUserPhotoUrl by authStateManager.currentUserPhotoUrl.collectAsState()

                    MainTabView(
                        onLogout = { authStateManager.logout() },
                        dailyLanguage = dailyLanguage,
                        currentUserName = currentUserName,
                        currentUserPhotoUrl = currentUserPhotoUrl,
                        isGuest = false,
                        onSignInRequired = {},  // not needed for authenticated users
                    )
                }
            }
        }
    }
}
