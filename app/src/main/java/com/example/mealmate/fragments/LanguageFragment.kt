package com.example.mealmate.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.mealmate.MainActivity
import com.example.mealmate.R
import com.example.mealmate.databinding.FragmentLanguageBinding
import com.example.mealmate.utils.LocaleHelper

class LanguageFragment : Fragment() {

    private var _binding: FragmentLanguageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(true)

        val currentTags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        when {
            currentTags.contains("zu") -> binding.rbZulu.isChecked = true
            currentTags.contains("xh") -> binding.rbXhosa.isChecked = true
            currentTags.contains("af") -> binding.rbAfrikaans.isChecked = true
            currentTags.contains("st") -> binding.rbSesotho.isChecked = true
            else -> binding.rbEnglish.isChecked = true
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val langCode = when (checkedId) {
                R.id.rbZulu -> "zu"
                R.id.rbXhosa -> "xh"
                R.id.rbAfrikaans -> "af"
                R.id.rbSesotho -> "st"
                else -> "en"
            }
            val currentTags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
            if (!currentTags.contains(langCode)) {
                LocaleHelper.setLocale(requireContext(), langCode)
                activity?.recreate()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
