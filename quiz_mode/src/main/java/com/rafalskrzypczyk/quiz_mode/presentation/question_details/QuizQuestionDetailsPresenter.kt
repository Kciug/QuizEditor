package com.rafalskrzypczyk.quiz_mode.presentation.question_details

import android.os.Bundle
import android.util.Log
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.quiz_mode.domain.QuizQuestionDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.AnswerUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.toDomain
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.toSimplePresentation
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map

class QuizQuestionDetailsPresenter @Inject constructor(
    private val view: QuizQuestionDetailsContract.View,
    private val interactor: QuizQuestionDetailsInteractor
) : BasePresenter(), QuizQuestionDetailsContract.Presenter {
    private var isQuestionLoaded = false

    override fun getData(bundle: Bundle?) {
        val questionId = bundle?.getInt("questionId")
        if (questionId == null || questionId == 0) {
            view.setupNewElementView()
            val parentCategoryId = bundle?.getInt("parentCategoryId")
            if(parentCategoryId != null) interactor.setParentCategoryId(parentCategoryId)
            return
        }

        presenterScope.launch {
            presentQuestion(interactor.getQuestion(questionId))
        }
    }

    private fun presentQuestion(response: Response<Question>) {
        when (response) {
            is Response.Success -> {
                isQuestionLoaded = true
                updateUI(response.data)
            }

            is Response.Error -> {
                Log.e("QuizQuestionDetailsPresenter", "Error: ${response.error}")
            }

            is Response.Loading -> {
                Log.d("QuizQuestionDetailsPresenter", "Loading")
            }
        }
    }

    private fun updateUI(question: Question) {
        view.setupView()
        view.displayQuestionText(question.text)
        view.displayAnswersDetails(
            question.answers.count(),
            question.answers.count { it.isCorrect })
        view.displayAnswersList(question.answers.map { it.toSimplePresentation() })
        view.displayCreatedOn(String.formatDate(question.creationDate), question.createdBy)
        updateLinkedCategories()
    }

    override fun saveNewQuestion(questionText: String) {
        presenterScope.launch {
            presentQuestion(interactor.instantiateNewQuestion(questionText))
        }
    }

    override fun updateQuestionText(questionText: String) = interactor.updateQuestionText(questionText)

    override fun onQuestionTextSubmitted(questionText: String) {
        if (questionText.isEmpty()) return
        if (isQuestionLoaded) interactor.updateQuestionText(questionText)
        else saveNewQuestion(questionText)
    }

    override fun addAnswer(answerText: String) {
        if (answerText.isEmpty()) return
        interactor.addAnswer(answerText)
        view.addNewAnswer(interactor.getLastAnswer().toSimplePresentation())
        view.displayAnswersDetails(interactor.answerCount(), interactor.correctAnswerCount())
    }

    override fun updateAnswer(answer: AnswerUIModel) {
        if(answer.answerText.isEmpty()) return
        interactor.updateAnswer(answer.toDomain())
        view.displayAnswersDetails(interactor.answerCount(), interactor.correctAnswerCount())
    }

    override fun removeAnswer(answer: AnswerUIModel, answerPosition: Int) {
        interactor.removeAnswer(answer.toDomain())
        view.displayAnswersDetails(interactor.answerCount(), interactor.correctAnswerCount())
        view.removeAnswer(answerPosition)
    }

    override fun updateLinkedCategories() {
        presenterScope.launch {
            view.displayLinkedCategories(interactor.getLinkedCategories().map { it.toSimplePresentation() })
        }
    }

    override fun saveUpdatedData() {
        presenterScope.launch {
            interactor.saveCachedQuestion()
        }
    }
}