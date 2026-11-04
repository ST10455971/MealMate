package com.example.mealmate.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mealmate.MainActivity
import com.example.mealmate.databinding.FragmentProfileBinding
import com.example.mealmate.utils.PasswordUtils
import com.example.mealmate.utils.PreferenceHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(true)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val currentUser = auth.currentUser
        val savedName = PreferenceHelper.getUserName(requireContext())
        val savedEmail = PreferenceHelper.getUserEmail(requireContext())

        val displayName = currentUser?.displayName?.ifEmpty { null } ?: savedName.ifEmpty { null } ?: "Pete Mark"
        val displayEmail = currentUser?.email?.ifEmpty { null } ?: savedEmail.ifEmpty { null } ?: "petemark@gmail.com"

        binding.etProfileName.setText(displayName)
        binding.etProfileEmail.setText(displayEmail)

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etProfileName.text.toString().trim()
            val email = binding.etProfileEmail.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                PreferenceHelper.saveUserProfile(requireContext(), name, email)

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                currentUser?.updateProfile(profileUpdates)

                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please enter both name and email", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUpdatePassword.setOnClickListener {
            val newPass = binding.etNewPassword.text?.toString()?.trim() ?: ""
            val confirmPass = binding.etConfirmPassword.text?.toString()?.trim() ?: ""

            if (!PasswordUtils.isPasswordValid(newPass)) {
                binding.tvPasswordStatus.text = "Password must be at least 8 characters long and contain at least one number and one special character."
                binding.tvPasswordStatus.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                binding.tvPasswordStatus.text = "Passwords do not match."
                binding.tvPasswordStatus.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val encryptedHash = PasswordUtils.hashPassword(newPass)
            Log.d("MealMate", "Updating password hash: ${encryptedHash.take(8)}...")

            currentUser?.updatePassword(newPass)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.etNewPassword.setText("")
                        binding.etConfirmPassword.setText("")
                        binding.tvPasswordStatus.visibility = View.GONE
                        Toast.makeText(requireContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.tvPasswordStatus.text = task.exception?.message ?: "Failed to update password."
                        binding.tvPasswordStatus.visibility = View.VISIBLE
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
