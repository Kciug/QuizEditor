package com.rafalskrzypczyk.cem_mode.domain

import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.cem_mode.domain.models.CemQuestion
import com.rafalskrzypczyk.core.api_result.Response
import kotlinx.coroutines.flow.Flow

interface CemModeRepository {
    fun getCategories(): Flow<Response<List<CemCategory>>>
    fun getUpdatedCategories(): Flow<List<CemCategory>>
    fun getCategoryById(categoryId: Long): Flow<Response<CemCategory>>
    suspend fun addCategory(category: CemCategory): Response<Unit>
    suspend fun updateCategory(category: CemCategory): Response<Unit>
    suspend fun deleteCategory(categoryId: Long): Response<Unit>

    fun getQuestions(): Flow<Response<List<CemQuestion>>>
    fun getUpdatedQuestions(): Flow<List<CemQuestion>>
    fun getQuestionById(questionId: Long): Flow<Response<CemQuestion>>
    suspend fun addQuestion(question: CemQuestion): Response<Unit>
    suspend fun updateQuestion(question: CemQuestion): Response<Unit>
    suspend fun deleteQuestion(questionId: Long): Response<Unit>

    suspend fun bindQuestionWithCategory(questionId: Long, categoryId: Long)
    suspend fun unbindQuestionWithCategory(questionId: Long, categoryId: Long)
}
