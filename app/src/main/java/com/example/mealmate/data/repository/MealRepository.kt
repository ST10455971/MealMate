package com.example.mealmate.data.repository

import com.example.mealmate.data.local.FavoriteMealEntity
import com.example.mealmate.data.local.MealDao
import com.example.mealmate.data.local.MealPlanEntity
import com.example.mealmate.data.local.PantryEntity
import com.example.mealmate.data.local.RecipeRatingEntity
import com.example.mealmate.data.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MealRepository(private val mealDao: MealDao) {

    fun getFavorites(userId: String) = mealDao.getAllFavorites(userId).map { entities ->
        entities.map { it.toMeal() }
    }

    fun getMealPlans(userId: String) = mealDao.getAllMealPlans(userId).map { entities ->
        val map = mutableMapOf<String, MutableMap<String, Meal>>()
        entities.forEach { entity ->
            val dayMap = map.getOrPut(entity.day) { mutableMapOf() }
            dayMap[entity.mealType] = entity.toMeal()
        }
        map
    }

    fun getPantryItems(userId: String): Flow<List<PantryEntity>> =
        mealDao.getPantryItems(userId)

    suspend fun addPantryItem(userId: String, ingredientName: String, quantity: String) {
        mealDao.insertPantryItem(PantryEntity(userId = userId, ingredientName = ingredientName, quantity = quantity))
    }

    suspend fun removePantryItem(item: PantryEntity) {
        mealDao.deletePantryItem(item)
    }

    fun getRecipeRatings(recipeId: String): Flow<List<RecipeRatingEntity>> =
        mealDao.getRecipeRatings(recipeId)

    suspend fun addRecipeRating(recipeId: String, rating: Float, review: String) {
        mealDao.insertRecipeRating(RecipeRatingEntity(recipeId = recipeId, rating = rating, review = review))
    }

    suspend fun addFavorite(userId: String, meal: Meal) {
        mealDao.insertFavorite(meal.toFavoriteEntity(userId))
    }

    suspend fun removeFavorite(userId: String, meal: Meal) {
        mealDao.deleteFavorite(meal.toFavoriteEntity(userId))
    }

    suspend fun addMealPlan(userId: String, day: String, type: String, meal: Meal) {
        mealDao.insertMealPlan(meal.toMealPlanEntity(day, type, userId))
    }

    suspend fun removeMealPlan(userId: String, day: String, type: String) {
        mealDao.deleteMealPlan(day, type, userId)
    }
}

fun Meal.toFavoriteEntity(userId: String) = FavoriteMealEntity(
    idMeal, userId, strMeal, strCategory, strArea, strInstructions, strMealThumb,
    strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
    strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
    strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
    strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
)

fun FavoriteMealEntity.toMeal() = Meal(
    idMeal, strMeal, strCategory, strArea, strInstructions, strMealThumb,
    strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
    strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
    strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
    strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
)

fun Meal.toMealPlanEntity(day: String, type: String, userId: String) = MealPlanEntity(
    day, type, userId, idMeal, strMeal, strCategory, strArea, strInstructions, strMealThumb,
    strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
    strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
    strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
    strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
)

fun MealPlanEntity.toMeal() = Meal(
    idMeal, strMeal, strCategory, strArea, strInstructions, strMealThumb,
    strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
    strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
    strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
    strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
)
