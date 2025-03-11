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

    override fun getAllCategories(): Flow<Response<List<Category>>> =
        firestoreApi.getQuizCategories().map { response ->
            _cachedCategories?.let { return@map Response.Success(it) }
            when (response) {
                is Response.Success -> {
                    _cachedCategories = response.data.map { it.toDomain() }
                    Response.Success(_cachedCategories!!)
                }
                is Response.Error -> response
                is Response.Loading -> response
            }
        }

    override fun getUpdatedCategories(): Flow<List<Category>> =
        firestoreApi.getUpdatedQuizCategories().map { it.map { dto -> dto.toDomain() }.also { _cachedCategories = it } }

    override fun getCategoryById(categoryId: Long): Flow<Response<Category>> =
        getAllCategories().map { response ->
            when (response) {
                is Response.Success -> response.data.find { it.id == categoryId }
                    ?.let { Response.Success(it) }
                    ?: Response.Error(resourceProvider.getString(R.string.error_not_found_category))
                is Response.Error -> response
                is Response.Loading -> response
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

    override fun getAllQuestions(): Flow<Response<List<Question>>> =
        firestoreApi.getQuizQuestions().map { response ->
            _cachedQuestions?.let { return@map Response.Success(it) }
            when (response) {
                is Response.Success -> {
                    _cachedQuestions = response.data.map { it.toDomain() }
                    Response.Success(_cachedQuestions!!)
                }
                is Response.Error -> response
                is Response.Loading -> response
            }
        }

    override fun getUpdatedQuestions(): Flow<List<Question>> =
        firestoreApi.getUpdatedQuizQuestions().map { it.map { dto -> dto.toDomain() }.also { _cachedQuestions = it } }

    override fun getQuestionById(questionId: Long): Flow<Response<Question>> =
        getAllQuestions().map { response ->
        when (response) {
            is Response.Success -> response.data.find { it.id == questionId }
                ?.let { Response.Success(it) }
                ?: Response.Error(resourceProvider.getString(R.string.error_not_found_question))
            is Response.Error -> response
            is Response.Loading -> response
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
        val question = _cachedQuestions?.find { it.id == questionId } ?: return
        val category = _cachedCategories?.find { it.id == categoryId } ?: return

        if(!question.linkedCategories.contains(categoryId)) question.linkedCategories.add(categoryId)
        if(!category.linkedQuestions.contains(questionId)) category.linkedQuestions.add(questionId)

        updateCategory(category)
        updateQuestion(question)
    }

    override suspend fun unbindQuestionWithCategory(questionId: Long, categoryId: Long) {
        val question = _cachedQuestions?.find { it.id == questionId } ?: return
        val category = _cachedCategories?.find { it.id == categoryId } ?: return

        if(question.linkedCategories.remove(categoryId) && category.linkedQuestions.remove(questionId)){
            updateCategory(category)
            updateQuestion(question)
        }
    }

    private suspend fun unbindAllQuestions(categoryId: Long){
        _cachedCategories?.find { it.id == categoryId }?.linkedQuestions?.forEach { questionId ->
            _cachedQuestions?.find { it.id == questionId }?.let {
                it.linkedCategories.remove(categoryId)
                updateQuestion(it)
            }
        }
    }

    private suspend fun unbindAllCategories(questionId: Long){
        _cachedQuestions?.find { it.id == questionId }?.linkedCategories?.forEach { categoryId ->
            _cachedCategories?.find { it.id == categoryId }?.let {
                it.linkedQuestions.remove(questionId)
                updateCategory(it)
            }
        }
    }
}