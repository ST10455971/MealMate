package com.example.mealmate.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealmate.data.local.AppDatabase
import com.example.mealmate.data.model.Meal
import com.example.mealmate.data.repository.MealRepository
import com.example.mealmate.data.repository.RecipeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MealViewModel(application: Application) : AndroidViewModel(application) {

    private val mealRepository: MealRepository
    private val recipeRepository = RecipeRepository()
    private val auth = FirebaseAuth.getInstance()

    // Monitor auth state to provide user-specific data
    private val _userId = MutableStateFlow(auth.currentUser?.uid ?: "")
    
    init {
        val database = AppDatabase.getDatabase(application)
        mealRepository = MealRepository(database.mealDao())
        
        // Update userId flow whenever auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            _userId.value = firebaseAuth.currentUser?.uid ?: ""
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val favorites: StateFlow<List<Meal>> = _userId
        .flatMapLatest { id ->
            if (id.isEmpty()) flowOf(emptyList())
            else mealRepository.getFavorites(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val mealPlans: StateFlow<Map<String, Map<String, Meal>>> = _userId
        .flatMapLatest { id ->
            if (id.isEmpty()) flowOf(emptyMap())
            else mealRepository.getMealPlans(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    var searchResults by mutableStateOf(listOf<Meal>())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val response = recipeRepository.searchRecipes(query)
                searchResults = response.meals ?: emptyList()
                if (searchResults.isEmpty()) {
                    error = "No recipes found for '$query'"
                }
            } catch (e: Exception) {
                error = "Unable to load recipes. Please check your internet connection."
                searchResults = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    suspend fun getFullMealDetails(id: String): Meal? {
        return try {
            val response = recipeRepository.getMealById(id)
            response.meals?.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    fun addFavorite(meal: Meal) {
        val id = _userId.value
        if (id.isNotEmpty()) {
            viewModelScope.launch {
                mealRepository.addFavorite(id, meal)
            }
        }
    }

    fun removeFavorite(meal: Meal) {
        val id = _userId.value
        if (id.isNotEmpty()) {
            viewModelScope.launch {
                mealRepository.removeFavorite(id, meal)
            }
        }
    }

    fun addMealPlan(day: String, type: String, meal: Meal) {
        val id = _userId.value
        if (id.isNotEmpty()) {
            viewModelScope.launch {
                mealRepository.addMealPlan(id, day, type, meal)
            }
        }
    }

    fun removeMealPlan(day: String, type: String) {
        val id = _userId.value
        if (id.isNotEmpty()) {
            viewModelScope.launch {
                mealRepository.removeMealPlan(id, day, type)
            }
        }
    }
}
