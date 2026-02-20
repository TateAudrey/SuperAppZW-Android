package com.superappzw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.superappzw.navigation.AppNavigation
import com.superappzw.navigation.AuthStateManager
import com.superappzw.ui.onboarding.OnboardingScreen
import com.superappzw.ui.theme.SuperAppZWTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase already auto-initializes with google-services.json
        // Remove manual FirebaseApp.initializeApp(this) - not needed!

        setContent {
            SuperAppZWTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authStateManager = viewModel<AuthStateManager>()
                    AppNavigation(authStateManager = authStateManager)
                }
            }
        }
    }
}

