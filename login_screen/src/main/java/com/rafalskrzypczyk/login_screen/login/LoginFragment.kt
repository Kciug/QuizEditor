package com.rafalskrzypczyk.login_screen.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.login_screen.R
import com.rafalskrzypczyk.login_screen.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate), LoginContract.View {

    @Inject
    lateinit var presenter: LoginContract.Presenter

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        binding.loginButton.setOnClickListener {
            presenter.login(binding.inputEmail.text.toString(), binding.passwordInput.text.toString())
        }

        binding.resetPassword.setOnClickListener {
            navController.navigate(R.id.navigation_reset_password)
        }
    }

    override fun showLoading() {
        val toast = Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun showError(message: String) {
        val errorDialog = ErrorDialog(requireContext(), message)
        errorDialog.show()
    }
}