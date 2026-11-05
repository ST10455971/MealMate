package com.example.mealmate.fragments

import com.example.mealmate.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.mealmate.MainActivity
import com.example.mealmate.data.model.Meal
import com.example.mealmate.databinding.FragmentRecipeDetailBinding
import com.example.mealmate.ui.viewmodel.MealViewModel
import kotlinx.coroutines.launch

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MealViewModel by activityViewModels()

    private var mealId: String? = null
    private var currentMeal: Meal? = null

    private var servingsCount = 2
    private var baseIngredients: List<String> = emptyList()

    companion object {
        private const val ARG_MEAL_ID = "arg_meal_id"

        fun newInstance(mealId: String): RecipeDetailFragment {
            val fragment = RecipeDetailFragment()
            val args = Bundle()
            args.putString(ARG_MEAL_ID, mealId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mealId = arguments?.getString(ARG_MEAL_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(true)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Dessert")

        val dayAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spDay.adapter = dayAdapter

        val typeAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, mealTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spType.adapter = typeAdapter

        mealId?.let { id ->
            lifecycleScope.launch {
                val fullMeal = viewModel.getFullMealDetails(id)
                fullMeal?.let { meal ->
                    currentMeal = meal
                    bindMeal(meal)
                }
            }
            observeRatings(id)
        }

        binding.btnCookingMode.setOnClickListener {
            currentMeal?.let { meal ->
                (activity as? MainActivity)?.replaceFragment(
                    CookingModeFragment.newInstance(meal.strMeal ?: "Recipe", meal.strInstructions ?: "")
                )
            }
        }

        binding.btnDecreaseServings.setOnClickListener {
            if (servingsCount > 1) {
                servingsCount--
                updateServingsDisplay()
            }
        }

        binding.btnIncreaseServings.setOnClickListener {
            if (servingsCount < 10) {
                servingsCount++
                updateServingsDisplay()
            }
        }

        binding.btnAddMealPlan.setOnClickListener {
            currentMeal?.let { meal ->
                val day = binding.spDay.selectedItem.toString()
                val type = binding.spType.selectedItem.toString()
                Log.d("MealPlanner", "Meal added for $day")
                viewModel.addMealPlan(day, type, meal)
                Toast.makeText(requireContext(), "Meal added for $day", Toast.LENGTH_SHORT).show()
                (activity as? MainActivity)?.replaceFragment(MealPlannerFragment())
            }
        }

        binding.btnFavorite.setOnClickListener {
            currentMeal?.let { meal ->
                val favs = viewModel.favorites.value
                val isFav = favs.any { it.idMeal == meal.idMeal }
                if (isFav) {
                    viewModel.removeFavorite(meal)
                    binding.btnFavorite.setImageResource(R.drawable.ic_heart_outline)
                } else {
                    viewModel.addFavorite(meal)
                    binding.btnFavorite.setImageResource(R.drawable.ic_heart_filled)
                }
            }
        }

        binding.btnSubmitReview.setOnClickListener {
            try {
                val id = mealId ?: return@setOnClickListener
                val rating = binding.ratingBar.rating
                val review = binding.etReview.text?.toString()?.trim() ?: ""
                if (review.isEmpty()) {
                    Toast.makeText(requireContext(), "Please write a review comment first", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.addRecipeRating(id, rating, review)
                binding.etReview.setText("")
                Toast.makeText(requireContext(), "Thank you for rating!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("MealMate", "Error submitting review", e)
            }
        }
    }

    private fun bindMeal(meal: Meal) {
        Log.d("MealMateAPI", "Recipe selected: ${meal.strMeal}")

        binding.tvRecipeTitle.text = meal.strMeal
        binding.tvRecipeCategory.text = listOfNotNull(meal.strCategory, meal.strArea).joinToString(" • ")
        binding.ivRecipeImage.load(meal.strMealThumb)
        binding.tvInstructions.text = meal.strInstructions ?: "No instructions available."

        baseIngredients = listOfNotNull(
            meal.strIngredient1, meal.strIngredient2, meal.strIngredient3,
            meal.strIngredient4, meal.strIngredient5, meal.strIngredient6,
            meal.strIngredient7, meal.strIngredient8, meal.strIngredient9,
            meal.strIngredient10
        ).filter { it.isNotBlank() }

        updateServingsDisplay()

        val isFav = viewModel.favorites.value.any { it.idMeal == meal.idMeal }
        binding.btnFavorite.setImageResource(
            if (isFav) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
        )
    }

    private fun updateServingsDisplay() {
        binding.tvServingsCount.text = "$servingsCount servings"
        val multiplier = servingsCount / 2.0
        val scaledList = baseIngredients.map { ingredient ->
            scaleIngredientString(ingredient, multiplier)
        }
        binding.tvIngredientsList.text = scaledList.joinToString("\n• ", prefix = "• ")
    }

    private fun scaleIngredientString(ingredient: String, multiplier: Double): String {
        val regex = Regex("(\\d+(?:\\.\\d+)?)")
        return regex.replace(ingredient) { match ->
            val num = match.value.toDoubleOrNull()
            if (num != null) {
                val scaled = num * multiplier
                if (scaled % 1.0 == 0.0) scaled.toInt().toString() else String.format("%.1f", scaled)
            } else {
                match.value
            }
        }
    }

    private fun observeRatings(id: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRecipeRatings(id).collect { ratings ->
                val currentBinding = _binding ?: return@collect
                if (ratings.isEmpty()) {
                    currentBinding.tvRatingsSummary.text = "No reviews yet. Be the first to review!"
                } else {
                    val avg = ratings.map { it.rating }.average()
                    val summaryText = StringBuilder(String.format("Average Rating: %.1f ⭐ (%d reviews)\n\n", avg, ratings.size))
                    ratings.take(3).forEach { r ->
                        summaryText.append("• ${r.rating.toInt()}★: \"${r.review.ifBlank { "No comment" }}\"\n")
                    }
                    currentBinding.tvRatingsSummary.text = summaryText.toString()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
