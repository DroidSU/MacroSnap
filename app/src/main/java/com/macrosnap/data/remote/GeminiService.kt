package com.macrosnap.data.remote

import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.macrosnap.BuildConfig
import com.macrosnap.data.model.MealAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GeminiService {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
    }

    suspend fun analyzeMeal(bitmap: Bitmap): MealAnalysis? = withContext(Dispatchers.IO) {
        val prompt = """
            Analyze this image of an Indian meal. 
            Identify the dish name and estimate the total calories, protein, carbs, and fats.
            Provide a healthier swap if possible and a portion tweak recommendation.
            Return the result in JSON format with the following keys:
            dishName (string), calories (int), protein (int), carbs (int), fats (int), healthierSwap (string), portionTweak (string).
            
            IMPORTANT: Return ONLY the raw JSON object. Do not include markdown formatting or any other text.
        """.trimIndent()

        val inputContent = content {
            image(bitmap)
            text(prompt)
        }

        try {
            val response = generativeModel.generateContent(inputContent)
            var responseText = response.text?.trim() ?: return@withContext null
            
            // Clean markdown formatting (e.g., ```json ... ```) if the model includes it
            if (responseText.startsWith("```")) {
                responseText = responseText
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()
            }

            json.decodeFromString<MealAnalysis>(responseText)
        } catch (e: Exception) {
            Log.e("GeminiService", "Analysis failed: ${e.message}", e)
            null
        }
    }
}
