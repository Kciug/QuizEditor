package com.rafalskrzypczyk.firestore.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.data_statistics.DataStatistics
import com.rafalskrzypczyk.firestore.data.models.CategoryDTO
import com.rafalskrzypczyk.firestore.data.models.MessageDTO
import com.rafalskrzypczyk.firestore.data.models.QuestionDTO
import com.rafalskrzypczyk.firestore.data.models.SwipeQuestionDTO
import com.rafalskrzypczyk.firestore.data.models.TranslationQuestionDTO
import com.rafalskrzypczyk.firestore.data.models.UserDataDTO
import kotlinx.coroutines.flow.Flow

interface FirestoreApi {
    fun getUserData(userId: String): Flow<Response<UserDataDTO>>

    suspend fun getDatabaseStatistics() : Flow<Response<DataStatistics>>

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

    fun getSwipeQuestions(): Flow<Response<List<SwipeQuestionDTO>>>
    fun getUpdatedSwipeQuestions(): Flow<List<SwipeQuestionDTO>>
    suspend fun addSwipeQuestion(question: SwipeQuestionDTO): Response<Unit>
    suspend fun updateSwipeQuestion(question: SwipeQuestionDTO): Response<Unit>
    suspend fun deleteSwipeQuestion(questionId: Long): Response<Unit>

    fun getLatestMessages(): Flow<Response<List<MessageDTO>>>
    fun getOlderMessages(): Flow<Response<List<MessageDTO>>>
    fun getUpdatedMessages(): Flow<List<MessageDTO>>
    suspend fun sendMessage(message: MessageDTO): Response<Unit>

    fun getTranslationQuestions(): Flow<Response<List<TranslationQuestionDTO>>>
    fun getUpdatedTranslationQuestions(): Flow<List<TranslationQuestionDTO>>
    suspend fun addTranslationQuestion(question: TranslationQuestionDTO): Response<Unit>
    suspend fun updateTranslationQuestion(question: TranslationQuestionDTO): Response<Unit>
    suspend fun deleteTranslationQuestion(questionId: Long): Response<Unit>
}