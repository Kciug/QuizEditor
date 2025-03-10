package com.rafalskrzypczyk.core.base

abstract class BasePresenter<V: BaseContract.View> : BaseContract.Presenter<V> {
    private var _view: V? = null
    val view: V get() = _view!!

    override fun onAttach(view: V) {
        _view = view
    }

    override fun onViewCreated() {}

    override fun onDestroy() {
        _view = null
    }
}