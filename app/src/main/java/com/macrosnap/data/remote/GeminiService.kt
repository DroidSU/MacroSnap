package com.macrosnap.data.remote

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.macrosnap.data.model.MealAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GeminiService {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
//        apiKey = BuildConfig.GEMINI_API_KEY,
        apiKey = "",
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        }
    )

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun analyzeMeal(bitmap: Bitmap): MealAnalysis? = withContext(Dispatchers.IO) {
        val prompt = """
            Analyze this image of an Indian meal. 
            Identify the dish name and estimate the total calories, protein, carbs, and fats.
            Provide a healthier swap if possible and a portion tweak recommendation.
            Return the result in JSON format with the following keys:
            dishName (string), calories (int), protein (double), carbs (double), fats (double), healthierSwap (string), portionTweak (string).
        """.trimIndent()

        val inputContent = content {
            image(bitmap)
            text(prompt)
        }

        try {
            val response = generativeModel.generateContent(inputContent)
            response.text?.let { json.decodeFromString<MealAnalysis>(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
