package com.example.mealmate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mealmate.data.model.Meal
import com.example.mealmate.databinding.ItemMealPlanSlotBinding

data class MealPlanSlotItem(
    val day: String,
    val type: String,
    val meal: Meal?
)

class MealPlanAdapter(
    private var slots: List<MealPlanSlotItem> = emptyList(),
    private val onAddOrChange: (String, String) -> Unit,
    private val onRemove: (String, String) -> Unit,
    private val onRecipeClick: (Meal) -> Unit
) : RecyclerView.Adapter<MealPlanAdapter.SlotViewHolder>() {

    fun updateSlots(newSlots: List<MealPlanSlotItem>) {
        slots = newSlots
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val binding = ItemMealPlanSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        holder.bind(slots[position])
    }

    override fun getItemCount(): Int = slots.size

    inner class SlotViewHolder(private val binding: ItemMealPlanSlotBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MealPlanSlotItem) {
            binding.tvSlotTitle.text = "${item.day} - ${item.type}"

            if (item.meal != null) {
                binding.tvMealName.text = item.meal.strMeal
                binding.ivMealImage.load(item.meal.strMealThumb)
                binding.ivMealImage.visibility = View.VISIBLE
                binding.btnAddOrChange.text = "Change"
                binding.btnRemoveMeal.visibility = View.VISIBLE

                binding.cardMealSlot.setOnClickListener { onRecipeClick(item.meal) }
            } else {
                binding.tvMealName.text = "No meal planned"
                binding.ivMealImage.visibility = View.GONE
                binding.btnAddOrChange.text = "Add"
                binding.btnRemoveMeal.visibility = View.GONE

                binding.cardMealSlot.setOnClickListener(null)
            }

            binding.btnAddOrChange.setOnClickListener {
                onAddOrChange(item.day, item.type)
            }

            binding.btnRemoveMeal.setOnClickListener {
                onRemove(item.day, item.type)
            }
        }
    }
}
