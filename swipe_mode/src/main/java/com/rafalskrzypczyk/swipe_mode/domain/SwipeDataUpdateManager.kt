package com.rafalskrzypczyk.swipe_mode.domain

import com.rafalskrzypczyk.core.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class SwipeDataUpdateManager @Inject constructor(
    private val repository: SwipeModeRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    fun updateQuestion(question: SwipeQuestion) {
        CoroutineScope(SupervisorJob() + dispatcher).launch {
            repository.updateQuestion(question)
        }
    }
}