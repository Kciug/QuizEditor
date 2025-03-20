package com.rafalskrzypczyk.login_screen.reset_password

import com.rafalskrzypczyk.core.base.BaseContract

interface ResetPasswordContract {
    interface View : BaseContract.View {
        fun displayMailSentSuccessfully()
        fun displayToastMessage(messageResId: Int)
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun resetPassword(email: String)
    }
}