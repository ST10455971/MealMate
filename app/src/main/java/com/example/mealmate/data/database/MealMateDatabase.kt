package com.example.mealmate.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        MealPlanEntity::class,
        ShoppingItemEntity::class
    ],
    version = 1
)
abstract class MealMateDatabase : RoomDatabase() {
    abstract fun mealDao(): MealPlanDao
    abstract fun shoppingDao(): ShoppingDao
}
