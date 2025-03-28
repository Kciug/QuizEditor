package com.rafalskrzypczyk.core.database_management

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object DatabaseEventBus {
    private val _eventReloadData = MutableSharedFlow<Unit>()
    val eventReloadData = _eventReloadData.asSharedFlow()

    fun publish() {
        _eventReloadData.tryEmit(Unit)
    }
}