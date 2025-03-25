package com.rafalskrzypczyk.swipe_mode.domain

import com.rafalskrzypczyk.core.api_result.Response
import kotlinx.coroutines.flow.Flow

interface SwipeModeRepository {
    fun getAllQuestions(): Flow<Response<List<SwipeQuestion>>>
    fun getUpdatedQuestions(): Flow<List<SwipeQuestion>>
    fun getQuestionById(questionId: Long): Flow<Response<SwipeQuestion>>
    suspend fun addQuestion(question: SwipeQuestion): Response<Unit>
    suspend fun updateQuestion(question: SwipeQuestion): Response<Unit>
    suspend fun deleteQuestion(questionId: Long): Response<Unit>
}