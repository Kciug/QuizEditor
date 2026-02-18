package com.rafalskrzypczyk.cem_mode.data

import com.rafalskrzypczyk.cem_mode.domain.CemModeRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.cem_mode.domain.models.CemQuestion
import com.rafalskrzypczyk.cem_mode.domain.models.toDTO
import com.rafalskrzypczyk.cem_mode.domain.models.toDomain
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CemModeRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi
) : CemModeRepository {

    override fun getCategories(): Flow<Response<List<CemCategory>>> =
        firestoreApi.getCemCategories().map { response ->
            when (response) {
                is Response.Success -> Response.Success(response.data.map { it.toDomain() })
                is Response.Error -> Response.Error(response.error)
                is Response.Loading -> Response.Loading
            }
        }

    override fun getUpdatedCategories(): Flow<List<CemCategory>> =
        firestoreApi.getUpdatedCemCategories().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun addCategory(category: CemCategory): Response<Unit> =
        firestoreApi.addCemCategory(category.toDTO())

    override suspend fun updateCategory(category: CemCategory): Response<Unit> =
        firestoreApi.updateCemCategory(category.toDTO())

    override suspend fun deleteCategory(categoryId: Long): Response<Unit> =
        firestoreApi.deleteCemCategory(categoryId)

    override fun getQuestions(): Flow<Response<List<CemQuestion>>> =
        firestoreApi.getCemQuestions().map { response ->
            when (response) {
                is Response.Success -> Response.Success(response.data.map { it.toDomain() })
                is Response.Error -> Response.Error(response.error)
                is Response.Loading -> Response.Loading
            }
        }

    override fun getUpdatedQuestions(): Flow<List<CemQuestion>> =
        firestoreApi.getUpdatedCemQuestions().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun addQuestion(question: CemQuestion): Response<Unit> =
        firestoreApi.addCemQuestion(question.toDTO())

    override suspend fun updateQuestion(question: CemQuestion): Response<Unit> =
        firestoreApi.updateCemQuestion(question.toDTO())

    override suspend fun deleteQuestion(questionId: Long): Response<Unit> =
        firestoreApi.deleteCemQuestion(questionId)
}
