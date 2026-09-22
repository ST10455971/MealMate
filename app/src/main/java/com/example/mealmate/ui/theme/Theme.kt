package com.example.mealmate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ForestGreen,
    secondary = DeepGreen,
    tertiary = WarmGray,
    background = Charcoal,
    surface = NearBlack,
    onPrimary = White,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    secondary = DeepGreen,
    tertiary = WarmGray,
    background = CreamBackground,
    surface = White,
    onPrimary = White,
    onBackground = NearBlack,
    onSurface = NearBlack
)

@Composable
fun MealMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
