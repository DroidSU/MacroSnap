package com.macrosnap.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MealAnalysis(
    val dishName: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fats: Double,
    val healthierSwap: String? = null,
    val portionTweak: String? = null
)
