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
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    fun updateCategory(category: Category) {
        scope.launch {
            repository.updateCategory(category)
        }
    }

    fun updateQuestion(question: Question) {
        scope.launch {
            repository.updateQuestion(question)
        }
    }

    fun bindQuestionWithCategory(questionId: Long, categoryId: Long, onScope: CoroutineScope? = null) {
        val bindScope = onScope ?: scope
        bindScope.launch {
            repository.bindQuestionWithCategory(questionId, categoryId)
        }
    }

    fun unbindQuestionWithCategory(questionId: Long, categoryId: Long) {
        scope.launch {
            repository.unbindQuestionWithCategory(questionId, categoryId)
        }
    }

    fun getCoroutineContext() = scope.coroutineContext
}