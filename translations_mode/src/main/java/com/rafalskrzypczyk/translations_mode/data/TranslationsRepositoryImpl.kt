package com.rafalskrzypczyk.translations_mode.data

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.translations_mode.domain.TranslationQuestion
import com.rafalskrzypczyk.translations_mode.domain.TranslationsRepository
import com.rafalskrzypczyk.translations_mode.domain.toDTO
import com.rafalskrzypczyk.translations_mode.domain.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TranslationsRepositoryImpl @Inject constructor(
    private val firestore: FirestoreApi
) : TranslationsRepository {
    override fun getAllQuestions(): Flow<Response<List<TranslationQuestion>>> =
        firestore.getTranslationQuestions().map { response ->
            when (response) {
                is Response.Success -> Response.Success(response.data.map { it.toDomain() })
                is Response.Error -> response
                is Response.Loading -> response
            }
        }

    override fun getUpdatedQuestions(): Flow<List<TranslationQuestion>> =
        firestore.getUpdatedTranslationQuestions().map { it.map { dto -> dto.toDomain() } }

    override fun getQuestionById(questionId: Long): Flow<Response<TranslationQuestion>> =
        getAllQuestions().map {
            when (it) {
                is Response.Success -> it.data.find { it.id == questionId }
                    ?.let { Response.Success(it) }
                    ?: Response.Error("Question not found")
                is Response.Error -> it
                is Response.Loading -> it
            }
        }

    override suspend fun addQuestion(question: TranslationQuestion): Response<Unit> =
        firestore.addTranslationQuestion(question.toDTO())

    override suspend fun updateQuestion(question: TranslationQuestion): Response<Unit> =
        firestore.updateTranslationQuestion(question.toDTO())

    override suspend fun deleteQuestion(questionId: Long): Response<Unit> =
        firestore.deleteTranslationQuestion(questionId)
}
