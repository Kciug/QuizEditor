package com.rafalskrzypczyk.quizeditor.user_panel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.rafalskrzypczyk.core.base.BaseDialogFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.quizeditor.MainActivity
import com.rafalskrzypczyk.quizeditor.databinding.LayoutUserPanelBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserPanelFragment :
    BaseDialogFragment<LayoutUserPanelBinding>(LayoutUserPanelBinding::inflate),
    UserPanelContract.View {
    @Inject
    lateinit var presenter: UserPanelContract.Presenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.onAttach(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewCreated()
    }

    override fun onViewBound() {
        super.onViewBound()

        with(binding) {
            btnChangePassword.setOnClickListener { presenter.onChangePassword() }
            btnLogout.setOnClickListener { presenter.onLogout() }
        }
    }

    override fun onDestroyView() {
        presenter.onDestroy()
        super.onDestroyView()
    }

    override fun displayUserIcon(iconResId: Int) {
        binding.ivUserIcon.setImageResource(iconResId)
    }

    override fun displayUserData(
        userName: String,
        userEmail: String,
        userRole: String,
    ) {
        binding.tvUserName.text = userName
        binding.tvUserEmail.text = userEmail
        binding.tvUserRole.text = userRole
    }

    override fun openLoginActivity() {
        val activity = requireActivity()
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity.finish()
    }

    override fun displayLoading() {

    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}