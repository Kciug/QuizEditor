package com.rafalskrzypczyk.core.database_management

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object DatabaseEventBus {
    private val _eventReloadData = MutableSharedFlow<Unit>()
    val eventReloadData = _eventReloadData.asSharedFlow()

    suspend fun publish() {
        _eventReloadData.emit(Unit)
    }
}