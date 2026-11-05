package com.example.mealmate.utils

import org.junit.Assert.*
import org.junit.Test

class MealMateInputValidatorTest {

    @Test
    fun isValidEmail_validEmail_returnsTrue() {
        assertTrue(MealMateInputValidator.isValidEmail("user@example.com"))
        assertTrue(MealMateInputValidator.isValidEmail("pete.mark@mealmate.co.za"))
    }

    @Test
    fun isValidEmail_invalidEmail_returnsFalse() {
        assertFalse(MealMateInputValidator.isValidEmail("invalid-email"))
        assertFalse(MealMateInputValidator.isValidEmail("user@.com"))
        assertFalse(MealMateInputValidator.isValidEmail(""))
    }

    @Test
    fun isValidPassword_sixOrMoreChars_returnsTrue() {
        assertTrue(MealMateInputValidator.isValidPassword("password123"))
        assertTrue(MealMateInputValidator.isValidPassword("123456"))
    }

    @Test
    fun isValidPassword_lessThanSixChars_returnsFalse() {
        assertFalse(MealMateInputValidator.isValidPassword("12345"))
        assertFalse(MealMateInputValidator.isValidPassword(""))
    }

    @Test
    fun doPasswordsMatch_matchingPasswords_returnsTrue() {
        assertTrue(MealMateInputValidator.doPasswordsMatch("Secret123!", "Secret123!"))
    }

    @Test
    fun doPasswordsMatch_differentPasswords_returnsFalse() {
        assertFalse(MealMateInputValidator.doPasswordsMatch("Secret123!", "Secret456!"))
    }

    @Test
    fun isValidServingSize_withinRange_returnsTrue() {
        assertTrue(MealMateInputValidator.isValidServingSize(1))
        assertTrue(MealMateInputValidator.isValidServingSize(4))
        assertTrue(MealMateInputValidator.isValidServingSize(50))
    }

    @Test
    fun isValidServingSize_outsideRange_returnsFalse() {
        assertFalse(MealMateInputValidator.isValidServingSize(0))
        assertFalse(MealMateInputValidator.isValidServingSize(-5))
        assertFalse(MealMateInputValidator.isValidServingSize(51))
    }

    @Test
    fun calculateIngredientAmount_validInputs_returnsScaledAmount() {
        val originalAmount = 200.0
        val originalServings = 2
        val newServings = 4

        val result = MealMateInputValidator.calculateIngredientAmount(
            originalAmount,
            originalServings,
            newServings
        )

        assertEquals(400.0, result, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun calculateIngredientAmount_invalidServings_throwsException() {
        MealMateInputValidator.calculateIngredientAmount(200.0, 2, 0)
    }
}
