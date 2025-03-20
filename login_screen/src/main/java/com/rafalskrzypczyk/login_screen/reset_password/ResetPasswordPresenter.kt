package com.rafalskrzypczyk.login_screen.reset_password

import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.rafalskrzypczyk.login_screen.R

class ResetPasswordPresenter @Inject constructor(
    private val authRepository: AuthRepository,
    @MainDispatcher dispatcher: CoroutineDispatcher
): BasePresenter<ResetPasswordContract.View>(), ResetPasswordContract.Presenter {
    private val presenterScope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun resetPassword(email: String) {
        if(email.isEmpty()){
            view.displayToastMessage(R.string.warning_reset_password_missing_email)
            return
        }

        presenterScope.launch {
            authRepository.sendPasswordResetToEmail(email).collectLatest {
                when (it) {
                    is Response.Error -> view.displayError(it.error)
                    Response.Loading -> view.displayLoading()
                    is Response.Success -> view.displayMailSentSuccessfully()
                }
            }
        }
    }

    override fun onDestroy() {
        presenterScope.cancel()
        super.onDestroy()
    }
}