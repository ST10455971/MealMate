package com.example.mealmate.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealmate.data.local.AppDatabase
import com.example.mealmate.data.local.PantryEntity
import com.example.mealmate.data.local.RecipeRatingEntity
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

    private fun getEffectiveUserId(): String {
        val uid = auth.currentUser?.uid
        return if (!uid.isNullOrEmpty()) uid else "default_user"
    }

    private val _userId = MutableStateFlow(getEffectiveUserId())

    private val _searchResults = MutableStateFlow<List<Meal>>(emptyList())
    val searchResults: StateFlow<List<Meal>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        mealRepository = MealRepository(database.mealDao())

        auth.addAuthStateListener { firebaseAuth ->
            _userId.value = firebaseAuth.currentUser?.uid ?: "default_user"
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val favorites: StateFlow<List<Meal>> = _userId
        .flatMapLatest { id ->
            mealRepository.getFavorites(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val mealPlans: StateFlow<Map<String, Map<String, Meal>>> = _userId
        .flatMapLatest { id ->
            mealRepository.getMealPlans(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    @OptIn(ExperimentalCoroutinesApi::class)
    val pantryItems: StateFlow<List<PantryEntity>> = _userId
        .flatMapLatest { id ->
            mealRepository.getPantryItems(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addPantryItem(name: String, quantity: String) {
        val id = _userId.value
        viewModelScope.launch {
            mealRepository.addPantryItem(id, name, quantity)
        }
    }

    fun removePantryItem(item: PantryEntity) {
        viewModelScope.launch {
            mealRepository.removePantryItem(item)
        }
    }

    fun getRecipeRatings(recipeId: String): Flow<List<RecipeRatingEntity>> =
        mealRepository.getRecipeRatings(recipeId)

    fun addRecipeRating(recipeId: String, rating: Float, review: String) {
        viewModelScope.launch {
            mealRepository.addRecipeRating(recipeId, rating, review)
        }
    }

    fun searchRecipes(query: String) {
        Log.d("MealMateAPI", "Searching recipes: $query")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = recipeRepository.searchRecipes(query)
                val list = response.meals ?: emptyList()
                _searchResults.value = list
                if (list.isEmpty()) {
                    _error.value = "No recipes found for '$query'"
                }
            } catch (e: Exception) {
                _error.value = "Unable to load recipes. Please check your internet connection."
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
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
        viewModelScope.launch {
            mealRepository.addFavorite(id, meal)
        }
    }

    fun removeFavorite(meal: Meal) {
        val id = _userId.value
        viewModelScope.launch {
            mealRepository.removeFavorite(id, meal)
        }
    }

    fun addMealPlan(day: String, type: String, meal: Meal) {
        Log.d("MealPlanner", "Meal added for $day")
        val id = _userId.value
        viewModelScope.launch {
            mealRepository.addMealPlan(id, day, type, meal)
        }
    }

    fun removeMealPlan(day: String, type: String) {
        val id = _userId.value
        viewModelScope.launch {
            mealRepository.removeMealPlan(id, day, type)
        }
    }
}
