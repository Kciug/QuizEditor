package com.rafalskrzypczyk.swipe_mode.domain

import com.rafalskrzypczyk.core.api_result.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SwipeQuestionDetailsInteractor @Inject constructor(
    private val repository: SwipeModeRepository,
    private val dataUpdateManager: SwipeDataUpdateManager
) {
    private var questionInitialState: SwipeQuestion? = null
    private var questionReference: SwipeQuestion? = null

    fun getQuestion(questionId: Long): Flow<Response<SwipeQuestion>> =
        repository.getQuestionById(questionId).map {
            if(it is Response.Success) {
                questionReference = it.data
                updateInitialState(it.data)
                Response.Success(it.data)
            } else it
        }

    private fun updateInitialState(question: SwipeQuestion) {
        questionInitialState = question.copy()
    }

    suspend fun instantiateNewQuestion(questionText: String, isCorrect: Boolean, explanation: String): Response<SwipeQuestion> {
        val newQuestion = SwipeQuestion.new(questionText, isCorrect)
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

    fun updateIsCorrect(isCorrect: Boolean) {
        questionReference?.isCorrect = isCorrect
    }

    fun saveCachedQuestion() {
        questionReference?.let {
            if(it == questionInitialState) return
            dataUpdateManager.updateQuestion(it)
        }
    }

    fun clearReference() {
        questionReference = null
        questionInitialState = null
    }
}