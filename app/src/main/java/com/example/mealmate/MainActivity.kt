package com.example.mealmate

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mealmate.databinding.ActivityMainBinding
import com.example.mealmate.fragments.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            replaceFragment(SplashFragment(), addToBackStack = false)
        }
    }

    fun showBottomNavigation(show: Boolean) {
        binding.bottomNavigation.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
        
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    replaceFragment(DashboardFragment(), addToBackStack = false)
                    true
                }
                R.id.nav_meal_planner -> {
                    replaceFragment(MealPlannerFragment(), addToBackStack = false)
                    true
                }
                R.id.nav_shopping -> {
                    replaceFragment(ShoppingListFragment(), addToBackStack = false)
                    true
                }
                R.id.nav_favourites -> {
                    replaceFragment(FavouriteFragment(), addToBackStack = false)
                    true
                }
                R.id.nav_settings -> {
                    replaceFragment(SettingsFragment(), addToBackStack = false)
                    true
                }
                else -> false
            }
        }
    }
}
