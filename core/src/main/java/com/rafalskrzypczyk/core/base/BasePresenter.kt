package com.rafalskrzypczyk.core.base

import com.rafalskrzypczyk.core.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject

abstract class BasePresenter<V: BaseContract.View> : BaseContract.Presenter<V> {
    private var _view: V? = null
    val view: V get() = _view!!

    @Inject
    @MainDispatcher
    lateinit var dispatcher : CoroutineDispatcher

    protected var presenterScope : CoroutineScope? = null

    override fun onAttach(view: V) {
        _view = view
    }

    override fun onViewCreated() {
        presenterScope = CoroutineScope(SupervisorJob() + dispatcher)
    }

    override fun onDestroy() {
        presenterScope?.cancel()
        _view = null
    }
}