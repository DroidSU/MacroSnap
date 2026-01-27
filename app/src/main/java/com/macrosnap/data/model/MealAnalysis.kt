package com.macrosnap.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MealAnalysis(
    val dishName: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fats: Int,
    val healthierSwap: String? = null,
    val portionTweak: String? = null
)
