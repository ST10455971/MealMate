package com.example.mealmate.data.api

import com.example.mealmate.data.model.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApiService {

    @GET("search.php")
    suspend fun searchMeals(
        @Query("s") search: String
    ): RecipeResponse

    @GET("filter.php")
    suspend fun filterByCategory(
        @Query("c") category: String
    ): RecipeResponse

    @GET("filter.php")
    suspend fun filterByArea(
        @Query("a") area: String
    ): RecipeResponse

    @GET("lookup.php")
    suspend fun getMealById(
        @Query("i") id: String
    ): RecipeResponse
}
