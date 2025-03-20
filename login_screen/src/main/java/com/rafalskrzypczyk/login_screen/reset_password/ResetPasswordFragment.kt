package com.rafalskrzypczyk.login_screen.reset_password

import android.content.Context
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
import com.rafalskrzypczyk.login_screen.databinding.FragmentResetPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment<FragmentResetPasswordBinding>(FragmentResetPasswordBinding::inflate), ResetPasswordContract.View {

    @Inject
    lateinit var presenter: ResetPasswordContract.Presenter

    private lateinit var navController: NavController
    private lateinit var keyboardController: KeyboardController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.onAttach(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewCreated()
        keyboardController = KeyboardController(requireContext())
    }

    override fun onViewBound() {
        super.onViewBound()

        navController = findNavController()

        binding.buttonBack.setOnClickListener {
            navController.popBackStack()
        }

        binding.content.buttonResetPassword.setOnClickListener {
            onResetPassword()
        }

        binding.content.inputEmail.setOnEditorActionListener { _, _, _ ->
            onResetPassword()
            true
        }
    }

    override fun onDestroyView() {
        presenter.onDestroy()
        super.onDestroyView()
    }

    override fun displayMailSentSuccessfully() {
        QuizEditorAnimations.animateReplaceScaleOutIn(binding.loading.root, binding.tvSuccessfullyPrompt)
    }

    override fun displayToastMessage(messageResId: Int) {
        Toast.makeText(requireContext(), messageResId, Toast.LENGTH_SHORT).show()
    }

    override fun displayLoading() {
        QuizEditorAnimations.animateReplaceScaleOutIn(binding.content.root, binding.loading.root)
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
        binding.loading.root.makeGone()
        binding.content.root.makeVisible()
    }

    private fun onResetPassword() {
        keyboardController.hideKeyboard(binding.content.inputEmail)
        presenter.resetPassword(binding.content.inputEmail.text.toString())
    }
}