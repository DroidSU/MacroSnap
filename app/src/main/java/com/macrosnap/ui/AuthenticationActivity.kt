package com.macrosnap.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.macrosnap.data.local.MealDatabase
import com.macrosnap.data.remote.GeminiService
import com.macrosnap.data.repository.MealRepository
import com.macrosnap.ui.screen.LoginScreen
import com.macrosnap.ui.theme.MacroSnapTheme
import com.macrosnap.ui.viewmodel.AuthState
import com.macrosnap.ui.viewmodel.AuthViewModel
import com.macrosnap.ui.viewmodel.MacroSnapViewModelFactory

class AuthenticationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = MealDatabase.getDatabase(this)
        val geminiService = GeminiService()
        val repository = MealRepository(geminiService, database.mealDao())
        val viewModelFactory = MacroSnapViewModelFactory(repository)

        setContent {
            MacroSnapTheme {
                val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
                val authState by authViewModel.authState.collectAsState()

                LaunchedEffect(authState) {
                    if (authState is AuthState.Success) {
                        startActivity(Intent(this@AuthenticationActivity, DashboardActivity::class.java))
                        finish()
                    }
                }

                LoginScreen(
                    authState = authState,
                    onGoogleSignIn = {
                        authViewModel.signInWithGoogle(this@AuthenticationActivity)
                    }
                )
            }
        }
    }
}
