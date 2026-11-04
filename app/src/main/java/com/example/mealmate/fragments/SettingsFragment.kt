package com.example.mealmate.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.mealmate.MainActivity
import com.example.mealmate.R
import com.example.mealmate.databinding.FragmentSettingsBinding
import com.example.mealmate.utils.NotificationHelper
import com.example.mealmate.utils.PreferenceHelper
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()

    private val options = arrayOf("Vegetarian", "Halal", "Vegan", "Gluten-Free", "Keto", "Paleo")
    private lateinit var selectedItems: BooleanArray

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(true)

        val currentUser = auth.currentUser
        val savedName = PreferenceHelper.getUserName(requireContext())
        val displayName = currentUser?.displayName?.ifEmpty { null } ?: savedName.ifEmpty { null } ?: currentUser?.email ?: "User"
        binding.tvUserName.text = displayName

        checkNetworkAndSyncStatus()
        loadDietarySummary()
        updateLanguageDisplay()

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                NotificationHelper.sendRecipeOfTheDay(requireContext())
                NotificationHelper.sendMealPlanReminder(requireContext())
                NotificationHelper.sendSeasonalDishAlert(requireContext())
                NotificationHelper.sendReengagementPrompt(requireContext())
                NotificationHelper.sendSavedRecipeUpdate(requireContext())
                Toast.makeText(requireContext(), "Real-time Notifications Sent!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rowProfile.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(ProfileFragment())
        }

        binding.rowPantry.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(PantryFragment())
        }

        binding.rowBudget.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(BudgetFragment())
        }

        binding.rowLanguage.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(LanguageFragment())
        }

        binding.rowDietary.setOnClickListener {
            showDietaryDialog()
        }

        binding.btnLogout.setOnClickListener {
            Log.d("MealMate", "User logged out")
            PreferenceHelper.setLoggedIn(requireContext(), false)
            auth.signOut()
            (activity as? MainActivity)?.let { main ->
                main.showBottomNavigation(false)
                main.replaceFragment(LoginFragment())
            }
        }
    }

    private fun updateLanguageDisplay() {
        val currentTags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val langName = when {
            currentTags.contains("zu") -> "isiZulu"
            currentTags.contains("xh") -> "isiXhosa"
            currentTags.contains("af") -> "Afrikaans"
            currentTags.contains("st") -> "Sesotho"
            else -> "English"
        }
        binding.tvSelectedLanguage.text = langName
    }

    private fun loadDietarySummary() {
        val saved = PreferenceHelper.getDietaryPreferences(requireContext())
        selectedItems = BooleanArray(options.size) { i -> saved.contains(options[i]) }
        binding.tvDietarySummary.text = if (saved.isEmpty()) "None selected" else saved.joinToString(" • ")
    }

    private fun checkNetworkAndSyncStatus() {
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(network)
        val isOnline = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        if (isOnline) {
            binding.tvSyncStatus.setText(R.string.online_synced)
        } else {
            binding.tvSyncStatus.setText(R.string.offline_roomdb)
        }
    }

    private fun showDietaryDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Dietary Preferences")
            .setMultiChoiceItems(options, selectedItems) { _, which, isChecked ->
                selectedItems[which] = isChecked
            }
            .setPositiveButton("Save") { _, _ ->
                val selected = options.filterIndexed { index, _ -> selectedItems[index] }.toSet()
                PreferenceHelper.saveDietaryPreferences(requireContext(), selected)
                binding.tvDietarySummary.text = if (selected.isEmpty()) "None selected" else selected.joinToString(" • ")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
