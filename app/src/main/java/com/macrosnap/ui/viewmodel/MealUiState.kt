package com.macrosnap.ui.viewmodel

import com.macrosnap.data.model.MealAnalysis

sealed class MealUiState {
    data object Idle : MealUiState()
    data object Loading : MealUiState()
    data class Success(val analysis: MealAnalysis) : MealUiState()
    data class Error(val message: String) : MealUiState()
}
