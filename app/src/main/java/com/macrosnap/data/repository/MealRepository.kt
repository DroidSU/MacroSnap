package com.macrosnap.data.repository

import android.graphics.Bitmap
import com.macrosnap.data.local.MealDao
import com.macrosnap.data.local.MealEntity
import com.macrosnap.data.model.MealAnalysis
import com.macrosnap.data.remote.GeminiService
import kotlinx.coroutines.flow.Flow

class MealRepository(
    private val geminiService: GeminiService,
    private val mealDao: MealDao
) {
    suspend fun analyzeMeal(bitmap: Bitmap): MealAnalysis? {
        return geminiService.analyzeMeal(bitmap)
    }

    suspend fun saveMeal(analysis: MealAnalysis) {
        val entity = MealEntity(
            dishName = analysis.dishName,
            calories = analysis.calories,
            protein = analysis.protein,
            carbs = analysis.carbs,
            fats = analysis.fats
        )
        mealDao.insertMeal(entity)
    }

    fun getAllMeals(): Flow<List<MealEntity>> {
        return mealDao.getAllMeals()
    }

    fun getWeeklyMeals(): Flow<List<MealEntity>> {
        val oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        return mealDao.getMealsSince(oneWeekAgo)
    }
}
