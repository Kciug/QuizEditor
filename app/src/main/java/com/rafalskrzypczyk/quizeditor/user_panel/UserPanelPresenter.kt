package com.rafalskrzypczyk.quizeditor.user_panel

import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.core.base.BasePresenter
import javax.inject.Inject

class UserPanelPresenter @Inject constructor(
    private val userManager: UserManager,
    private val authRepository: AuthRepository
): BasePresenter<UserPanelContract.View>(), UserPanelContract.Presenter {

    override fun onViewCreated() {
        super.onViewCreated()

        val user = userManager.getCurrentLoggedUser()
        user?.let { view.displayUserData(it.name, it.email, it.role?.name ?: "Użytkownik") }
    }

    override fun onChangePassword() {

    }

    override fun onLogout() {
        authRepository.signOut()
        view.openLoginActivity()
    }
}