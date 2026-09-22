package com.example.mealmate.data.repository

import com.example.mealmate.data.api.RetrofitClient
import com.example.mealmate.data.model.RecipeResponse

class RecipeRepository {

    suspend fun searchRecipes(query: String): RecipeResponse {
        val mappedQuery = when (query.lowercase()) {
            "asian" -> "Chinese"
            "salads" -> "Side"
            "desserts" -> "Dessert"
            "italian" -> "Pasta"
            else -> query
        }

        // 1. Try searching by name
        val nameSearch = RetrofitClient.api.searchMeals(mappedQuery)
        if (!nameSearch.meals.isNullOrEmpty()) return nameSearch
        
        // 2. Try searching by category
        val categorySearch = RetrofitClient.api.filterByCategory(mappedQuery)
        if (!categorySearch.meals.isNullOrEmpty()) return categorySearch
        
        // 3. Try searching by area (place)
        val areaSearch = RetrofitClient.api.filterByArea(mappedQuery)
        if (!areaSearch.meals.isNullOrEmpty()) return areaSearch

        return areaSearch
    }

    suspend fun getMealById(id: String) = RetrofitClient.api.getMealById(id)
}
