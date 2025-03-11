package com.rafalskrzypczyk.quiz_mode.data

import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.domain.models.toDTO
import com.rafalskrzypczyk.quiz_mode.domain.models.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizModeRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val resourceProvider: ResourceProvider,
) : QuizModeRepository {
    private var _cachedCategories: List<Category>? = null
    private var _cachedQuestions: List<Question>? = null

    override fun getAllCategories(): Flow<Response<List<Category>>> {
        return firestoreApi.getQuizCategories().map {
            if (_cachedCategories != null) {
                return@map Response.Success(_cachedCategories!!)
            }
            when (it) {
                is Response.Error -> Response.Error(it.error)
                is Response.Loading -> Response.Loading
                is Response.Success -> {
                    _cachedCategories = it.data.map { it.toDomain() }
                    Response.Success(_cachedCategories!!)
                }
            }
        }
    }

    override fun getUpdatedCategories(): Flow<List<Category>> {
        return firestoreApi.getUpdatedQuizCategories().map {
            _cachedCategories = it.map { it.toDomain() }
            _cachedCategories!!
        }
    }

    override fun getCategoryById(categoryId: Long): Flow<Response<Category>> =
        getAllCategories().map {
            when (it) {
                is Response.Error -> Response.Error(it.error)
                is Response.Loading -> Response.Loading
                is Response.Success -> {
                    val category = it.data.find { it.id == categoryId }
                    if (category == null) Response.Error(resourceProvider.getString(R.string.error_not_found_category))
                    else Response.Success(category)
                }
            }
        }


    override suspend fun addCategory(category: Category): Response<Unit> =
        firestoreApi.addQuizCategory(category.toDTO())

    override suspend fun updateCategory(category: Category): Response<Unit> =
        firestoreApi.updateQuizCategory(category.toDTO())

    override suspend fun deleteCategory(categoryId: Long): Response<Unit> {
        unbindAllQuestions(categoryId)
        return firestoreApi.deleteQuizCategory(categoryId)
    }

    override fun getAllQuestions(): Flow<Response<List<Question>>> {
        return firestoreApi.getQuizQuestions().map {
            if (_cachedQuestions != null) {
                return@map Response.Success(_cachedQuestions!!)
            }
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

    override fun getUpdatedQuestions(): Flow<List<Question>> {
        return firestoreApi.getUpdatedQuizQuestions().map {
            _cachedQuestions = it.map { it.toDomain() }
            _cachedQuestions!!
        }
    }

    override fun getQuestionById(questionId: Long): Flow<Response<Question>> = getAllQuestions().map {
        when (it) {
            is Response.Error -> Response.Error(it.error)
            is Response.Loading -> Response.Loading
            is Response.Success -> {
                val question = it.data.find { it.id == questionId }
                if (question == null) Response.Error(resourceProvider.getString(R.string.error_not_found_question))
                else Response.Success(question)
            }
        }
    }

    override suspend fun addQuestion(question: Question): Response<Unit> =
        firestoreApi.addQuizQuestion(question.toDTO())

    override suspend fun updateQuestion(question: Question): Response<Unit> =
        firestoreApi.updateQuizQuestion(question.toDTO())


    override suspend fun deleteQuestion(questionId: Long): Response<Unit> {
        unbindAllCategories(questionId)
        return firestoreApi.deleteQuizQuestion(questionId)
    }


    override suspend fun bindQuestionWithCategory(questionId: Long, categoryId: Long) {
        val question = _cachedQuestions?.find { it.id == questionId }
        val category = _cachedCategories?.find { it.id == categoryId }

        if (question == null || category == null) return

        if(!question.linkedCategories.contains(categoryId)) question.linkedCategories.add(categoryId)
        if(!category.linkedQuestions.contains(questionId)) category.linkedQuestions.add(questionId)

        updateCategory(category)
        updateQuestion(question)
    }

    override suspend fun unbindQuestionWithCategory(questionId: Long, categoryId: Long) {
        val question = _cachedQuestions?.find { it.id == questionId }
        val category = _cachedCategories?.find { it.id == categoryId }
        if (question == null || category == null) return

        question.linkedCategories.remove(categoryId)
        category.linkedQuestions.remove(questionId)

        updateCategory(category)
        updateQuestion(question)
    }

    private suspend fun unbindAllQuestions(categoryId: Long){
        val category = _cachedCategories?.find { it.id == categoryId } ?: return
        category.linkedQuestions.forEach { questionId ->
            _cachedQuestions?.find { it.id == questionId }?.let {
                it.linkedCategories.remove(categoryId)
                updateQuestion(it)
            }
        }
    }

    private suspend fun unbindAllCategories(questionId: Long){
        val question = _cachedQuestions?.find { it.id == questionId } ?: return
        question.linkedCategories.forEach { categoryId ->
            _cachedCategories?.find { it.id == categoryId }?.let {
                it.linkedQuestions.remove(questionId)
                updateCategory(it)
            }
        }
    }
}