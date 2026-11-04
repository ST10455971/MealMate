package com.example.mealmate.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mealmate.R
import com.example.mealmate.data.model.Meal
import com.example.mealmate.databinding.ItemRecipeBinding

class RecipeAdapter(
    private var recipes: List<Meal> = emptyList(),
    private var favorites: List<Meal> = emptyList(),
    private val onRecipeClick: (Meal) -> Unit,
    private val onFavoriteClick: (Meal) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    fun updateData(newRecipes: List<Meal>, newFavorites: List<Meal>) {
        recipes = newRecipes
        favorites = newFavorites
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(meal: Meal) {
            binding.tvRecipeName.text = meal.strMeal
            binding.tvRecipeCategory.text = listOfNotNull(meal.strCategory, meal.strArea).joinToString(" • ")
            binding.ivRecipeImage.load(meal.strMealThumb)

            val isFav = favorites.any { it.idMeal == meal.idMeal }
            binding.btnFavorite.setImageResource(
                if (isFav) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            )

            binding.cardRecipe.setOnClickListener { onRecipeClick(meal) }
            binding.btnFavorite.setOnClickListener { onFavoriteClick(meal) }
        }
    }
}
