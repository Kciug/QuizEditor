package com.rafalskrzypczyk.core.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class BasePresenter {
    val presenterScope = CoroutineScope(Dispatchers.Main)
}