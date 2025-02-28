package com.rafalskrzypczyk.quiz_mode.data

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.extensions.updateById
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.domain.models.toDTO
import com.rafalskrzypczyk.quiz_mode.domain.models.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizModeRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi
) : QuizModeRepository {
    private var _cachedCategories: MutableList<Category>? = null
    private var _cachedQuestions: MutableList<Question>? = null

    override fun getAllCategories(): Flow<Response<List<Category>>> {
        return if (_cachedCategories != null) {
            flowOf(Response.Success(_cachedCategories!!))
        } else {
            firestoreApi.getAllCategories().map {
                when (it) {
                    is Response.Error -> Response.Error(it.error)
                    is Response.Loading -> Response.Loading
                    is Response.Success -> {
                        _cachedCategories = it.data.map { it.toDomain() }.toMutableList()
                        Response.Success(_cachedCategories!!)
                    }
                }
            }
        }
    }

    override fun getCategoryById(categoryId: Int): Flow<Response<Category>> {
        return flow {
            val category = _cachedCategories?.find { it.id == categoryId }
            if (category == null) emit(Response.Error("NIE MA KURWA TAKIEJ KATEGORII"))
            else emit(Response.Success(category))
        }
    }

    override suspend fun addCategory(category: Category): Response<Unit> {
        val response = firestoreApi.addCategory(category.toDTO())
        return when (response) {
            is Response.Success -> {
                _cachedCategories?.add(category)
                Response.Success(Unit)
            }

            is Response.Error -> Response.Error(response.error)
            is Response.Loading -> Response.Loading
        }
    }

    override suspend fun updateCategory(category: Category): Response<Unit> {
        var response = firestoreApi.updateCategory(category.toDTO())
        if (response is Response.Success) _cachedCategories?.updateById(category)
        return response
    }

    override suspend fun deleteCategory(categoryId: Int): Response<Unit> {
        val response = firestoreApi.deleteCategory(categoryId)
        if (response is Response.Success) _cachedCategories?.removeIf{ it.id == categoryId }
        return response
    }

    override fun getAllQuestions(): Flow<Response<List<Question>>> {
        return if (_cachedQuestions != null) {
            flowOf(Response.Success(_cachedQuestions!!))
        } else {
            firestoreApi.getAllQuestions().map {
                when (it) {
                    is Response.Error -> Response.Error(it.error)
                    is Response.Loading -> Response.Loading
                    is Response.Success -> {
                        _cachedQuestions = it.data.map { it.toDomain() }.toMutableList()
                        Response.Success(_cachedQuestions!!)
                    }
                }
            }
        }
    }

    override fun getQuestionById(questionId: Int): Flow<Response<Question>> {
        return flow {
            val question = _cachedQuestions?.find { it.id == questionId }
            if (question == null) emit(Response.Error("NIE MA KURWA TAKIEGO PYTANIA"))
            else emit(Response.Success(question))
        }
    }

    override suspend fun updateQuestion(question: Question): Response<Unit> {
        var response = firestoreApi.updateQuestion(question.toDTO())
        if (response is Response.Success) _cachedQuestions?.updateById(question)
        return response
    }

    override suspend fun saveQuestion(question: Question): Response<Unit> {
        val response = firestoreApi.addQuestion(question.toDTO())
        return when (response) {
            is Response.Success -> {
                _cachedQuestions?.add(question)
                Response.Success(Unit)
            }

            is Response.Error -> Response.Error(response.error)
            is Response.Loading -> Response.Loading
        }
    }

    override suspend fun deleteQuestion(questionId: Int): Response<Unit> {
        val response = firestoreApi.deleteQuestion(questionId)
        if (response is Response.Success) _cachedQuestions?.removeIf{ it.id == questionId }
        return response
    }

    override fun bindQuestionWithCategory(questionId: Int, categoryId: Int) {
        val question = _cachedQuestions?.find { it.id == questionId }
        val category = _cachedCategories?.find { it.id == categoryId }
        if (question == null || category == null) return

        question.linkedCategories.add(categoryId)
        category.linkedQuestions.add(questionId)
    }

    override fun unbindQuestionWithCategory(questionId: Int, categoryId: Int) {
        val question = _cachedQuestions?.find { it.id == questionId }
        val category = _cachedCategories?.find { it.id == categoryId }
        if (question == null || category == null) return

        question.linkedCategories.remove(categoryId)
        category.linkedQuestions.remove(questionId)
    }
}