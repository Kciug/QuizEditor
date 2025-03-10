package com.rafalskrzypczyk.core.base

interface BaseContract {
    interface View {
        fun showLoading()
        fun showError(message: String)
    }

    interface Presenter<V : View> {
        fun onAttach(view: V)
        fun onViewCreated()
        fun onDestroy()
    }
}