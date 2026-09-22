package com.example.mealmate.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mealmate.ui.theme.*

@Composable
fun BottomNavBar(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = White
    ) {
        val items = listOf(
            NavigationItem("Discover", "dashboard", Icons.Default.Home),
            NavigationItem("Favorites", "favourites", Icons.Default.Favorite),
            NavigationItem("Meal Plan", "mealPlanner", Icons.Default.CalendarMonth),
            NavigationItem("Shopping", "shopping", Icons.Default.ShoppingCart),
            NavigationItem("Settings", "settings", Icons.Default.Settings)
        )

        items.forEach { item ->
            NavigationBarItem(
                selected = currentScreen == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { 
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        color = if (currentScreen == item.route) ForestGreen else MediumGray,
                        fontWeight = if (currentScreen == item.route) FontWeight.Bold else FontWeight.Normal
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ForestGreen,
                    unselectedIconColor = MediumGray,
                    indicatorColor = LightMint
                )
            )
        }
    }
}

data class NavigationItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)
