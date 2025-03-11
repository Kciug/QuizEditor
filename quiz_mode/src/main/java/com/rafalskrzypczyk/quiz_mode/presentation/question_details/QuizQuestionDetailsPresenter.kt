package com.rafalskrzypczyk.quiz_mode.presentation.question_details

import android.os.Bundle
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.di.MainDispatcher
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.quiz_mode.R
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
    private val resourceProvider: ResourceProvider,
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
            bundle?.getLong("parentCategoryId")?.let { interactor.setParentCategoryId(it) }
            return
        }

        presenterScope.launch{
            interactor.getQuestion(questionId).collectLatest { handleQuestionResponse(it) }
        }
    }

    private fun attachChangeListener() {
        presenterScope.launch {
            interactor.getUpdatedQuestion().collectLatest { it?.let { updateUI(it) } }
        }
    }

    private fun handleQuestionResponse(response: Response<Question>) {
        when (response) {
            is Response.Success -> {
                isQuestionLoaded = true
                updateUI(response.data)
            }
            is Response.Error -> view.displayError(response.error)
            is Response.Loading -> view.displayLoading()
        }
    }

    private fun updateUI(question: Question) {
        if(!isQuestionLoaded) return

        with(view){
            setupView()
            displayQuestionText(question.text)
            displayAnswersDetails(question.answers.count(), question.answers.count { it.isCorrect })
            displayAnswersList(question.answers.map { it.toSimplePresentation() })
            displayCreatedOn(String.formatDate(question.creationDate), question.createdBy)
        }
        updateLinkedCategories()
    }

    override fun saveNewQuestion(questionText: String) {
        presenterScope.launch {
            handleQuestionResponse(interactor.instantiateNewQuestion(questionText))
        }
    }

    override fun updateQuestionText(questionText: String) {
        if (questionText.isEmpty()) {
            view.displayToastMessage(resourceProvider.getString(R.string.warning_empty_question_text))
            return
        }
        if (isQuestionLoaded) interactor.updateQuestionText(questionText)
        else saveNewQuestion(questionText)
    }

    override fun addAnswer(answerText: String) {
        if (answerText.isEmpty()) {
            view.displayToastMessage(resourceProvider.getString(R.string.warning_empty_answer_text))
            return
        }
        interactor.addAnswer(answerText)
        displayAnswers()
    }

    override fun updateAnswer(answer: AnswerUIModel) {
        if (answer.answerText.isEmpty()) {
            view.displayToastMessage(resourceProvider.getString(R.string.warning_empty_answer_text))
            return
        }
        interactor.updateAnswer(answer.id, answer.answerText, answer.isCorrect)
        displayAnswers()
    }

    override fun removeAnswer(answer: AnswerUIModel) {
        interactor.removeAnswer(answer.id)
        displayAnswers()
    }

    private fun displayAnswers() {
        view.displayAnswersDetails(interactor.answerCount(), interactor.correctAnswerCount())
        view.displayAnswersList(interactor.getAnswers().map { it.toSimplePresentation() })
    }

    override fun updateLinkedCategories() {
        presenterScope.launch {
            interactor.getLinkedCategories().collectLatest {
                when (it) {
                    is Response.Success -> view.displayLinkedCategories(it.data.map { it.toSimplePresentation() })
                    is Response.Error -> view.displayError(it.error)
                    is Response.Loading -> view.displayLoading()
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