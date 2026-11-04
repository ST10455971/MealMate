package com.example.mealmate.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.data.local.PantryEntity
import com.example.mealmate.databinding.ItemPantryBinding

class PantryAdapter(
    private var items: List<PantryEntity> = emptyList(),
    private val onRemoveItem: (PantryEntity) -> Unit
) : RecyclerView.Adapter<PantryAdapter.PantryViewHolder>() {

    fun updateItems(newItems: List<PantryEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PantryViewHolder {
        val binding = ItemPantryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PantryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PantryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PantryViewHolder(private val binding: ItemPantryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PantryEntity) {
            binding.tvPantryIngredientName.text = item.ingredientName
            binding.tvPantryQuantity.text = "Quantity: ${item.quantity}"
            binding.btnRemovePantry.setOnClickListener { onRemoveItem(item) }
        }
    }
}
