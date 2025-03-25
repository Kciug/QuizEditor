package com.rafalskrzypczyk.login_screen.reset_password

import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.login_screen.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ResetPasswordPresenter @Inject constructor(
    private val authRepository: AuthRepository
): BasePresenter<ResetPasswordContract.View>(), ResetPasswordContract.Presenter {

    override fun resetPassword(email: String) {
        if(email.isEmpty()){
            view.displayToastMessage(R.string.warning_reset_password_missing_email)
            return
        }

        presenterScope?.launch {
            authRepository.sendPasswordResetToEmail(email).collectLatest {
                when (it) {
                    is Response.Error -> view.displayError(it.error)
                    Response.Loading -> view.displayLoading()
                    is Response.Success -> view.displayMailSentSuccessfully()
                }
            }
        }
    }
}