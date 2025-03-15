package com.rafalskrzypczyk.quizeditor.user_panel

import com.rafalskrzypczyk.core.base.BaseContract

interface UserPanelContract {
    interface View : BaseContract.View {
        fun displayUserIcon(iconResId: Int)
        fun displayUserData(userName: String, userEmail: String, userRole: String)
        fun openLoginActivity()
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun onChangePassword()
        fun onLogout()
    }
}