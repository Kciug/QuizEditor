package com.rafalskrzypczyk.cem_mode.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.cem_mode.domain.models.CemAnswer
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.cem_mode.domain.models.CemQuestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CemQuestionDetailsInteractor @Inject constructor(
    private val repository: CemModeRepository,
    private val dataUpdateManager: CemDataUpdateManager
) {
    private var questionInitialState: CemQuestion? = null
    private var questionReference: CemQuestion? = null
    private var answersInitialState: List<CemAnswer> = emptyList()

    fun getQuestion(questionId: Long): Flow<Response<CemQuestion>> =
        repository.getQuestionById(questionId).map {
            if(it is Response.Success) {
                questionReference = it.data
                updateInitialState(it.data)
                Response.Success(it.data)
            } else it
        }

    fun getCategories(): Flow<Response<List<CemCategory>>> = repository.getCategories()

    fun getUpdatedQuestion(questionId: Long): Flow<CemQuestion?> =
        repository.getUpdatedQuestionById(questionId).map { question ->
            question?.also {
                questionReference = it
                updateInitialState(it)
            }
        }

    private fun updateInitialState(question: CemQuestion) {
        questionInitialState = question.copy()
        answersInitialState = question.answers.map { it.copy() }
    }

    suspend fun instantiateNewQuestion(text: String, explanation: String): Response<CemQuestion> {
        val newQuestion = CemQuestion.new(text)
        newQuestion.explanation = explanation
        return when (val response = repository.addQuestion(newQuestion)) {
            is Response.Success -> {
                questionReference = newQuestion
                updateInitialState(newQuestion)
                Response.Success(newQuestion)
            }
            is Response.Error -> response
            is Response.Loading -> response
        }
    }

    fun updateQuestionText(text: String) {
        questionReference?.text = text
    }

    fun updateExplanation(explanation: String) {
        questionReference?.explanation = explanation
    }

    fun addAnswer(text: String) {
        questionReference?.answers?.add(CemAnswer.new(text, false))
    }

    fun updateAnswerText(answerId: Long, text: String) {
        questionReference?.answers?.find { it.id == answerId }?.text = text
    }

    fun updateAnswerCorrectness(answerId: Long, isCorrect: Boolean) {
        questionReference?.answers?.find { it.id == answerId }?.isCorrect = isCorrect
    }

    fun deleteAnswer(answerId: Long) {
        questionReference?.answers?.removeAll { it.id == answerId }
    }

    fun bindWithCategory(categoryId: Long) {
        questionReference?.id?.let {
            dataUpdateManager.bindQuestionWithCategory(it, categoryId)
        }
    }

    fun saveCachedQuestion() {
        questionReference?.let {
            if (it == questionInitialState && answersEqual(it.answers, answersInitialState)) return
            dataUpdateManager.updateQuestion(it)
        }
    }

    private fun answersEqual(currentAnswers: List<CemAnswer>, initialAnswers: List<CemAnswer>): Boolean {
        if (currentAnswers.size != initialAnswers.size) return false
        return currentAnswers.zip(initialAnswers).all { (current, initial) ->
            current == initial
        }
    }
}