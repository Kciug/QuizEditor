package com.rafalskrzypczyk.cem_mode.data

import com.rafalskrzypczyk.cem_mode.domain.CemModeRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.cem_mode.domain.models.CemQuestion
import com.rafalskrzypczyk.cem_mode.domain.models.toDTO
import com.rafalskrzypczyk.cem_mode.domain.models.toDomain
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
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

    override fun getCategoryById(categoryId: Long): Flow<Response<CemCategory>> =
        firestoreApi.getCemCategoryById(categoryId).map { response ->
            when (response) {
                is Response.Success -> Response.Success(response.data.toDomain())
                is Response.Error -> Response.Error(response.error)
                is Response.Loading -> Response.Loading
            }
        }

    override fun getUpdatedCategoryById(categoryId: Long): Flow<CemCategory?> =
        firestoreApi.getUpdatedCemCategoryById(categoryId).map { it?.toDomain() }

    override suspend fun addCategory(category: CemCategory): Response<Unit> {
        val result = firestoreApi.addCemCategory(category.toDTO())
        if (result is Response.Success && category.parentCategoryID != null) {
            val parentResponse = getCategoryById(category.parentCategoryID).last()
            if (parentResponse is Response.Success) {
                val parent = parentResponse.data
                if (!parent.linkedSubcategories.contains(category.id)) {
                    parent.linkedSubcategories.add(category.id)
                    updateCategory(parent)
                }
            }
        }
        return result
    }

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

    override fun getQuestionById(questionId: Long): Flow<Response<CemQuestion>> =
        firestoreApi.getCemQuestionById(questionId).map { response ->
            when (response) {
                is Response.Success -> Response.Success(response.data.toDomain())
                is Response.Error -> Response.Error(response.error)
                is Response.Loading -> Response.Loading
            }
        }

    override fun getUpdatedQuestionById(questionId: Long): Flow<CemQuestion?> =
        firestoreApi.getUpdatedCemQuestionById(questionId).map { it?.toDomain() }

    override suspend fun addQuestion(question: CemQuestion): Response<Unit> =
        firestoreApi.addCemQuestion(question.toDTO())

    override suspend fun updateQuestion(question: CemQuestion): Response<Unit> =
        firestoreApi.updateCemQuestion(question.toDTO())

    override suspend fun deleteQuestion(questionId: Long): Response<Unit> =
        firestoreApi.deleteCemQuestion(questionId)

    override suspend fun bindQuestionWithCategory(questionId: Long, categoryId: Long) {
        val questionResponse = getQuestionById(questionId).last()
        val categoryResponse = getCategoryById(categoryId).last()

        if (questionResponse is Response.Success && categoryResponse is Response.Success) {
            val question = questionResponse.data
            val category = categoryResponse.data

            if (!question.linkedCategories.contains(categoryId)) question.linkedCategories.add(categoryId)
            if (!category.linkedQuestions.contains(questionId)) category.linkedQuestions.add(questionId)

            updateCategory(category)
            updateQuestion(question)
        }
    }

    override suspend fun unbindQuestionWithCategory(questionId: Long, categoryId: Long) {
        val questionResponse = getQuestionById(questionId).last()
        val categoryResponse = getCategoryById(categoryId).last()

        if (questionResponse is Response.Success && categoryResponse is Response.Success) {
            val question = questionResponse.data
            val category = categoryResponse.data

            if (question.linkedCategories.remove(categoryId) && category.linkedQuestions.remove(questionId)) {
                updateCategory(category)
                updateQuestion(question)
            }
        }
    }
}
