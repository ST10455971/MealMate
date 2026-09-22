package com.example.mealmate.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mealmate.data.model.Meal
import com.example.mealmate.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    meal: Meal,
    preselectedSlot: Pair<String, String>? = null,
    onBack: () -> Unit,
    onFavourite: () -> Unit,
    onMealPlan: (String, String, Meal) -> Unit
) {
    val ingredients = remember(meal) { getIngredients(meal) }
    var favouriteSaved by remember { mutableStateOf(false) }
    var mealAdded by remember { mutableStateOf(false) }

    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Dessert")

    var selectedDay by remember { mutableStateOf(preselectedSlot?.first ?: days[0]) }
    var selectedType by remember { mutableStateOf(preselectedSlot?.second ?: mealTypes[0]) }

    var dayExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                AsyncImage(
                    model = meal.strMealThumb,
                    contentDescription = meal.strMeal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                )

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        meal.strMeal ?: "Unknown Meal",
                        style = MaterialTheme.typography.headlineMedium,
                        color = NearBlack
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Category: ${meal.strCategory ?: "Recipe"}", color = Charcoal)
                    Text("Cuisine: ${meal.strArea ?: "International"}", color = Charcoal)

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onFavourite()
                            favouriteSaved = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForestGreen
                        )
                    ) {
                        Icon(Icons.Default.FavoriteBorder, null, tint = White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save to Favourites", color = White)
                    }

                    if (favouriteSaved) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "❤️ Recipe added to favourites!",
                            color = ForestGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    val actionTitle = if (preselectedSlot != null) "Update Weekly Planner" else "Add to Weekly Planner"
                    Text(actionTitle, style = MaterialTheme.typography.titleLarge, color = NearBlack)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (preselectedSlot != null) {
                        // Show where it will be updated
                        Text(
                            text = "Changing: ${preselectedSlot.first} • ${preselectedSlot.second}",
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = dayExpanded,
                        onExpandedChange = { dayExpanded = !dayExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedDay,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Day", color = WarmGray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = LightBeige,
                                focusedBorderColor = ForestGreen
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = dayExpanded,
                            onDismissRequest = { dayExpanded = false }
                        ) {
                            days.forEach { day ->
                                DropdownMenuItem(
                                    text = { Text(day, color = Charcoal) },
                                    onClick = {
                                        selectedDay = day
                                        dayExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Meal Type", color = WarmGray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = LightBeige,
                                focusedBorderColor = ForestGreen
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            mealTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type, color = Charcoal) },
                                    onClick = {
                                        selectedType = type
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onMealPlan(selectedDay, selectedType, meal)
                            mealAdded = true
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen),
                        border = BorderStroke(1.dp, ForestGreen)
                    ) {
                        Icon(Icons.Default.CalendarMonth, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        val buttonText = if (preselectedSlot != null) "Confirm Change" else "Confirm Addition"
                        Text(buttonText, color = NearBlack)
                    }

                    if (mealAdded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Added to $selectedDay $selectedType! 📅",
                            color = ForestGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Ingredients",
                        style = MaterialTheme.typography.titleLarge,
                        color = NearBlack
                    )
                }
            }

            items(ingredients.size) { index ->
                Text(
                    text = "• ${ingredients[index]}",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    color = Charcoal
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        "Cooking Instructions",
                        style = MaterialTheme.typography.titleLarge,
                        color = NearBlack
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(meal.strInstructions ?: "No instructions available.", color = Charcoal)
                }
            }
        }

        // Floating Back Button
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .clickable { onBack() },
            shape = CircleShape,
            color = White.copy(alpha = 0.9f),
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NearBlack,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun getIngredients(meal: Meal): List<String> {
    return listOfNotNull(
        meal.strIngredient1, meal.strIngredient2, meal.strIngredient3,
        meal.strIngredient4, meal.strIngredient5, meal.strIngredient6,
        meal.strIngredient7, meal.strIngredient8, meal.strIngredient9,
        meal.strIngredient10, meal.strIngredient11, meal.strIngredient12,
        meal.strIngredient13, meal.strIngredient14, meal.strIngredient15,
        meal.strIngredient16, meal.strIngredient17, meal.strIngredient18,
        meal.strIngredient19, meal.strIngredient20
    ).filter { it.isNotBlank() }
}
