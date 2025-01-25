package com.rafalskrzypczyk.login_screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.rafalskrzypczyk.login_screen.LoginActivity
import com.rafalskrzypczyk.login_screen.databinding.FragmentLoginBinding
import com.rafalskrzypczyk.login_screen.R

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        navController = findNavController()

        setupOnClickListeners(requireActivity() as LoginActivity)

        return binding.root
    }

    private fun setupOnClickListeners(activity: LoginActivity){
        binding.loginButton.setOnClickListener {
            activity.handleLoginButtonClick()
        }

        binding.resetPassword.setOnClickListener {
            navController.navigate(R.id.navigation_reset_password)
        }
    }
}