package com.rafalskrzypczyk.quiz_mode.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import kotlinx.coroutines.flow.Flow

interface QuizModeRepository {
    fun getAllCategories(): Flow<Response<List<Category>>>
    fun getUpdatedCategories(): Flow<List<Category>>
    fun getCategoryById(categoryId: Long): Flow<Response<Category>>
    suspend fun addCategory(category: Category): Response<Unit>
    suspend fun updateCategory(category: Category): Response<Unit>
    suspend fun deleteCategory(categoryId: Long): Response<Unit>

    fun getAllQuestions(): Flow<Response<List<Question>>>
    fun getUpdatedQuestions(): Flow<List<Question>>
    fun getQuestionById(questionId: Long): Flow<Response<Question>>
    suspend fun addQuestion(question: Question): Response<Unit>
    suspend fun updateQuestion(question: Question): Response<Unit>
    suspend fun deleteQuestion(questionId: Long): Response<Unit>

    suspend fun bindQuestionWithCategory(questionId: Long, categoryId: Long)
    suspend fun unbindQuestionWithCategory(questionId: Long, categoryId: Long)
}