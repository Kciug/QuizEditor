package com.rafalskrzypczyk.quiz_mode.data

import android.util.Log
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
                Log.d("KURWA", "QMRepo:getAllCategories: cache")
                return@map Response.Success(_cachedCategories!!)
            }
            when (it) {
                is Response.Error -> Response.Error(it.error)
                is Response.Loading -> Response.Loading
                is Response.Success -> {
                    Log.d("KURWA", "QMRepo:getAllCategories: fs")
                    _cachedCategories = it.data.map { it.toDomain() }
                    Log.d("KURWA", "QMRepo:getCategoryById: $_cachedCategories")
                    Response.Success(_cachedCategories!!)
                }
            }
        }
    }

    override fun getUpdatedCategories(): Flow<List<Category>> {
        return firestoreApi.getUpdatedQuizCategories().map {
            Log.d("KURWA", "QMRepo:updateCategories: ${it}")
            _cachedCategories = it.map { it.toDomain() }
            _cachedCategories!!
        }
    }

    override fun getCategoryById(categoryId: Long): Response<Category> {
        val fetchedCategory = _cachedCategories?.firstOrNull { it.id == categoryId }
        return if(fetchedCategory == null) Response.Error(resourceProvider.getString(R.string.error_not_found_category))
        else Response.Success(fetchedCategory)
    }

    override suspend fun addCategory(category: Category): Response<Unit> =
        firestoreApi.addQuizCategory(category.toDTO())

    override suspend fun updateCategory(category: Category): Response<Unit> =
        firestoreApi.updateQuizCategory(category.toDTO())

    override suspend fun deleteCategory(categoryId: Long): Response<Unit> =
        firestoreApi.deleteQuizCategory(categoryId)

    override fun getAllQuestions(): Flow<Response<List<Question>>> {
        return firestoreApi.getQuizQuestions().map {
            if (_cachedQuestions != null) {
                Log.d("KURWA", "QMRepo:getAllQuestions: cache")
                return@map Response.Success(_cachedQuestions!!)
            }
            when (it) {
                is Response.Error -> Response.Error(it.error)
                is Response.Loading -> Response.Loading
                is Response.Success -> {
                    Log.d("KURWA", "QMRepo:getAllQuestions: fs")
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

    override fun getQuestionById(questionId: Long): Response<Question> {
        val fetchedQuestion = _cachedQuestions?.firstOrNull { it.id == questionId }
        return if(fetchedQuestion == null) Response.Error(resourceProvider.getString(R.string.error_not_found_question))
        else Response.Success(fetchedQuestion)
    }

    override suspend fun addQuestion(question: Question): Response<Unit> =
        firestoreApi.addQuizQuestion(question.toDTO())

    override suspend fun updateQuestion(question: Question): Response<Unit> =
        firestoreApi.updateQuizQuestion(question.toDTO())

    override suspend fun deleteQuestion(questionId: Long): Response<Unit> =
        firestoreApi.deleteQuizQuestion(questionId)

    override suspend fun bindQuestionWithCategory(questionId: Long, categoryId: Long) {
        val question = _cachedQuestions?.find { it.id == questionId }
        val category = _cachedCategories?.find { it.id == categoryId }
        if (question == null || category == null) return

        question.linkedCategories.add(categoryId)
        category.linkedQuestions.add(questionId)

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
}