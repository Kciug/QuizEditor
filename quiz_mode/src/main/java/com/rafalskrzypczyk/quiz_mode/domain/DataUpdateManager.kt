package com.rafalskrzypczyk.quiz_mode.domain

import com.rafalskrzypczyk.core.di.IoDispatcher
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class DataUpdateManager @Inject constructor(
    private val repository: QuizModeRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    fun updateCategory(category: Category) {
        CoroutineScope(SupervisorJob() + dispatcher).launch {
            repository.updateCategory(category)
        }
    }

    fun updateQuestion(question: Question) {
        CoroutineScope(SupervisorJob() + dispatcher).launch {
            repository.updateQuestion(question)
        }
    }

    fun bindQuestionWithCategory(questionId: Long, categoryId: Long) {
        CoroutineScope(SupervisorJob() + dispatcher).launch {
            repository.bindQuestionWithCategory(questionId, categoryId)
        }
    }

    fun unbindQuestionWithCategory(questionId: Long, categoryId: Long) {
        CoroutineScope(SupervisorJob() + dispatcher).launch {
            repository.unbindQuestionWithCategory(questionId, categoryId)
        }
    }
}