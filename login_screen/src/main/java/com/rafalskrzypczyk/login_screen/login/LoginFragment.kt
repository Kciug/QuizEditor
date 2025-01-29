package com.rafalskrzypczyk.login_screen.login

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.login_screen.LoginActivity
import com.rafalskrzypczyk.login_screen.R
import com.rafalskrzypczyk.login_screen.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        setupOnClickListeners(requireActivity() as LoginActivity)
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