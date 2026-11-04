package com.example.mealmate.adapters

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.databinding.ItemShoppingHeaderBinding
import com.example.mealmate.databinding.ItemShoppingIngredientBinding

sealed class ShoppingListItem {
    data class Header(val title: String) : ShoppingListItem()
    data class Ingredient(
        val uniqueId: String,
        val ingredientName: String,
        var isChecked: Boolean
    ) : ShoppingListItem()
}

class ShoppingAdapter(
    private var items: List<ShoppingListItem> = emptyList(),
    private val onItemChecked: (String, Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_INGREDIENT = 1
    }

    fun updateItems(newItems: List<ShoppingListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ShoppingListItem.Header -> TYPE_HEADER
            is ShoppingListItem.Ingredient -> TYPE_INGREDIENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            val binding = ItemShoppingHeaderBinding.inflate(inflater, parent, false)
            HeaderViewHolder(binding)
        } else {
            val binding = ItemShoppingIngredientBinding.inflate(inflater, parent, false)
            IngredientViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ShoppingListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is ShoppingListItem.Ingredient -> (holder as IngredientViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class HeaderViewHolder(private val binding: ItemShoppingHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShoppingListItem.Header) {
            binding.tvHeaderTitle.text = item.title
        }
    }

    inner class IngredientViewHolder(private val binding: ItemShoppingIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShoppingListItem.Ingredient) {
            binding.tvIngredientName.text = item.ingredientName
            binding.cbIngredient.setOnCheckedChangeListener(null)
            binding.cbIngredient.isChecked = item.isChecked

            if (item.isChecked) {
                binding.tvIngredientName.paintFlags =
                    binding.tvIngredientName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.tvIngredientName.paintFlags =
                    binding.tvIngredientName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            binding.cbIngredient.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
                if (isChecked) {
                    Log.d("ShoppingList", "Item checked off")
                    binding.tvIngredientName.paintFlags =
                        binding.tvIngredientName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    binding.tvIngredientName.paintFlags =
                        binding.tvIngredientName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                onItemChecked(item.uniqueId, isChecked)
            }
        }
    }
}
