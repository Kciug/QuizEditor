package com.rafalskrzypczyk.quiz_mode.presentation.question_details

import android.os.Bundle
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.di.MainDispatcher
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.quiz_mode.domain.QuizQuestionDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.AnswerUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.toSimplePresentation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map

class QuizQuestionDetailsPresenter @Inject constructor(
    private val interactor: QuizQuestionDetailsInteractor,
    @MainDispatcher dispatcher: CoroutineDispatcher
) : BasePresenter<QuizQuestionDetailsContract.View>(), QuizQuestionDetailsContract.Presenter {
    private var presenterScope = CoroutineScope(SupervisorJob() + dispatcher)
    private var isQuestionLoaded = false

    override fun onViewCreated() {
        super.onViewCreated()
        attachChangeListener()
    }

    override fun getData(bundle: Bundle?) {
        val questionId = bundle?.getLong("questionId")
        if (questionId == null || questionId == 0.toLong()) {
            view.setupNewElementView()
            val parentCategoryId = bundle?.getLong("parentCategoryId")
            if(parentCategoryId != null) interactor.setParentCategoryId(parentCategoryId)
            return
        }

        presenterScope.launch{
            interactor.getQuestion(questionId).collectLatest {
                when (it) {
                    is Response.Success -> { displayQuestion(it) }
                    else -> it
                }
            }
        }
    }

    private fun attachChangeListener() {
        presenterScope.launch {
            interactor.getUpdatedQuestion().collectLatest { it?.let { updateUI(it) } }
        }
    }

    private fun displayQuestion(response: Response<Question>) {
        when (response) {
            is Response.Success -> {
                isQuestionLoaded = true
                updateUI(response.data)
            }
            is Response.Error -> view.showError(response.error)
            is Response.Loading -> view.showLoading()
        }
    }

    private fun updateUI(question: Question) {
        if(!isQuestionLoaded) return

        view.setupView()
        view.displayQuestionText(question.text)
        view.displayAnswersDetails(
            question.answers.count(),
            question.answers.count { it.isCorrect })
        displayAnswersList()
        view.displayCreatedOn(String.formatDate(question.creationDate), question.createdBy)
        updateLinkedCategories()
    }

    override fun saveNewQuestion(questionText: String) {
        presenterScope.launch {
            displayQuestion(interactor.instantiateNewQuestion(questionText))
            interactor.bindWithParentCategory()
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
        view.displayAnswersDetails(interactor.answerCount(), interactor.correctAnswerCount())
        displayAnswersList()
    }

    override fun updateAnswer(answer: AnswerUIModel) {
        if(answer.answerText.isEmpty()) return
        interactor.updateAnswer(answer.id, answer.answerText, answer.isCorrect)
        view.displayAnswersDetails(interactor.answerCount(), interactor.correctAnswerCount())
        displayAnswersList()
    }

    override fun removeAnswer(answer: AnswerUIModel) {
        interactor.removeAnswer(answer.id)
        view.displayAnswersDetails(interactor.answerCount(), interactor.correctAnswerCount())
        displayAnswersList()
    }

    private fun displayAnswersList() {
        view.displayAnswersList(interactor.getAnswers().map { it.toSimplePresentation() })
    }

    override fun updateLinkedCategories() {
        presenterScope.launch {
            interactor.getLinkedCategories().collectLatest {
                when (it) {
                    is Response.Success -> view.displayLinkedCategories(it.data.map { it.toSimplePresentation() })
                    is Response.Error -> view.showError(it.error)
                    is Response.Loading -> view.showLoading()
                }
            }
        }
    }

    override fun onAssignCategory() {
        view.displayCategoryPicker()
    }

    override fun onDestroy() {
        interactor.saveCachedQuestion()
        presenterScope.cancel()
        super.onDestroy()
    }
}