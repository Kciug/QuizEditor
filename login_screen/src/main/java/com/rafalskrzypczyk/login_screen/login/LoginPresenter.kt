package com.rafalskrzypczyk.login_screen.login

import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.di.MainDispatcher
import com.rafalskrzypczyk.login_screen.R
import com.rafalskrzypczyk.login_screen.SuccessLoginHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val authRepository: AuthRepository,
    private val successLoginHandler: SuccessLoginHandler,
    @MainDispatcher private val  dispatcher: CoroutineDispatcher
) : BasePresenter<LoginContract.View>(), LoginContract.Presenter {
    private var presenterScope : CoroutineScope? = null

    override fun onViewCreated() {
        super.onViewCreated()
        presenterScope = CoroutineScope(SupervisorJob() + dispatcher)
    }

    override fun login(email: String, password: String) {
        if(email.isEmpty() || password.isEmpty()){
            view.displayToastMessage(R.string.warning_missing_login_data)
            return
        }

        presenterScope?.launch{
            authRepository.loginWithEmailAndPassword(email, password).collectLatest {
                when(it){
                    is Response.Error -> view.displayError(it.error)
                    Response.Loading -> view.displayLoading()
                    is Response.Success -> successLoginHandler.onSuccessLogin()
                }
            }
        }
    }

    override fun onDestroy() {
        presenterScope?.cancel()
        super.onDestroy()
    }
}