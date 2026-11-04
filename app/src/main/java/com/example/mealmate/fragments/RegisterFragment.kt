package com.example.mealmate.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mealmate.MainActivity
import com.example.mealmate.databinding.FragmentRegisterBinding
import com.example.mealmate.utils.PasswordUtils
import com.example.mealmate.utils.PreferenceHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(false)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                binding.tvStatus.text = "Please fill in all fields"
                binding.tvStatus.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (!PasswordUtils.isPasswordValid(password)) {
                binding.tvStatus.text = "Password must be at least 8 characters long and contain at least one number and one special character."
                binding.tvStatus.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val hashedPassword = PasswordUtils.hashPassword(password)
            Log.d("MealMate", "Account encrypted hash generated: ${hashedPassword.take(8)}...")

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        auth.currentUser?.updateProfile(profileUpdates)
                        PreferenceHelper.saveUserProfile(requireContext(), name, email)

                        (activity as? MainActivity)?.let { main ->
                            main.showBottomNavigation(true)
                            main.replaceFragment(DashboardFragment(), addToBackStack = false)
                        }
                    } else {
                        binding.tvStatus.text = task.exception?.message ?: "Registration Failed"
                        binding.tvStatus.visibility = View.VISIBLE
                    }
                }
        }

        binding.btnBackToLogin.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(LoginFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
