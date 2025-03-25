package com.rafalskrzypczyk.swipe_mode.data

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeRepository
import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestion
import com.rafalskrzypczyk.swipe_mode.domain.toDTO
import com.rafalskrzypczyk.swipe_mode.domain.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SwipeModeRepositoryImpl @Inject constructor(
    private val firestore: FirestoreApi
) : SwipeModeRepository {
    private var cachedQuestions: List<SwipeQuestion>? = null

    override fun getAllQuestions(): Flow<Response<List<SwipeQuestion>>> =
        if(cachedQuestions != null) flowOf(Response.Success(cachedQuestions!!))
        else firestore.getSwipeQuestions().map { response ->
            when (response) {
                is Response.Success -> {
                    cachedQuestions = response.data.map { it.toDomain() }
                    Response.Success(cachedQuestions!!)
                }
                is Response.Error -> response
                is Response.Loading -> response
            }
        }

    override fun getUpdatedQuestions(): Flow<List<SwipeQuestion>> =
        firestore.getUpdatedSwipeQuestions().map { it.map { dto -> dto.toDomain() }.also { cachedQuestions = it } }

    override fun getQuestionById(questionId: Long): Flow<Response<SwipeQuestion>> =
        getAllQuestions().map {
            when (it) {
                is Response.Success -> it.data.find { it.id == questionId }
                    ?.let { Response.Success(it) }
                    ?: Response.Error("Question not found")
                is Response.Error -> it
                is Response.Loading -> it
            }
        }

    override suspend fun addQuestion(question: SwipeQuestion): Response<Unit> =
        firestore.addSwipeQuestion(question.toDTO())

    override suspend fun updateQuestion(question: SwipeQuestion): Response<Unit> =
        firestore.updateSwipeQuestion(question.toDTO())

    override suspend fun deleteQuestion(questionId: Long): Response<Unit> =
        firestore.deleteSwipeQuestion(questionId)
}