package com.example.mealmate.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.MainActivity
import com.example.mealmate.adapters.RecipeAdapter
import com.example.mealmate.adapters.TrendingAdapter
import com.example.mealmate.data.model.Meal
import com.example.mealmate.data.repository.RecipeRepository
import com.example.mealmate.databinding.FragmentDashboardBinding
import com.example.mealmate.ui.viewmodel.MealViewModel
import com.example.mealmate.utils.PreferenceHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MealViewModel by activityViewModels()

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var trendingAdapter: TrendingAdapter

    private var searchJob: Job? = null
    private val trendingCategories = listOf("Beef", "Pasta", "Seafood", "Dessert", "Chicken", "Pork", "Lamb", "Side")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(true)

        setupRecyclerViews()
        setupSearchAndFilters()
        loadHourlyTrending()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        recipeAdapter = RecipeAdapter(
            onRecipeClick = { meal ->
                (activity as? MainActivity)?.replaceFragment(
                    RecipeDetailFragment.newInstance(meal.idMeal)
                )
            },
            onFavoriteClick = { meal ->
                val currentFavs = viewModel.favorites.value
                if (currentFavs.any { it.idMeal == meal.idMeal }) {
                    viewModel.removeFavorite(meal)
                } else {
                    viewModel.addFavorite(meal)
                }
            }
        )

        binding.rvRecipes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecipes.adapter = recipeAdapter

        trendingAdapter = TrendingAdapter { meal ->
            (activity as? MainActivity)?.replaceFragment(
                RecipeDetailFragment.newInstance(meal.idMeal)
            )
        }

        binding.rvTrendingRecipes.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTrendingRecipes.adapter = trendingAdapter
    }

    private fun setupSearchAndFilters() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(300)
                    if (query.length >= 2) {
                        viewModel.searchRecipes(query)
                    } else if (query.isEmpty()) {
                        viewModel.searchRecipes("Chicken")
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipChicken.setOnClickListener { viewModel.searchRecipes("Chicken") }
        binding.chipBeef.setOnClickListener { viewModel.searchRecipes("Beef") }
        binding.chipSeafood.setOnClickListener { viewModel.searchRecipes("Seafood") }
        binding.chipVegetarian.setOnClickListener { viewModel.searchRecipes("Vegetarian") }
        binding.chipPasta.setOnClickListener { viewModel.searchRecipes("Pasta") }
    }

    private fun loadHourlyTrending() {
        val currentHourIndex = ((System.currentTimeMillis() / (1000 * 3600)) % trendingCategories.size).toInt()
        val trendingQuery = trendingCategories[currentHourIndex]

        lifecycleScope.launch {
            try {
                val repository = RecipeRepository()
                val response = repository.searchRecipes(trendingQuery)
                val meals = response.meals ?: emptyList()
                if (meals.isNotEmpty()) {
                    trendingAdapter.updateMeals(meals.take(6))
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun filterByDietaryPreferences(meals: List<Meal>): List<Meal> {
        val prefs = PreferenceHelper.getDietaryPreferences(requireContext())
        if (prefs.isEmpty()) return meals

        val filtered = meals.filter { meal ->
            val text = "${meal.strMeal} ${meal.strCategory} ${meal.strArea} ${meal.strInstructions}".lowercase()
            prefs.all { pref ->
                when (pref.lowercase()) {
                    "vegetarian" -> !text.contains("beef") && !text.contains("pork") && !text.contains("lamb")
                    "vegan" -> !text.contains("beef") && !text.contains("chicken") && !text.contains("pork") && !text.contains("cheese") && !text.contains("milk")
                    "halal" -> !text.contains("pork") && !text.contains("bacon") && !text.contains("wine")
                    "gluten-free" -> !text.contains("flour") && !text.contains("pasta") && !text.contains("bread")
                    else -> true
                }
            }
        }
        return if (filtered.isNotEmpty()) filtered else meals
    }

    private fun observeViewModel() {
        if (viewModel.searchResults.value.isEmpty()) {
            viewModel.searchRecipes("Chicken")
        }

        lifecycleScope.launch {
            viewModel.searchResults.collect { results ->
                val favs = viewModel.favorites.value
                val filteredList = filterByDietaryPreferences(results)
                recipeAdapter.updateData(filteredList, favs)
            }
        }

        lifecycleScope.launch {
            viewModel.favorites.collect { favs ->
                val results = viewModel.searchResults.value
                val filteredList = filterByDietaryPreferences(results)
                recipeAdapter.updateData(filteredList, favs)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect { err ->
                if (err != null && viewModel.searchResults.value.isEmpty()) {
                    binding.tvError.text = err
                    binding.tvError.visibility = View.VISIBLE
                } else {
                    binding.tvError.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
