package com.macrosnap.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.macrosnap.data.local.MealDatabase
import com.macrosnap.data.remote.GeminiService
import com.macrosnap.data.repository.AuthRepository
import com.macrosnap.data.repository.MealRepository
import com.macrosnap.ui.screen.SplashScreen
import com.macrosnap.ui.theme.MacroSnapTheme
import com.macrosnap.ui.viewmodel.AuthViewModel
import com.macrosnap.ui.viewmodel.MacroSnapViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = MealDatabase.getDatabase(this)
        val geminiService = GeminiService()
        val mealRepository = MealRepository(geminiService, database.mealDao())
        val authRepository = AuthRepository()
        val viewModelFactory = MacroSnapViewModelFactory(mealRepository, authRepository)

        setContent {
            MacroSnapTheme {
                val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
                
                SplashScreen(onTimeout = {
                    val currentUser = authViewModel.currentUser
                    if (currentUser == null) {
                        startActivity(Intent(this, AuthenticationActivity::class.java))
                    } else {
                        startActivity(Intent(this, DashboardActivity::class.java))
                    }
                    finish()
                })
            }
        }
    }
}
