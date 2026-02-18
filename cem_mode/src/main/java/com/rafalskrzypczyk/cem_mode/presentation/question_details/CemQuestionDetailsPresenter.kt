package com.rafalskrzypczyk.cem_mode.presentation.question_details

import android.os.Bundle
import com.rafalskrzypczyk.cem_mode.domain.CemQuestionDetailsInteractor
import com.rafalskrzypczyk.cem_mode.domain.models.CemAnswer
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.cem_mode.domain.models.CemQuestion
import com.rafalskrzypczyk.cem_mode.presentation.question_details.ui_models.toUIModel
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.core.presentation.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.core.utils.ResourceProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

class CemQuestionDetailsPresenter @Inject constructor(
    private val interactor: CemQuestionDetailsInteractor,
    private val resourceProvider: ResourceProvider
) : BasePresenter<CemQuestionDetailsContract.View>(), CemQuestionDetailsContract.Presenter {

    private var allCategories: List<CemCategory> = emptyList()
    private var isDataLoaded = false
    private var parentCategoryID: Long = -1L

    override fun getData(bundle: Bundle?) {
        val questionId = bundle?.getLong("questionId", -1L) ?: -1L
        parentCategoryID = bundle?.getLong("parentCategoryID", -1L) ?: -1L

        if (questionId == -1L) {
            view.setupNewElementView()
            return
        }

        presenterScope?.launch {
            combine(
                interactor.getQuestion(questionId),
                interactor.getCategories().filter { it is Response.Success }
            ) { questionResponse, categoriesResponse ->
                if (questionResponse is Response.Success && categoriesResponse is Response.Success) {
                    allCategories = categoriesResponse.data
                    updateUI(questionResponse.data)
                    
                    if (!isDataLoaded) {
                        isDataLoaded = true
                        attachChangeListener(questionId)
                    }
                }
            }.collectLatest { }
        }
    }

    private fun attachChangeListener(questionId: Long) {
        presenterScope?.launch {
            interactor.getUpdatedQuestion(questionId).collectLatest { question ->
                question?.let {
                    updateUI(it)
                }
            }
        }
    }

    private fun updateUI(question: CemQuestion) {
        val linkedCategories = allCategories.filter { it.id in question.linkedCategories }
            .map { SimpleCategoryUIModel(it.title, it.color.toLong()) }

        with(view) {
            setupView()
            displayQuestionDetails(question.text, question.explanation)
            displayCreatedDetails(String.formatDate(question.creationDate))
            displayAnswersCount(question.answers.size, question.answers.count { it.isCorrect })
            displayAnswers(question.answers.map { it.toUIModel() })
            displayLinkedCategories(linkedCategories)
            displayContent()
        }
    }

    override fun createNewQuestion(text: String, explanation: String) {
        if (text.isEmpty()) {
            view.displayToastMessage("Question text cannot be empty")
            return
        }
        presenterScope?.launch {
            val result = interactor.instantiateNewQuestion(text, explanation)
            if (result is Response.Success) {
                if (parentCategoryID != -1L) {
                    interactor.bindWithCategory(parentCategoryID)
                }
                isDataLoaded = true
                updateUI(result.data)
                attachChangeListener(result.data.id)
            } else if (result is Response.Error) {
                view.displayError(result.error)
            }
        }
    }

    override fun updateQuestionText(text: String) {
        if (isDataLoaded) {
            interactor.updateQuestionText(text)
        }
    }

    override fun updateExplanation(explanation: String) {
        if (isDataLoaded) {
            interactor.updateExplanation(explanation)
        }
    }

    override fun onAssignCategory() {
        view.displayCategoriesPicker()
    }

    override fun addAnswer(text: String) {
        if (isDataLoaded) {
            interactor.addAnswer(text)
            // Manual update since we don't have a flow for local changes yet
            // In quiz_mode they use a repository flow for this
        }
    }

    override fun updateAnswerText(answerId: Long, text: String) {
        if (isDataLoaded) {
            interactor.updateAnswerText(answerId, text)
        }
    }

    override fun updateAnswerCorrectness(answerId: Long, isCorrect: Boolean) {
        if (isDataLoaded) {
            interactor.updateAnswerCorrectness(answerId, isCorrect)
        }
    }

    override fun deleteAnswer(answerId: Long) {
        if (isDataLoaded) {
            interactor.deleteAnswer(answerId)
        }
    }

    override fun onDestroy() {
        interactor.saveCachedQuestion()
        super.onDestroy()
    }
}
