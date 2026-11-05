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
import com.example.mealmate.adapters.RecipeAdapter
import com.example.mealmate.databinding.FragmentFavouriteBinding
import com.example.mealmate.ui.viewmodel.MealViewModel
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MealViewModel by activityViewModels()
    private lateinit var adapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(true)

        adapter = RecipeAdapter(
            onRecipeClick = { meal ->
                (activity as? MainActivity)?.replaceFragment(
                    RecipeDetailFragment.newInstance(meal.idMeal)
                )
            },
            onFavoriteClick = { meal ->
                viewModel.removeFavorite(meal)
            }
        )

        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favorites.collect { favorites ->
                val currentBinding = _binding ?: return@collect
                if (favorites.isEmpty()) {
                    currentBinding.tvEmptyFavorites.visibility = View.VISIBLE
                    currentBinding.rvFavorites.visibility = View.GONE
                } else {
                    currentBinding.tvEmptyFavorites.visibility = View.GONE
                    currentBinding.rvFavorites.visibility = View.VISIBLE
                    adapter.updateData(favorites, favorites)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
