package com.rafalskrzypczyk.login_screen.login

import android.content.Context
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.onAttach(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewCreated()
        navController = findNavController()
    }

    override fun onViewBound() {
        super.onViewBound()

        binding.loginButton.setOnClickListener {
            presenter.login(binding.inputEmail.text.toString(), binding.passwordInput.text.toString())
        }

        binding.resetPassword.setOnClickListener {
            navController.navigate(R.id.navigation_reset_password)
        }
    }

    override fun onDestroyView() {
        presenter.onDestroy()
        super.onDestroyView()
    }

    override fun displayLoading() {
        val toast = Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun displayError(message: String) {
        val errorDialog = ErrorDialog(requireContext(), message)
        errorDialog.show()
    }
}