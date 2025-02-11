package com.rafalskrzypczyk.quiz_mode.data

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuizModeRepositoryMock @Inject constructor() : QuizModeRepository {
    private val categories = mutableListOf<Category>()
    private val questions = mutableListOf<Question>()

    override fun getAllCategories(): Flow<Response<List<Category>>> = flow {
        emit(Response.Success(categories))
    }

    override fun getCategoryById(categoryId: Int): Flow<Response<Category>> = flow {
        val category = categories.find { it.id == categoryId }
        if(category == null) emit(Response.Error("NIE MA KURWA TAKIEJ KATEGORII"))
        else emit(Response.Success(category))
    }

    override suspend fun addCategory(category: Category): Response<Unit> {
        categories.add(category)
        return Response.Success(Unit)
    }

    override suspend fun updateCategory(category: Category): Response<Unit> {
        val index = categories.indexOfFirst { it.id == category.id }
        if(index == -1) return Response.Error("NIE MA KURWA TAKIEJ KATEGORII")
        categories[index] = category
        return Response.Success(Unit)
    }

    override suspend fun deleteCategory(categoryId: Int): Response<Unit> {
        val categoryToRemove = categories.find { it.id == categoryId }
        if(categoryToRemove == null) return Response.Error("NIE MA KURWA TAKIEJ KATEGORII")
        categories.remove(categoryToRemove)
        return Response.Success(Unit)
    }

    override fun getAllQuestions(): Flow<Response<List<Question>>> = flow {
        emit(Response.Success(questions))
    }

    override fun getQuestionById(questionId: Int): Flow<Response<Question>> = flow {
        val question = questions.find { it.id == questionId }
        if(question == null) emit(Response.Error("NIE MA KURWA TAKIEGO PYTANIA"))
        else emit(Response.Success(question))
    }

    override fun updateQuestion(question: Question) {
        val index = questions.indexOfFirst { it.id == question.id }
        questions[index] = question
    }

    override suspend fun saveQuestion(question: Question): Response<Int> {
        questions.add(question)
        return Response.Success(question.id)
    }

    override suspend fun deleteQuestion(questionId: Int): Response<Unit> {
        val questionToRemove = questions.find { it.id == questionId }
        if(questionToRemove == null) return Response.Error("NIE MA KURWA TAKIEGO PYTANIA")
        questions.remove(questionToRemove)
        return Response.Success(Unit)
    }

}