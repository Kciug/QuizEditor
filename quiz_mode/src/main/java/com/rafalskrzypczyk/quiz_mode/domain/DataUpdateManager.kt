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
    private val backgroundUpdateScope = CoroutineScope(SupervisorJob() + dispatcher)

    fun updateCategory(category: Category) {
        backgroundUpdateScope.launch {
            repository.updateCategory(category)
        }
    }

    fun updateQuestion(question: Question) {
        backgroundUpdateScope.launch {
            repository.updateQuestion(question)
        }
    }

    fun bindQuestionWithCategory(questionId: Long, categoryId: Long) {
        backgroundUpdateScope.launch {
            repository.bindQuestionWithCategory(questionId, categoryId)
        }
    }

    fun unbindQuestionWithCategory(questionId: Long, categoryId: Long) {
        backgroundUpdateScope.launch {
            repository.unbindQuestionWithCategory(questionId, categoryId)
        }
    }
}