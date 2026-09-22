package com.example.mealmate.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {
    @Insert
    suspend fun insertMeal(meal: MealPlanEntity)

    @Delete
    suspend fun deleteMeal(meal: MealPlanEntity)

    @Query("SELECT * FROM meal_plan")
    fun getMeals(): Flow<List<MealPlanEntity>>
}
