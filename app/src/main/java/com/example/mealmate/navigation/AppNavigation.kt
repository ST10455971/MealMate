package com.example.mealmate.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.mealmate.screens.DashboardScreen
import com.example.mealmate.screens.LoginScreen
import com.example.mealmate.screens.MealPlannerScreen
import com.example.mealmate.screens.RecipeDetailScreen
import com.example.mealmate.screens.RegisterScreen
import com.example.mealmate.screens.ShoppingListScreen
import com.example.mealmate.screens.SplashScreen
import com.example.mealmate.screens.FavouriteScreen
import com.example.mealmate.screens.SettingsScreen
import com.example.mealmate.screens.LanguageScreen
import com.example.mealmate.data.model.Meal
import com.example.mealmate.ui.viewmodel.MealViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val viewModel: MealViewModel = viewModel()
    val scope = rememberCoroutineScope()
    
    var currentScreen by remember {
        mutableStateOf("splash")
    }

    var selectedMeal by remember {
        mutableStateOf<Meal?>(null)
    }

    // Track the target slot for changing a meal
    var targetSlot by remember {
        mutableStateOf<Pair<String, String>?>(null)
    }

    // Listen to Firebase Auth state to force UI updates on login/logout
    var firebaseUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
    
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            firebaseUser = auth.currentUser
        }
        FirebaseAuth.getInstance().addAuthStateListener(listener)
        onDispose {
            FirebaseAuth.getInstance().removeAuthStateListener(listener)
        }
    }

    val favouriteMeals by viewModel.favorites.collectAsState()
    val mealPlans by viewModel.mealPlans.collectAsState()

    when (currentScreen) {

        "splash" -> SplashScreen {
            currentScreen = if (firebaseUser != null) "dashboard" else "login"
        }

        "login" -> LoginScreen(

            onRegisterClick = {
                currentScreen = "register"
            },

            onLoginSuccess = {
                currentScreen = "dashboard"
            }

        )

        "register" -> RegisterScreen(
            onBackClick = {
                currentScreen = "login"
            }
        )

        "dashboard" -> DashboardScreen(
            viewModel = viewModel,
            onRecipeClick = { meal ->
                scope.launch {
                    val fullMeal = if (meal.strInstructions == null) {
                        viewModel.getFullMealDetails(meal.idMeal) ?: meal
                    } else {
                        meal
                    }
                    selectedMeal = fullMeal
                    currentScreen = "recipeDetail"
                }
            },
            onNavigate = { 
                if (it != "dashboard") {
                    targetSlot = null
                }
                currentScreen = it 
            }
        )

        "recipeDetail" -> {

            selectedMeal?.let { meal ->

                RecipeDetailScreen(

                    meal = meal,
                    preselectedSlot = targetSlot,

                    onBack = {
                        currentScreen = "dashboard"
                    },

                    onFavourite = {
                        if (!favouriteMeals.contains(meal)) {
                            viewModel.addFavorite(meal)
                        } else {
                            viewModel.removeFavorite(meal)
                        }
                    },

                    onMealPlan = { day, type, mealToAdd ->
                        viewModel.addMealPlan(day, type, mealToAdd)
                        targetSlot = null
                        currentScreen = "mealPlanner"
                    }

                )

            }

        }

        "mealPlanner" -> MealPlannerScreen(
            mealPlans = mealPlans,
            onAddMeal = { day, type, _ ->
                targetSlot = day to type
                currentScreen = "dashboard"
            },
            onRemoveMeal = { day, type ->
                viewModel.removeMealPlan(day, type)
            },
            onRecipeClick = { meal ->
                scope.launch {
                    val fullMeal = if (meal.strInstructions == null) {
                        viewModel.getFullMealDetails(meal.idMeal) ?: meal
                    } else {
                        meal
                    }
                    selectedMeal = fullMeal
                    currentScreen = "recipeDetail"
                }
            },
            onBack = {
                currentScreen = "dashboard"
            },
            onNavigateToShoppingList = {
                currentScreen = "shopping"
            },
            onNavigate = { currentScreen = it }
        )

        "shopping" -> ShoppingListScreen(
            mealPlans = mealPlans,
            onBack = {
                currentScreen = "mealPlanner"
            },
            onNavigate = { currentScreen = it }
        )

        "favourites" -> FavouriteScreen(
            favourites = favouriteMeals,
            onRecipeClick = { meal ->
                scope.launch {
                    val fullMeal = if (meal.strInstructions == null) {
                        viewModel.getFullMealDetails(meal.idMeal) ?: meal
                    } else {
                        meal
                    }
                    selectedMeal = fullMeal
                    currentScreen = "recipeDetail"
                }
            },
            onNavigate = { currentScreen = it }
        )

        "settings" -> SettingsScreen(
            user = firebaseUser,
            onLogout = {
                FirebaseAuth.getInstance().signOut()
                currentScreen = "login"
            },
            onNavigate = { currentScreen = it }
        )

        "language" -> LanguageScreen(
            onBack = {
                currentScreen = "settings"
            },
            onNavigate = { currentScreen = it }
        )
    }
}
