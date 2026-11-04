package com.example.mealmate.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mealmate.R

object NotificationHelper {

    private const val CHANNEL_ID = "mealmate_alerts_channel"
    private const val CHANNEL_NAME = "MealMate Notifications"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "MealMate Meal Reminders and Recipe Alerts"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(context: Context, title: String, message: String) {
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.mealmate_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % 10000).toInt(), builder.build())
    }

    fun sendRecipeOfTheDay(context: Context) {
        sendNotification(
            context,
            "Recipe of the Day 🍽️",
            "Daily recommendation tailored for you: Honey Garlic Chicken Bowl!"
        )
    }

    fun sendMealPlanReminder(context: Context) {
        sendNotification(
            context,
            "Meal Plan Reminder ⏰",
            "Scheduled alert: It's time to prepare your planned Lunch menu!"
        )
    }

    fun sendSeasonalDishAlert(context: Context) {
        sendNotification(
            context,
            "Seasonal & Holiday Dishes 🎃",
            "Curated collection: Festive South African holiday dishes ideas!"
        )
    }

    fun sendReengagementPrompt(context: Context) {
        sendNotification(
            context,
            "Quick 15-Minute Meals ⚡",
            "Haven't cooked in a few days? Try a fast, delicious 15-minute recipe now."
        )
    }

    fun sendSavedRecipeUpdate(context: Context) {
        sendNotification(
            context,
            "Saved Recipe Update 🌟",
            "A recipe you saved in your Favorites has just been reviewed & featured!"
        )
    }
}
