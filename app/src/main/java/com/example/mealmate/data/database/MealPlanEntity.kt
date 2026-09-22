package com.example.mealmate.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_plan")
data class MealPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val day: String,
    val mealType: String,
    val recipeName: String
)
