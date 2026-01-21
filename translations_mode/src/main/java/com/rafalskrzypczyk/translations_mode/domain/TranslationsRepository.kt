package com.rafalskrzypczyk.translations_mode.domain

import com.rafalskrzypczyk.core.api_result.Response
import kotlinx.coroutines.flow.Flow

interface TranslationsRepository {
    fun getAllQuestions(): Flow<Response<List<TranslationQuestion>>>
    fun getUpdatedQuestions(): Flow<List<TranslationQuestion>>
    fun getQuestionById(questionId: Long): Flow<Response<TranslationQuestion>>
    suspend fun addQuestion(question: TranslationQuestion): Response<Unit>
    suspend fun updateQuestion(question: TranslationQuestion): Response<Unit>
    suspend fun deleteQuestion(questionId: Long): Response<Unit>
}
