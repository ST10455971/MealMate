package com.example.mealmate.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mealmate.R
import com.example.mealmate.data.model.Meal
import com.example.mealmate.ui.theme.*

@Composable
fun FavouriteScreen(
    favourites: List<Meal>,
    onRecipeClick: (Meal) -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        containerColor = CreamBackground,
        bottomBar = {
            BottomNavBar(
                currentScreen = "favourites",
                onNavigate = { onNavigate(it) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Favorite Recipes",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = NearBlack
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.mealmate_logo),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MealMate",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NearBlack
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (favourites.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No favorite recipes yet.",
                        color = WarmGray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favourites) { meal ->
                        FavoriteRecipeCard(meal, onRecipeClick)
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteRecipeCard(meal: Meal, onRecipeClick: (Meal) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRecipeClick(meal) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = meal.strMealThumb,
                contentDescription = meal.strMeal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = (meal.strCategory ?: "Recipe").uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = getCategoryColor(meal.strCategory ?: "")
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meal.strMeal ?: "Unknown Meal",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    color = NearBlack
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MediumGray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${(10..45).random()}m",
                        fontSize = 13.sp,
                        color = WarmGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "italian" -> Color(0xFFF28C52)
        "seafood" -> Color(0xFF3B7A57) // ForestGreen
        "dessert" -> Color(0xFF2E5A44) // DeepGreen
        "breakfast" -> Color(0xFFF28C52)
        else -> Color(0xFF3B7A57) // ForestGreen
    }
}
