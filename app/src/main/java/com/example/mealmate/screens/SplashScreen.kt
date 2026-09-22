package com.example.mealmate.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealmate.R
import com.example.mealmate.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(2500.milliseconds)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ForestGreen),
        contentAlignment = Alignment.Center,
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Surface(
                modifier = Modifier.size(150.dp),
                shape = RoundedCornerShape(32.dp),
                color = White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.mealmate_logo),
                        contentDescription = "MealMate Logo",
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MealMate",
                style = TextStyle(
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your Daily Meal Companion",
                style = MaterialTheme.typography.bodyLarge,
                color = White.copy(alpha = 0.9f)
            )
        }
    }
}
