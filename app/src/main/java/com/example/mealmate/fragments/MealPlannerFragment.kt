package com.example.mealmate.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.MainActivity
import com.example.mealmate.adapters.CalendarDayAdapter
import com.example.mealmate.adapters.CalendarDayItem
import com.example.mealmate.adapters.MealPlanAdapter
import com.example.mealmate.adapters.MealPlanSlotItem
import com.example.mealmate.databinding.FragmentMealPlannerBinding
import com.example.mealmate.ui.viewmodel.MealViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MealPlannerFragment : Fragment() {

    private var _binding: FragmentMealPlannerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MealViewModel by activityViewModels()

    private lateinit var calendarAdapter: CalendarDayAdapter
    private lateinit var mealPlanAdapter: MealPlanAdapter

    private var selectedEnglishDayName: String = "Monday"
    private val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Dessert")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealPlannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(true)

        setupCalendar()
        setupMealSlotsList()
        observeMealPlans()
    }

    private fun setupCalendar() {
        val calendarDays = generateCurrentWeekDays()
        val defaultSelected = calendarDays.firstOrNull { it.isSelected } ?: calendarDays.firstOrNull()
        if (defaultSelected != null) {
            selectedEnglishDayName = defaultSelected.englishDayName
            binding.tvSelectedDayTitle.text = defaultSelected.fullDayName
        }

        calendarAdapter = CalendarDayAdapter(calendarDays) { selectedItem ->
            selectedEnglishDayName = selectedItem.englishDayName
            binding.tvSelectedDayTitle.text = selectedItem.fullDayName
            refreshSlots()
        }

        binding.rvCalendarDates.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCalendarDates.adapter = calendarAdapter
    }

    private fun generateCurrentWeekDays(): List<CalendarDayItem> {
        val days = mutableListOf<CalendarDayItem>()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val todayCal = Calendar.getInstance()
        val shortFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val numberFormat = SimpleDateFormat("dd", Locale.getDefault())
        val fullFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val englishFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)

        for (i in 0..6) {
            val isToday = (calendar.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR) &&
                    calendar.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR))

            days.add(
                CalendarDayItem(
                    dayNameShort = shortFormat.format(calendar.time),
                    dateNumber = numberFormat.format(calendar.time),
                    fullDayName = fullFormat.format(calendar.time),
                    englishDayName = englishFormat.format(calendar.time),
                    isSelected = isToday || (i == 0 && todayCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

    private fun setupMealSlotsList() {
        mealPlanAdapter = MealPlanAdapter(
            onAddOrChange = { _, type ->
                (activity as? MainActivity)?.replaceFragment(DashboardFragment())
            },
            onRemove = { _, type ->
                viewModel.removeMealPlan(selectedEnglishDayName, type)
            },
            onRecipeClick = { meal ->
                (activity as? MainActivity)?.replaceFragment(
                    RecipeDetailFragment.newInstance(meal.idMeal)
                )
            }
        )

        binding.rvMealPlan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMealPlan.adapter = mealPlanAdapter
    }

    private fun observeMealPlans() {
        lifecycleScope.launch {
            viewModel.mealPlans.collect {
                refreshSlots()
            }
        }
    }

    private fun refreshSlots() {
        val currentPlans = viewModel.mealPlans.value
        val dayMap = currentPlans[selectedEnglishDayName] ?: emptyMap()

        val slotItems = mealTypes.map { type ->
            MealPlanSlotItem(selectedEnglishDayName, type, dayMap[type])
        }
        mealPlanAdapter.updateSlots(slotItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
