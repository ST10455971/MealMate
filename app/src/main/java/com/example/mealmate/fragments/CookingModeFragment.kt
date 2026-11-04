package com.example.mealmate.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mealmate.MainActivity
import com.example.mealmate.databinding.FragmentCookingModeBinding

class CookingModeFragment : Fragment() {

    private var _binding: FragmentCookingModeBinding? = null
    private val binding get() = _binding!!

    private var recipeName: String = "Recipe"
    private var steps: List<String> = emptyList()
    private var currentStepIndex = 0

    private var timer: CountDownTimer? = null
    private var timerMillis: Long = 300000L // 5 mins default
    private var isTimerRunning = false

    companion object {
        private const val ARG_NAME = "arg_recipe_name"
        private const val ARG_INSTRUCTIONS = "arg_recipe_instructions"

        fun newInstance(name: String, instructions: String): CookingModeFragment {
            val fragment = CookingModeFragment()
            val args = Bundle()
            args.putString(ARG_NAME, name)
            args.putString(ARG_INSTRUCTIONS, instructions)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeName = arguments?.getString(ARG_NAME) ?: "Recipe"
        val rawInstructions = arguments?.getString(ARG_INSTRUCTIONS) ?: "No instructions available."
        steps = rawInstructions.split(Regex("(?<=\\.)|\\r?\\n")).map { it.trim() }.filter { it.isNotBlank() }
        if (steps.isEmpty()) {
            steps = listOf(rawInstructions)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCookingModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(true)

        binding.tvRecipeName.text = recipeName
        updateStepDisplay()

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnPrevStep.setOnClickListener {
            if (currentStepIndex > 0) {
                currentStepIndex--
                updateStepDisplay()
            }
        }

        binding.btnNextStep.setOnClickListener {
            if (currentStepIndex < steps.size - 1) {
                currentStepIndex++
                updateStepDisplay()
            } else {
                Toast.makeText(requireContext(), "Cooking Completed! Enjoy your meal 🎉", Toast.LENGTH_LONG).show()
            }
        }

        setupTimerControls()
    }

    private fun updateStepDisplay() {
        binding.tvStepTitle.text = "Step ${currentStepIndex + 1} of ${steps.size}"
        binding.tvStepInstruction.text = steps[currentStepIndex]
        binding.btnPrevStep.isEnabled = currentStepIndex > 0
    }

    private fun setupTimerControls() {
        updateTimerText()

        binding.btnAdd1m.setOnClickListener { addTime(60000L) }
        binding.btnAdd5m.setOnClickListener { addTime(300000L) }
        binding.btnAdd10m.setOnClickListener { addTime(600000L) }

        binding.btnStartTimer.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        binding.btnResetTimer.setOnClickListener {
            resetTimer()
        }
    }

    private fun addTime(millis: Long) {
        timerMillis += millis
        updateTimerText()
        if (isTimerRunning) {
            pauseTimer()
            startTimer()
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timerMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerMillis = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                isTimerRunning = false
                binding.btnStartTimer.text = "Start"
                binding.tvTimerDisplay.text = "00:00"
                Toast.makeText(requireContext(), "⏱ Timer Finished!", Toast.LENGTH_LONG).show()
            }
        }.start()

        isTimerRunning = true
        binding.btnStartTimer.text = "Pause"
    }

    private fun pauseTimer() {
        timer?.cancel()
        isTimerRunning = false
        binding.btnStartTimer.text = "Start"
    }

    private fun resetTimer() {
        pauseTimer()
        timerMillis = 300000L
        updateTimerText()
    }

    private fun updateTimerText() {
        val minutes = (timerMillis / 1000) / 60
        val seconds = (timerMillis / 1000) % 60
        binding.tvTimerDisplay.text = String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
    }
}
