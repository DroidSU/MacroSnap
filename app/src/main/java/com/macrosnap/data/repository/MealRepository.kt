package com.macrosnap.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.macrosnap.data.local.MealDao
import com.macrosnap.data.local.MealEntity
import com.macrosnap.data.model.MealAnalysis
import com.macrosnap.data.remote.GeminiService
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class MealRepository(
    private val geminiService: GeminiService,
    private val mealDao: MealDao,
    private val context: Context
) {

    fun getAllMeals(): Flow<List<MealEntity>> = mealDao.getAllMeals()

    fun getWeeklyMeals(): Flow<List<MealEntity>> {
        val oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        return mealDao.getWeeklyMeals(oneWeekAgo)
    }

    suspend fun analyzeMeal(bitmap: Bitmap): MealAnalysis? {
        return geminiService.analyzeMeal(bitmap)
    }

    suspend fun saveMeal(analysis: MealAnalysis, bitmap: Bitmap?) {
        val imagePath = bitmap?.let { saveImageToInternalStorage(it) }
        val meal = MealEntity(
            dishName = analysis.dishName,
            calories = analysis.calories,
            protein = analysis.protein,
            carbs = analysis.carbs,
            fats = analysis.fats,
            imagePath = imagePath
        )
        mealDao.insertMeal(meal)
    }

    suspend fun deleteMeal(meal: MealEntity) {
        meal.imagePath?.let { path ->
            File(path).delete()
        }
        mealDao.deleteMeal(meal)
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String? {
        return try {
            val filename = "${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, filename)
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
