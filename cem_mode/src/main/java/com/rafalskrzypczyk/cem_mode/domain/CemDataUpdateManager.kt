package com.rafalskrzypczyk.cem_mode.domain

import com.rafalskrzypczyk.core.di.IoDispatcher
import com.rafalskrzypczyk.cem_mode.domain.models.CemQuestion
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class CemDataUpdateManager @Inject constructor(
    private val repository: CemModeRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    fun updateQuestion(question: CemQuestion) {
        CoroutineScope(SupervisorJob() + dispatcher).launch {
            repository.updateQuestion(question)
        }
    }
    
    fun bindQuestionWithCategory(questionId: Long, categoryId: Long) {
        CoroutineScope(SupervisorJob() + dispatcher).launch {
            repository.bindQuestionWithCategory(questionId, categoryId)
        }
    }
}