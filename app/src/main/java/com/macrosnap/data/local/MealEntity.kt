package com.macrosnap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dishName: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fats: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val imagePath: String? = null
)
