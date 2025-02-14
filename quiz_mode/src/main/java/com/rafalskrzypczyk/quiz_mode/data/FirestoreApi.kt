package com.rafalskrzypczyk.quiz_mode.data

import com.rafalskrzypczyk.core.api_result.Response
import kotlinx.coroutines.flow.Flow

interface FirestoreApi {
    suspend fun getUserData(): Flow<Response<UserDTO>>
    suspend fun updateUserData(userData: UserDTO) : Response<Unit>

    fun getAllCategories(): Flow<Response<List<CategoryDTO>>>
    suspend fun addCategory(category: CategoryDTO) : Response<Unit>
    suspend fun updateCategory(category: CategoryDTO) : Response<Unit>
    suspend fun deleteCategory(categoryId: Int) : Response<Unit>

    fun getAllQuestions(): Flow<Response<List<QuestionDTO>>>
    suspend fun addQuestion(question: QuestionDTO) : Response<Unit>
    suspend fun updateQuestion(question: QuestionDTO) : Response<Unit>
    suspend fun deleteQuestion(questionId: Int) : Response<Unit>
}