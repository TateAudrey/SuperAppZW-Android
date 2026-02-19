package com.superappzw.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.superappzw.ui.onboarding.OnboardingScreen
import com.superappzw.ui.screens.RegistrationScreen
import com.superappzw.viewModel.AppSessionViewModel

@Composable
fun RootScreen(
    sessionViewModel: AppSessionViewModel = hiltViewModel()
) {
    val isAuthenticated by sessionViewModel.isAuthenticated.collectAsState()

    if (isAuthenticated) {
        // Main App Content
        RegistrationScreen()
    } else {
        // Show onboarding UI only
        OnboardingScreen()
    }
}

