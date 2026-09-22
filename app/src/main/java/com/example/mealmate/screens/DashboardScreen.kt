package com.example.mealmate.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mealmate.R
import com.example.mealmate.data.model.Meal
import com.example.mealmate.ui.theme.*
import com.example.mealmate.ui.viewmodel.MealViewModel
import java.util.Calendar
import java.util.Locale

@Composable
fun ShimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
}

@Composable
fun RecipeSkeletonCard(modifier: Modifier = Modifier) {
    val brush = ShimmerBrush()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(brush)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .background(brush, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(14.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(14.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    viewModel: MealViewModel,
    onRecipeClick: (Meal) -> Unit,
    onNavigate: (String) -> Unit
) {
    var search by remember { mutableStateOf("") }
    val recipes = viewModel.searchResults
    val isLoading = viewModel.isLoading
    val error = viewModel.error
    
    var isSearchActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (recipes.isEmpty()) {
            viewModel.searchRecipes("Pasta")
        }
    }

    Scaffold(
        containerColor = CreamBackground,
        bottomBar = {
            BottomNavBar(
                currentScreen = "dashboard",
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
            
            if (!isSearchActive) {
                HeaderSection()
                Spacer(modifier = Modifier.height(20.dp))
            }

            SearchBarSection(
                value = search,
                onValueChange = { 
                    search = it
                    if (it.isNotEmpty()) {
                        isSearchActive = true
                        viewModel.searchRecipes(it)
                    } else {
                        isSearchActive = false
                    }
                },
                onClear = {
                    search = ""
                    isSearchActive = false
                }
            )

            if (isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF2F6B4F),
                        strokeWidth = 4.dp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Finding delicious recipes...",
                        color = Color(0xFF2F6B4F),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Please wait a moment 🍽️",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else if (error != null) {
                ErrorState(message = error) {
                    viewModel.searchRecipes(if (search.isEmpty()) "Pasta" else search)
                }
            } else {
                if (isSearchActive) {
                    SearchResultsContent(
                        recipes = recipes,
                        onRecipeClick = onRecipeClick
                    )
                } else {
                    DashboardHomeContent(
                        recipes = recipes,
                        onRecipeClick = onRecipeClick,
                        onCategoryClick = { category ->
                            search = category
                            isSearchActive = true
                            viewModel.searchRecipes(category)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Red.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = Color.Red,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
        ) {
            Text("Retry Connection", color = White)
        }
    }
}

@Composable
fun SearchResultsContent(
    recipes: List<Meal>,
    onRecipeClick: (Meal) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = true,
                    onClick = { /* TODO */ },
                    label = { Text("Under 30 min") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ForestGreen,
                        selectedLabelColor = White,
                        labelColor = Charcoal
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = null
                )
            }
            item {
                FilterChip(
                    selected = false,
                    onClick = { /* TODO */ },
                    label = { Text("Easy") },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(labelColor = Charcoal)
                )
            }
            item {
                FilterChip(
                    selected = false,
                    onClick = { /* TODO */ },
                    label = { Text("Popular") },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(labelColor = Charcoal)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "${recipes.size} Recipes found",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = NearBlack
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recipes) { meal ->
                SearchRecipeCard(meal, onRecipeClick)
            }
        }
    }
}

@Composable
fun DashboardHomeContent(
    recipes: List<Meal>,
    onRecipeClick: (Meal) -> Unit,
    onCategoryClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            CategoriesSection(onCategoryClick)
        }

        item {
            TrendingSection(recipes, onRecipeClick)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Discover Recipes",
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
}

@Composable
fun SearchBarSection(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search ingredients, recipes...", color = WarmGray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MediumGray) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = WarmGray)
                }
            } else {
                IconButton(onClick = { /* Open Filter */ }) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = DeepGreen)
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ForestGreen,
            unfocusedBorderColor = LightBeige,
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            focusedTextColor = NearBlack,
            unfocusedTextColor = NearBlack
        ),
        singleLine = true
    )
}

@Composable
fun CategoriesSection(onCategoryClick: (String) -> Unit) {
    var showMore by remember { mutableStateOf(false) }
    
    val allCategories = listOf(
        "Italian" to "https://www.themealdb.com/images/category/pasta.png",
        "Asian" to "https://www.themealdb.com/images/category/seafood.png",
        "Dessert" to "https://www.themealdb.com/images/category/dessert.png",
        "Salads" to "https://www.themealdb.com/images/category/side.png",
        "Chicken" to "https://www.themealdb.com/images/category/chicken.png",
        "Beef" to "https://www.themealdb.com/images/category/beef.png",
        "Seafood" to "https://www.themealdb.com/images/category/seafood.png",
        "Vegetarian" to "https://www.themealdb.com/images/category/vegetarian.png"
    )
    
    val categories = if (showMore) allCategories else allCategories.take(4)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = NearBlack
            )
            Text(
                text = if (showMore) "See Less" else "See All",
                fontSize = 14.sp,
                color = ForestGreen,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { showMore = !showMore }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(categories) { (name, imageUrl) ->
                CategoryItem(name, imageUrl, onCategoryClick)
            }
        }
    }
}

@Composable
fun CategoryItem(name: String, imageUrl: String, onClick: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick(name) }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .border(2.dp, ForestGreen, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Charcoal)
    }
}

@Composable
fun TrendingSection(recipes: List<Meal>, onRecipeClick: (Meal) -> Unit) {
    // Rotation logic
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val trendingRecipes = remember(recipes, currentHour) {
        if (recipes.size > 4) {
            val startIndex = (currentHour % (recipes.size / 2)) * 2
            recipes.subList(startIndex.coerceAtMost(recipes.size - 4), (startIndex + 4).coerceAtMost(recipes.size))
        } else {
            recipes
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trending Recipes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = NearBlack
            )
            Text(
                text = "Hot Now \uD83D\uDD25",
                fontSize = 14.sp,
                color = ForestGreen,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(trendingRecipes) { meal ->
                SearchRecipeCard(
                    meal = meal, 
                    onRecipeClick = onRecipeClick,
                    modifier = Modifier.width(280.dp)
                )
            }
        }
    }
}

@Composable
fun SearchRecipeCard(
    meal: Meal, 
    onRecipeClick: (Meal) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onRecipeClick(meal) }
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFDF8F4)
        )
    ) {
        Column {
            AsyncImage(
                model = meal.strMealThumb,
                contentDescription = meal.strMeal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = meal.strMeal ?: "Unknown Meal",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF2F6B4F)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Category: ${meal.strCategory ?: "Recipe"}",
                    color = Color.DarkGray
                )

                Text(
                    text = "Cuisine: ${meal.strArea ?: "International"}",
                    color = Color.Gray
                )
            }
        }
    }
}
