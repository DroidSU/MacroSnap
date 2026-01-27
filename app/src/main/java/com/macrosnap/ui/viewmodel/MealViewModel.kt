package com.macrosnap.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macrosnap.data.local.MealEntity
import com.macrosnap.data.model.MealAnalysis
import com.macrosnap.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MealViewModel(private val repository: MealRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<MealUiState>(MealUiState.Idle)
    val uiState: StateFlow<MealUiState> = _uiState.asStateFlow()

    private val _capturedImage = MutableStateFlow<Bitmap?>(null)
    val capturedImage: StateFlow<Bitmap?> = _capturedImage.asStateFlow()

    val history: StateFlow<List<MealEntity>> = repository.getAllMeals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyMeals: StateFlow<List<MealEntity>> = repository.getWeeklyMeals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun analyzeMeal(bitmap: Bitmap) {
        _uiState.value = MealUiState.Loading
        _capturedImage.value = bitmap
        viewModelScope.launch {
            try {
                val result = repository.analyzeMeal(bitmap)
                if (result != null) {
                    _uiState.value = MealUiState.Success(result)
                } else {
                    _uiState.value = MealUiState.Error("Failed to analyze image")
                }
            } catch (e: Exception) {
                _uiState.value = MealUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun startLoading() {
        _uiState.value = MealUiState.Loading
    }

    fun saveMeal(analysis: MealAnalysis) {
        viewModelScope.launch {
            repository.saveMeal(analysis)
        }
    }

    fun setCapturedImage(bitmap: Bitmap?) {
        _capturedImage.value = bitmap
    }

    fun resetState() {
        _uiState.value = MealUiState.Idle
        _capturedImage.value = null
    }
}
