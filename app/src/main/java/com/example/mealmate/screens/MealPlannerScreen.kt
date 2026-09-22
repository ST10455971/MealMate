package com.example.mealmate.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerScreen(
    mealPlans: Map<String, Map<String, Meal>>,
    onAddMeal: (String, String, Meal?) -> Unit,
    onRemoveMeal: (String, String) -> Unit,
    onRecipeClick: (Meal) -> Unit,
    onBack: () -> Unit,
    onNavigateToShoppingList: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val days = remember {
        val currentWeekDays = mutableListOf<DayData>()
        val cal = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val numberFormat = SimpleDateFormat("d", Locale.getDefault())
        val fullFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        for (i in 0 until 7) {
            currentWeekDays.add(
                DayData(
                    name = dayFormat.format(cal.time),
                    number = numberFormat.format(cal.time),
                    fullName = fullFormat.format(cal.time)
                )
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        currentWeekDays
    }

    val todayShort = remember { SimpleDateFormat("EEE", Locale.getDefault()).format(Date()) }
    var selectedDayShort by remember { mutableStateOf(todayShort) }
    val selectedDayFull = days.find { it.name == selectedDayShort }?.fullName ?: "Monday"

    Scaffold(
        containerColor = CreamBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Meal Planner",
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
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            BottomNavBar(
                currentScreen = "mealPlanner",
                onNavigate = { onNavigate(it) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                items(days) { day ->
                    DayItem(
                        day = day,
                        isSelected = selectedDayShort == day.name,
                        onClick = { selectedDayShort = day.name }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                val slots = listOf(
                    MealSlot("Breakfast"),
                    MealSlot("Lunch"),
                    MealSlot("Dinner"),
                    MealSlot("Dessert")
                )
                
                items(slots) { slot ->
                    val meal = mealPlans[selectedDayFull]?.get(slot.name)
                    MealSlotCard(
                        slot = slot,
                        meal = meal,
                        onRecipeClick = { meal?.let { onRecipeClick(it) } },
                        onChangeClick = { onAddMeal(selectedDayFull, slot.name, null) },
                        onRemoveClick = { onRemoveMeal(selectedDayFull, slot.name) }
                    )
                }
            }
        }
    }
}

@Composable
fun DayItem(day: DayData, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) ForestGreen else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(
            text = day.name,
            fontSize = 14.sp,
            color = if (isSelected) White else WarmGray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = day.number,
            fontSize = 18.sp,
            color = if (isSelected) White else NearBlack,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MealSlotCard(
    slot: MealSlot,
    meal: Meal?,
    onRecipeClick: () -> Unit,
    onChangeClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4EDF8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, LightBeige)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = LightMint,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = slot.name,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 14.sp,
                        color = ForestGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                if (meal != null) {
                    IconButton(onClick = onRemoveClick) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Remove",
                            tint = Color.Red.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (meal != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = meal.strMealThumb,
                        contentDescription = meal.strMeal,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = meal.strMeal ?: "Unknown Meal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NearBlack
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Row(
                                modifier = Modifier.clickable { onRecipeClick() },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = ForestGreen
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Recipe", color = ForestGreen, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Row(
                                modifier = Modifier.clickable { onChangeClick() },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Sync,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MediumGray
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Change", color = MediumGray, fontSize = 14.sp)
                            }
                        }
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onChangeClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen),
                    border = BorderStroke(1.dp, LightBeige)
                ) {
                    Text("Add a meal", color = NearBlack)
                }
            }
        }
    }
}

data class DayData(val name: String, val number: String, val fullName: String)
data class MealSlot(val name: String)
