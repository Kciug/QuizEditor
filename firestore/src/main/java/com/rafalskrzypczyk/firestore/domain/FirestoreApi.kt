package com.rafalskrzypczyk.firestore.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.firestore.data.models.CategoryDTO
import com.rafalskrzypczyk.firestore.data.models.QuestionDTO
import com.rafalskrzypczyk.firestore.data.models.UserDataDTO
import kotlinx.coroutines.flow.Flow

interface FirestoreApi {
    fun getUserData(userId: String): Flow<Response<UserDataDTO>>

    fun getQuizCategories(): Flow<Response<List<CategoryDTO>>>
    fun getUpdatedQuizCategories(): Flow<List<CategoryDTO>>
    suspend fun addQuizCategory(category: CategoryDTO): Response<Unit>
    suspend fun updateQuizCategory(category: CategoryDTO): Response<Unit>
    suspend fun deleteQuizCategory(categoryId: Long): Response<Unit>

    fun getQuizQuestions(): Flow<Response<List<QuestionDTO>>>
    fun getUpdatedQuizQuestions(): Flow<List<QuestionDTO>>
    suspend fun addQuizQuestion(question: QuestionDTO): Response<Unit>
    suspend fun updateQuizQuestion(question: QuestionDTO): Response<Unit>
    suspend fun deleteQuizQuestion(questionId: Long): Response<Unit>
}