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
import com.example.mealmate.adapters.ShoppingAdapter
import com.example.mealmate.adapters.ShoppingListItem
import com.example.mealmate.databinding.FragmentShoppingListBinding
import com.example.mealmate.ui.viewmodel.MealViewModel
import com.example.mealmate.utils.PreferenceHelper
import kotlinx.coroutines.launch

class ShoppingListFragment : Fragment() {

    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MealViewModel by activityViewModels()
    private lateinit var adapter: ShoppingAdapter

    private lateinit var checkedSet: MutableSet<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(true)

        checkedSet = PreferenceHelper.getCheckedShoppingItems(requireContext())

        adapter = ShoppingAdapter(
            onItemChecked = { id, isChecked ->
                if (isChecked) {
                    checkedSet.add(id)
                } else {
                    checkedSet.remove(id)
                }
                PreferenceHelper.saveCheckedShoppingItems(requireContext(), checkedSet)
            }
        )

        binding.rvShoppingList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvShoppingList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mealPlans.collect { mealPlans ->
                val currentBinding = _binding ?: return@collect
                val shoppingList = mutableListOf<ShoppingListItem>()

                mealPlans.forEach { (day, types) ->
                    types.forEach { (type, meal) ->
                        val ingredients = listOfNotNull(
                            meal.strIngredient1, meal.strIngredient2, meal.strIngredient3,
                            meal.strIngredient4, meal.strIngredient5, meal.strIngredient6,
                            meal.strIngredient7, meal.strIngredient8, meal.strIngredient9,
                            meal.strIngredient10
                        ).filter { it.isNotBlank() }

                        if (ingredients.isNotEmpty()) {
                            val headerTitle = "${meal.strMeal ?: "Recipe"} ($day $type)"
                            shoppingList.add(ShoppingListItem.Header(headerTitle))

                            ingredients.forEach { ingredient ->
                                val uniqueId = "$day-$type-${meal.idMeal}-$ingredient"
                                val isChecked = checkedSet.contains(uniqueId)
                                shoppingList.add(
                                    ShoppingListItem.Ingredient(uniqueId, ingredient, isChecked)
                                )
                            }
                        }
                    }
                }

                if (shoppingList.isEmpty()) {
                    currentBinding.tvEmptyShopping.visibility = View.VISIBLE
                    currentBinding.rvShoppingList.visibility = View.GONE
                } else {
                    currentBinding.tvEmptyShopping.visibility = View.GONE
                    currentBinding.rvShoppingList.visibility = View.VISIBLE
                    adapter.updateItems(shoppingList)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
