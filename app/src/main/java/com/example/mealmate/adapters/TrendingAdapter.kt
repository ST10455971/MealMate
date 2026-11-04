package com.example.mealmate.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mealmate.data.model.Meal
import com.example.mealmate.databinding.ItemTrendingRecipeBinding

class TrendingAdapter(
    private var meals: List<Meal> = emptyList(),
    private val onRecipeClick: (Meal) -> Unit
) : RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder>() {

    fun updateMeals(newMeals: List<Meal>) {
        meals = newMeals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val binding = ItemTrendingRecipeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TrendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.bind(meals[position])
    }

    override fun getItemCount(): Int = meals.size

    inner class TrendingViewHolder(private val binding: ItemTrendingRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(meal: Meal) {
            binding.tvTrendingTitle.text = meal.strMeal
            binding.tvTrendingCategory.text = listOfNotNull(meal.strCategory, meal.strArea).joinToString(" • ")
            binding.ivTrendingImage.load(meal.strMealThumb)

            binding.cardTrending.setOnClickListener { onRecipeClick(meal) }
        }
    }
}
