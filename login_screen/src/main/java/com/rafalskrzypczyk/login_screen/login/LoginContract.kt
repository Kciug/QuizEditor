package com.rafalskrzypczyk.login_screen.login

import com.rafalskrzypczyk.core.base.BaseContract

interface LoginContract {
    interface View : BaseContract.View
    interface Presenter : BaseContract.Presenter<View> {
        fun login(email: String, password: String)
    }
}