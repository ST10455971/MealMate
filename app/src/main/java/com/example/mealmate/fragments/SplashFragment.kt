package com.example.mealmate.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mealmate.MainActivity
import com.example.mealmate.databinding.FragmentSplashBinding
import com.example.mealmate.utils.PreferenceHelper
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(false)

        Handler(Looper.getMainLooper()).postDelayed({
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val isLoggedInPref = PreferenceHelper.isLoggedIn(requireContext())
            val mainActivity = activity as? MainActivity
            if (firebaseUser != null || isLoggedInPref) {
                mainActivity?.showBottomNavigation(true)
                mainActivity?.replaceFragment(DashboardFragment(), addToBackStack = false)
            } else {
                mainActivity?.showBottomNavigation(false)
                mainActivity?.replaceFragment(LoginFragment(), addToBackStack = false)
            }
        }, 1500)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
