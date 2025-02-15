package com.rafalskrzypczyk.quiz_mode.data

import com.rafalskrzypczyk.core.api_result.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FirestoreApiMock @Inject constructor() : FirestoreApi {
    private val categories = mutableListOf<CategoryDTO>()
    private val questions = mutableListOf<QuestionDTO>()

    override suspend fun getUserData(): Flow<Response<UserDTO>> {
        return flow{
            emit(Response.Success(UserDTO(
                id = "2137"
            )))
        }
    }

    override suspend fun updateUserData(userData: UserDTO): Response<Unit> {
        return Response.Success(Unit)
    }

    override fun getAllCategories(): Flow<Response<List<CategoryDTO>>> {
        return flowOf(Response.Success(categories))
    }

    override suspend fun addCategory(category: CategoryDTO): Response<Unit> {
        categories.add(category)
        return Response.Success(Unit)
    }

    override suspend fun updateCategory(category: CategoryDTO): Response<Unit> {
        val index = categories.indexOfFirst { it.id == category.id }
        if(index != -1) {
            categories[index] = category
            return Response.Success(Unit)
        }
        else return Response.Error("Category not found")
    }

    override suspend fun deleteCategory(categoryId: Int): Response<Unit> {
        val index = categories.indexOfFirst { it.id == categoryId }
        if(index != -1) {
            categories.removeAt(index)
            return Response.Success(Unit)
        }
        else return Response.Error("Category not found")
    }

    override fun getAllQuestions(): Flow<Response<List<QuestionDTO>>> {
        return flowOf(Response.Success(questions))
    }

    override suspend fun addQuestion(question: QuestionDTO): Response<Unit> {
        questions.add(question)
        return Response.Success(Unit)
    }

    override suspend fun updateQuestion(question: QuestionDTO): Response<Unit> {
        val index = questions.indexOfFirst { it.id == question.id }
        if(index != -1) {
            questions[index] = question
            return Response.Success(Unit)
        }
        else return Response.Error("Category not found")
    }

    override suspend fun deleteQuestion(questionId: Int): Response<Unit> {
        val index = questions.indexOfFirst { it.id == questionId }
        if(index != -1) {
            questions.removeAt(index)
            return Response.Success(Unit)
        }
        else return Response.Error("Category not found")
    }

}