package com.superappzw.navigation

import com.superappzw.ui.accountStatus.PreLoadView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    // ── Auth-level navigation ─────────────────────────────────────────────────
    // Mirrors RootView's .onChange(of: appSession.isAuthenticated)
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate("authenticated") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate("onboarding") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Loading -> Unit
        }
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

        // ── Authenticated root — gate switch ──────────────────────────────────
        // Mirrors RootView's switch on gateStatus
        composable("authenticated") {
            when (gateStatus) {

                // null = gate check not yet complete — show PreLoadView
                null -> PreLoadView()

                GateStatus.FORCE_UPDATE -> ForceUpdateView()

                GateStatus.BANNED -> BannedAccountView()

                GateStatus.SUSPENDED -> SuspendedAccountView()

                GateStatus.UPDATED_TERMS -> UpdatedTermsView(
                    onAcknowledged = {
                        // Re-run gate after terms accepted —
                        // mirrors UpdatedTermsView { Task { await checkGate() } }
                        authStateManager.checkGate()
                    },
                )

                GateStatus.CLEAR -> {
                    val dailyLanguage      by authStateManager.dailyLanguage.collectAsState()
                    val currentUserName    by authStateManager.currentUserName.collectAsState()
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
    }
}
