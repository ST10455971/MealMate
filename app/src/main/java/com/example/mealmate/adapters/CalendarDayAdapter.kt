package com.example.mealmate.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.R
import com.example.mealmate.databinding.ItemCalendarDayBinding

data class CalendarDayItem(
    val dayNameShort: String,
    val dateNumber: String,
    val fullDayName: String,
    val englishDayName: String,
    var isSelected: Boolean = false
)

class CalendarDayAdapter(
    private var days: List<CalendarDayItem> = emptyList(),
    private val onDateSelected: (CalendarDayItem) -> Unit
) : RecyclerView.Adapter<CalendarDayAdapter.CalendarDayViewHolder>() {

    fun updateDays(newDays: List<CalendarDayItem>) {
        days = newDays
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CalendarDayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarDayViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount(): Int = days.size

    inner class CalendarDayViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CalendarDayItem) {
            binding.tvDayName.text = item.dayNameShort
            binding.tvDayNumber.text = item.dateNumber

            val context = binding.root.context
            if (item.isSelected) {
                binding.cardCalendarDay.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.green_primary)
                )
                binding.tvDayName.setTextColor(Color.WHITE)
                binding.tvDayNumber.setTextColor(Color.WHITE)
                binding.cardCalendarDay.strokeColor =
                    ContextCompat.getColor(context, R.color.orange_accent)
            } else {
                binding.cardCalendarDay.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.white)
                )
                binding.tvDayName.setTextColor(
                    ContextCompat.getColor(context, R.color.green_light)
                )
                binding.tvDayNumber.setTextColor(
                    ContextCompat.getColor(context, R.color.dark_text)
                )
                binding.cardCalendarDay.strokeColor =
                    ContextCompat.getColor(context, R.color.light_grey)
            }

            binding.cardCalendarDay.setOnClickListener {
                days.forEach { it.isSelected = false }
                item.isSelected = true
                notifyDataSetChanged()
                onDateSelected(item)
            }
        }
    }
}
