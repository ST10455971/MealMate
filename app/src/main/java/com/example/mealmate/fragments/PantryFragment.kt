package com.example.mealmate.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.MainActivity
import com.example.mealmate.adapters.PantryAdapter
import com.example.mealmate.databinding.FragmentPantryBinding
import com.example.mealmate.ui.viewmodel.MealViewModel
import kotlinx.coroutines.launch

class PantryFragment : Fragment() {

    private var _binding: FragmentPantryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MealViewModel by activityViewModels()
    private lateinit var adapter: PantryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPantryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(true)

        adapter = PantryAdapter { item ->
            viewModel.removePantryItem(item)
        }

        binding.rvPantry.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPantry.adapter = adapter

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnAddPantry.setOnClickListener {
            val name = binding.etPantryIngredient.text.toString().trim()
            val qty = binding.etPantryQuantity.text.toString().trim()
            if (name.isNotEmpty() && qty.isNotEmpty()) {
                viewModel.addPantryItem(name, qty)
                binding.etPantryIngredient.text.clear()
                binding.etPantryQuantity.text.clear()
            } else {
                Toast.makeText(requireContext(), "Please fill in ingredient name and quantity", Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pantryItems.collect { items ->
                val currentBinding = _binding ?: return@collect
                if (items.isEmpty()) {
                    currentBinding.tvEmptyPantry.visibility = View.VISIBLE
                    currentBinding.rvPantry.visibility = View.GONE
                } else {
                    currentBinding.tvEmptyPantry.visibility = View.GONE
                    currentBinding.rvPantry.visibility = View.VISIBLE
                    adapter.updateItems(items)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
