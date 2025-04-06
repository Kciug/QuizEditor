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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizModeRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val resourceProvider: ResourceProvider,
) : QuizModeRepository {
    override fun getAllCategories(): Flow<Response<List<Category>>> =
        firestoreApi.getQuizCategories().map { response ->
            when (response) {
                is Response.Success -> Response.Success(response.data.map { it.toDomain() })
                is Response.Error -> response
                is Response.Loading -> response
            }
        }

    override fun getUpdatedCategories(): Flow<List<Category>> =
        firestoreApi.getUpdatedQuizCategories().map { it.map { dto -> dto.toDomain() } }

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
            when (response) {
                is Response.Success -> Response.Success(response.data.map { it.toDomain() })
                is Response.Error -> response
                is Response.Loading -> response
            }
        }

    override fun getUpdatedQuestions(): Flow<List<Question>> =
        firestoreApi.getUpdatedQuizQuestions().map { it.map { dto -> dto.toDomain() } }

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
        val questionResponse = getQuestionById(questionId).last()
        val categoryResponse = getCategoryById(categoryId).last()

        if (questionResponse is Response.Success && categoryResponse is Response.Success) {
            val question = questionResponse.data
            val category = categoryResponse.data

            if (!question.linkedCategories.contains(categoryId)) question.linkedCategories.add(categoryId)
            if (!category.linkedQuestions.contains(questionId)) category.linkedQuestions.add(questionId)

            updateCategory(category)
            updateQuestion(question)
        } else {
            throw Exception("Failed to fetch question or category")
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

    private suspend fun unbindAllQuestions(categoryId: Long){
        val questions = getAllQuestions().first()

        getCategoryById(categoryId).first().let {
            if(it is Response.Success && questions is Response.Success) {
                it.data.linkedQuestions.forEach { questionId ->
                    questions.data.find { it.id == questionId }?.let {
                        it.linkedCategories.remove(categoryId)
                        updateQuestion(it)
                    }
                }
            }
        }
    }

    private suspend fun unbindAllCategories(questionId: Long){
        val categories = getAllCategories().first()

        getQuestionById(questionId).first().let {
            if(it is Response.Success && categories is Response.Success) {
                it.data.linkedCategories.forEach { categoryId ->
                    categories.data.find { it.id == categoryId }?.let {
                        it.linkedQuestions.remove(questionId)
                        updateCategory(it)
                    }
                }
            }
        }
    }
}