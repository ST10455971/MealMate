package com.example.mealmate.utils

import android.content.Context

object PreferenceHelper {
    private const val PREF_NAME = "mealmate_prefs"
    private const val KEY_DIETARY = "key_dietary"
    private const val KEY_LOGGED_IN = "key_logged_in"
    private const val KEY_USER_NAME = "key_user_name"
    private const val KEY_USER_EMAIL = "key_user_email"
    private const val KEY_CHECKED_SHOPPING = "key_checked_shopping"

    fun saveDietaryPreferences(context: Context, preferences: Set<String>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_DIETARY, preferences).apply()
    }

    fun getDietaryPreferences(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_DIETARY, emptySet()) ?: emptySet()
    }

    fun setLoggedIn(context: Context, loggedIn: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_LOGGED_IN, false)
    }

    fun saveUserProfile(context: Context, name: String, email: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun getUserName(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_NAME, "") ?: ""
    }

    fun getUserEmail(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun saveCheckedShoppingItems(context: Context, checkedIds: Set<String>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_CHECKED_SHOPPING, checkedIds).apply()
    }

    fun getCheckedShoppingItems(context: Context): MutableSet<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_CHECKED_SHOPPING, emptySet()) ?: emptySet()
        return set.toMutableSet()
    }
}
