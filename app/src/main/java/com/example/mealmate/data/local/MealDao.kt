package com.example.mealmate.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getAllFavorites(userId: String): Flow<List<FavoriteMealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(meal: FavoriteMealEntity)

    @Delete
    suspend fun deleteFavorite(meal: FavoriteMealEntity)

    @Query("SELECT * FROM meal_plans WHERE userId = :userId")
    fun getAllMealPlans(userId: String): Flow<List<MealPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(mealPlan: MealPlanEntity)

    @Query("DELETE FROM meal_plans WHERE day = :day AND mealType = :mealType AND userId = :userId")
    suspend fun deleteMealPlan(day: String, mealType: String, userId: String)

    @Query("SELECT * FROM pantry WHERE userId = :userId")
    fun getPantryItems(userId: String): Flow<List<PantryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPantryItem(item: PantryEntity)

    @Delete
    suspend fun deletePantryItem(item: PantryEntity)

    @Query("SELECT * FROM recipe_ratings WHERE recipeId = :recipeId ORDER BY timestamp DESC")
    fun getRecipeRatings(recipeId: String): Flow<List<RecipeRatingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeRating(rating: RecipeRatingEntity)
}
