package com.example.mealmate.utils

import java.security.MessageDigest

object PasswordUtils {

    fun isPasswordValid(password: String): Boolean {
        val isLengthValid = password.length >= 8
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        return isLengthValid && hasDigit && hasSpecialChar
    }

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
