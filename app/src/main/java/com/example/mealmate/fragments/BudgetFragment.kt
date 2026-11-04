package com.example.mealmate.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.mealmate.MainActivity
import com.example.mealmate.databinding.FragmentBudgetBinding
import com.example.mealmate.ui.viewmodel.MealViewModel
import kotlinx.coroutines.launch
import java.util.Locale

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MealViewModel by activityViewModels()

    private var targetBudget: Double = 600.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(true)

        val prefs = requireContext().getSharedPreferences("mealmate_prefs", Context.MODE_PRIVATE)
        targetBudget = prefs.getFloat("weekly_budget", 600.0f).toDouble()

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnSetBudget.setOnClickListener {
            val input = binding.etBudgetTarget.text.toString().toDoubleOrNull()
            if (input != null && input > 0) {
                targetBudget = input
                prefs.edit().putFloat("weekly_budget", input.toFloat()).apply()
                binding.etBudgetTarget.text.clear()
                Toast.makeText(requireContext(), "Weekly budget updated!", Toast.LENGTH_SHORT).show()
                calculateAndDisplayBudget()
            } else {
                Toast.makeText(requireContext(), "Please enter a valid budget amount", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            viewModel.mealPlans.collect {
                calculateAndDisplayBudget()
            }
        }
    }

    private fun calculateAndDisplayBudget() {
        val currentPlans = viewModel.mealPlans.value
        var mealCount = 0
        currentPlans.values.forEach { dayMap ->
            mealCount += dayMap.size
        }

        val estimatedCostPerMeal = 45.0 // R45 average per meal
        val totalPlannedCost = mealCount * estimatedCostPerMeal
        val remaining = targetBudget - totalPlannedCost

        binding.tvBudgetTarget.text = String.format(Locale.getDefault(), "R%.2f", targetBudget)
        binding.tvTotalPlannedCost.text = String.format(Locale.getDefault(), "R%.2f", totalPlannedCost)
        binding.tvRemainingBudget.text = String.format(Locale.getDefault(), "R%.2f", remaining)

        val progressPercent = if (targetBudget > 0) ((totalPlannedCost / targetBudget) * 100).toInt() else 0
        binding.pbBudgetProgress.progress = progressPercent.coerceIn(0, 100)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
