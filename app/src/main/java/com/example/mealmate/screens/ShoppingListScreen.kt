package com.example.mealmate.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealmate.data.model.Meal
import com.example.mealmate.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    mealPlans: Map<String, Map<String, Meal>>,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    // Generate a flat list of planned meals with their day and type
    val plannedMeals = remember(mealPlans) {
        val list = mutableListOf<PlannedMealEntry>()
        // We iterate through all days and meal types to get every planned meal
        val daysOrder = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val mealTypesOrder = listOf("Breakfast", "Lunch", "Dinner", "Dessert")
        
        daysOrder.forEach { day ->
            mealPlans[day]?.let { slots ->
                mealTypesOrder.forEach { type ->
                    slots[type]?.let { meal ->
                        list.add(
                            PlannedMealEntry(
                                day = day,
                                type = type,
                                meal = meal,
                                ingredients = listOfNotNull(
                                    meal.strIngredient1, meal.strIngredient2, meal.strIngredient3,
                                    meal.strIngredient4, meal.strIngredient5, meal.strIngredient6,
                                    meal.strIngredient7, meal.strIngredient8, meal.strIngredient9,
                                    meal.strIngredient10, meal.strIngredient11, meal.strIngredient12,
                                    meal.strIngredient13, meal.strIngredient14, meal.strIngredient15,
                                    meal.strIngredient16, meal.strIngredient17, meal.strIngredient18,
                                    meal.strIngredient19, meal.strIngredient20
                                ).filter { it.isNotBlank() }
                            )
                        )
                    }
                }
            }
        }
        list
    }

    val checkedIngredients = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        containerColor = CreamBackground,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Shopping List", 
                        fontSize = 24.sp,
                        color = NearBlack, 
                        fontWeight = FontWeight.Bold 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back", 
                            tint = NearBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            BottomNavBar(
                currentScreen = "shopping",
                onNavigate = { onNavigate(it) }
            )
        }
    ) { padding ->
        if (plannedMeals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No ingredients needed yet.", color = Charcoal)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Add meals to your plan to see the list!", style = MaterialTheme.typography.bodySmall, color = WarmGray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
                plannedMeals.forEach { entry ->
                    item {
                        Column(modifier = Modifier.padding(top = 32.dp, bottom = 12.dp)) {
                            Text(
                                text = entry.meal.strMeal ?: "Unknown Recipe",
                                fontSize = 20.sp,
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${entry.day} • ${entry.type}",
                                fontSize = 16.sp,
                                color = WarmGray
                            )
                        }
                    }
                    
                    if (entry.ingredients.isEmpty()) {
                        item {
                            Text(
                                "No ingredients listed for this recipe.",
                                color = WarmGray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                    } else {
                        items(entry.ingredients) { ingredient ->
                            val uniqueId = "${entry.day}-${entry.type}-${entry.meal.idMeal}-$ingredient"
                            val isChecked = checkedIngredients[uniqueId] ?: false
                            
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                color = Color.Transparent
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { checked ->
                                            checkedIngredients[uniqueId] = checked
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = ForestGreen,
                                            uncheckedColor = MediumGray
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = ingredient,
                                        fontSize = 16.sp,
                                        textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                        color = if (isChecked) WarmGray else NearBlack,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            HorizontalDivider(color = LightBeige.copy(alpha = 0.5f))
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

data class PlannedMealEntry(
    val day: String,
    val type: String,
    val meal: Meal,
    val ingredients: List<String>
)
