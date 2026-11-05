package com.example.mealmate.utils

object MealMateInputValidator {

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS
            .matcher(email.trim()).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun doPasswordsMatch(
        password: String,
        confirmPassword: String
    ): Boolean {
        return password == confirmPassword
    }

    fun isValidServingSize(servings: Int): Boolean {
        return servings in 1..50
    }

    fun calculateIngredientAmount(
        originalAmount: Double,
        originalServings: Int,
        newServings: Int
    ): Double {
        require(originalAmount >= 0)
        require(originalServings > 0)
        require(isValidServingSize(newServings))

        return originalAmount * newServings / originalServings
    }
}