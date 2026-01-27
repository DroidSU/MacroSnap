package com.macrosnap.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.macrosnap.data.local.MealDatabase
import com.macrosnap.data.remote.GeminiService
import com.macrosnap.data.repository.MealRepository
import com.macrosnap.ui.screen.DashboardScreen
import com.macrosnap.ui.theme.MacroSnapTheme
import com.macrosnap.ui.viewmodel.AuthViewModel
import com.macrosnap.ui.viewmodel.MacroSnapViewModelFactory
import com.macrosnap.ui.viewmodel.MealViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = MealDatabase.getDatabase(this)
        val geminiService = GeminiService()
        val repository = MealRepository(geminiService, database.mealDao())
        val viewModelFactory = MacroSnapViewModelFactory(repository)

        setContent {
            MacroSnapTheme {
                val mealViewModel: MealViewModel = viewModel(factory = viewModelFactory)
                val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
                
                val uiState by mealViewModel.uiState.collectAsState()
                val history by mealViewModel.history.collectAsState()
                val weeklyMeals by mealViewModel.weeklyMeals.collectAsState()

                DashboardScreen(
                    uiState = uiState,
                    history = history,
                    weeklyMeals = weeklyMeals,
                    onAnalyzeMeal = { bitmap -> mealViewModel.analyzeMeal(bitmap) },
                    onSaveMeal = { analysis -> mealViewModel.saveMeal(analysis) },
                    onResetState = { mealViewModel.resetState() },
                    onSignOut = {
                        authViewModel.signOut(this)
                        startActivity(Intent(this, AuthenticationActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}
