package com.rafalskrzypczyk.login_screen.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.core.utils.KeyboardController
import com.rafalskrzypczyk.login_screen.R
import com.rafalskrzypczyk.login_screen.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding, LoginContract.View, LoginContract.Presenter>(
        FragmentLoginBinding::inflate
    ), LoginContract.View {

    private lateinit var navController: NavController
    private lateinit var keyboardController: KeyboardController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        keyboardController = KeyboardController(requireContext())
    }

    override fun onViewBound() {
        super.onViewBound()

        binding.btnLogin.setOnClickListener {
            onLogin()
        }

        binding.btnResetPassword.setOnClickListener {
            navController.navigate(R.id.action_login_to_reset_password)
        }

        binding.inputPassword.setOnEditorActionListener { _, _, _ ->
            onLogin()
            true
        }
    }

    override fun displayLoading() {
        QuizEditorAnimations.animateReplaceScaleOutIn(binding.content, binding.loading.root)
    }

    override fun displayError(message: String) {
        binding.loading.root.makeGone()
        binding.content.makeVisible()
        ErrorDialog(requireContext(), message).show()
    }

    private fun onLogin() {
        keyboardController.hideKeyboard(binding.inputPassword)
        presenter.login(binding.inputEmail.text.toString(), binding.inputPassword.text.toString())
    }

    override fun displayToastMessage(messageResId: Int) {
        Toast.makeText(requireContext(), messageResId, Toast.LENGTH_SHORT).show()
    }
}