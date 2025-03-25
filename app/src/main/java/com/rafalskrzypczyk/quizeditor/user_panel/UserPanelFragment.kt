package com.rafalskrzypczyk.quizeditor.user_panel

import android.content.Intent
import com.rafalskrzypczyk.core.base.BaseDialogFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.quizeditor.MainActivity
import com.rafalskrzypczyk.quizeditor.databinding.LayoutUserPanelBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserPanelFragment :
    BaseDialogFragment<LayoutUserPanelBinding, UserPanelContract.View, UserPanelContract.Presenter>(
        LayoutUserPanelBinding::inflate
    ), UserPanelContract.View {

    override fun onViewBound() {
        super.onViewBound()

        with(binding) {
            btnChangePassword.setOnClickListener { presenter.onChangePassword() }
            btnLogout.setOnClickListener { presenter.onLogout() }
        }
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