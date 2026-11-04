package com.example.mealmate.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleHelper {

    fun setLocale(context: Context, languageCode: String) {
        val appLocales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocales)
    }

    fun getLanguageDisplayName(code: String): String {
        return when (code) {
            "zu" -> "isiZulu"
            "xh" -> "isiXhosa"
            "af" -> "Afrikaans"
            "st" -> "Sesotho"
            else -> "English"
        }
    }
}
