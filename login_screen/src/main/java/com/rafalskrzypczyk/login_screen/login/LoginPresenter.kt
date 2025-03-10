package com.rafalskrzypczyk.login_screen.login

import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.login_screen.SuccessLoginHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val authRepository: AuthRepository,
    private val successLoginHandler: SuccessLoginHandler,
    dispatcher: CoroutineDispatcher
) : BasePresenter<LoginContract.View>(), LoginContract.Presenter {
    private val presenterScope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun login(email: String, password: String) {
        presenterScope.launch{
            authRepository.loginWithEmailAndPassword(email, password).collect{
                when(it){
                    is Response.Error -> view.showError(it.error)
                    Response.Loading -> view.showLoading()
                    is Response.Success -> successLoginHandler.onSuccessLogin()
                }
            }
        }
    }

    override fun onDestroy() {
        presenterScope.cancel()
        super.onDestroy()
    }
}