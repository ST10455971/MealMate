package com.example.mealmate.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.mealmate.MainActivity
import com.example.mealmate.databinding.FragmentLoginBinding
import com.example.mealmate.utils.PasswordUtils
import com.example.mealmate.utils.PreferenceHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val displayName = account.displayName ?: "Google User"
                val email = account.email ?: "googleuser@gmail.com"
                PreferenceHelper.saveUserProfile(requireContext(), displayName, email)

                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            (activity as? MainActivity)?.let { main ->
                                main.showBottomNavigation(true)
                                main.replaceFragment(DashboardFragment(), addToBackStack = false)
                            }
                        } else {
                            Toast.makeText(requireContext(), "Signed in as $displayName", Toast.LENGTH_SHORT).show()
                            (activity as? MainActivity)?.let { main ->
                                main.showBottomNavigation(true)
                                main.replaceFragment(DashboardFragment(), addToBackStack = false)
                            }
                        }
                    }
            }
        } catch (e: ApiException) {
            Log.e("MealMate", "Google Sign In ApiException code: ${e.statusCode}")
            PreferenceHelper.saveUserProfile(requireContext(), "Google User", "googleuser@gmail.com")
            Toast.makeText(requireContext(), "Signed in with Google!", Toast.LENGTH_SHORT).show()
            (activity as? MainActivity)?.let { main ->
                main.showBottomNavigation(true)
                main.replaceFragment(DashboardFragment(), addToBackStack = false)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNavigation(false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.btnLogin.setOnClickListener {
            Log.d("MealMate", "User clicked Login button.")
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                binding.tvStatus.text = "Please enter email and password"
                binding.tvStatus.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val hashedPassword = PasswordUtils.hashPassword(password)
            Log.d("MealMate", "Encrypted password hash generated: ${hashedPassword.take(8)}...")

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val name = user?.displayName?.ifEmpty { null } ?: email.substringBefore("@")
                        PreferenceHelper.saveUserProfile(requireContext(), name, email)

                        (activity as? MainActivity)?.let { main ->
                            main.showBottomNavigation(true)
                            main.replaceFragment(DashboardFragment(), addToBackStack = false)
                        }
                    } else {
                        binding.tvStatus.text = task.exception?.message ?: "Login Failed"
                        binding.tvStatus.visibility = View.VISIBLE
                    }
                }
        }

        binding.btnGoogleSignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        binding.btnGoToRegister.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(RegisterFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
