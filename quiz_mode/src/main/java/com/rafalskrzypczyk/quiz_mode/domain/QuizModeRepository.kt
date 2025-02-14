package com.rafalskrzypczyk.quiz_mode.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import kotlinx.coroutines.flow.Flow

interface QuizModeRepository {
    fun getAllCategories(): Flow<Response<List<Category>>>
    fun getCategoryById(categoryId: Int): Flow<Response<Category>>
    suspend fun addCategory(categoryTitle: String) : Response<Int>
    suspend fun updateCategory(category: Category) : Response<Unit>
    suspend fun deleteCategory(categoryId: Int) : Response<Unit>

    fun getAllQuestions(): Flow<Response<List<Question>>>
    fun getQuestionById(questionId: Int): Flow<Response<Question>>
    suspend fun updateQuestion(question: Question) : Response<Unit>
    suspend fun saveQuestion(questionText: String) : Response<Int>
    suspend fun deleteQuestion(questionId: Int) : Response<Unit>
}