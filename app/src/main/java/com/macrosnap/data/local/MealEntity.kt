package com.macrosnap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dishName: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fats: Double,
    val timestamp: Long = System.currentTimeMillis()
)
