package com.rafalskrzypczyk.cem_mode.presentation.question_details

import android.os.Bundle
import com.rafalskrzypczyk.cem_mode.domain.CemModeRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemAnswer
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.cem_mode.domain.models.CemQuestion
import com.rafalskrzypczyk.cem_mode.presentation.question_details.ui_models.toUIModel
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.core.presentation.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.core.utils.KeyboardController
import com.rafalskrzypczyk.core.utils.ResourceProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class CemQuestionDetailsPresenter @Inject constructor(
    private val repository: CemModeRepository,
    private val resourceProvider: ResourceProvider
) : BasePresenter<CemQuestionDetailsContract.View>(), CemQuestionDetailsContract.Presenter {

    private var currentQuestion: CemQuestion? = null
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
                repository.getQuestionById(questionId),
                repository.getCategories().filter { it is Response.Success }
            ) { questionResponse, categoriesResponse ->
                if (questionResponse is Response.Success && categoriesResponse is Response.Success) {
                    allCategories = categoriesResponse.data
                    currentQuestion = questionResponse.data
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
            repository.getUpdatedQuestionById(questionId).collectLatest { question ->
                question?.let {
                    currentQuestion = it
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
            displayQuestionDetails(question.text)
            displayCreatedDetails(String.formatDate(question.creationDate))
            displayAnswersCount(question.answers.size, question.answers.count { it.isCorrect })
            displayAnswers(question.answers.map { it.toUIModel() })
            displayLinkedCategories(linkedCategories)
            displayContent()
        }
    }

    override fun createNewQuestion(text: String) {
        if (text.isEmpty()) {
            view.displayToastMessage("Question text cannot be empty")
            return
        }
        val newQuestion = CemQuestion.new(text)
        presenterScope?.launch {
            val result = repository.addQuestion(newQuestion)
            if (result is Response.Success) {
                if (parentCategoryID != -1L) {
                    repository.bindQuestionWithCategory(newQuestion.id, parentCategoryID)
                }
                isDataLoaded = true
                currentQuestion = newQuestion
                updateUI(newQuestion)
                attachChangeListener(newQuestion.id)
            } else if (result is Response.Error) {
                view.displayError(result.error)
            }
        }
    }

    override fun updateQuestionText(text: String) {
        currentQuestion?.let {
            if (it.text != text) {
                it.text = text
                saveChanges()
            }
        }
    }

    override fun onAssignCategory() {
        view.displayCategoriesPicker()
    }

    override fun addAnswer(text: String) {
        currentQuestion?.let {
            it.answers.add(CemAnswer.new(text, false))
            updateUI(it)
            saveChanges()
        }
    }

    override fun updateAnswerText(answerId: Long, text: String) {
        currentQuestion?.let { q ->
            val answer = q.answers.find { it.id == answerId }
            if (answer != null && answer.text != text) {
                answer.text = text
                saveChanges()
            }
        }
    }

    override fun updateAnswerCorrectness(answerId: Long, isCorrect: Boolean) {
        currentQuestion?.let { q ->
            val answer = q.answers.find { it.id == answerId }
            if (answer != null && answer.isCorrect != isCorrect) {
                answer.isCorrect = isCorrect
                updateUI(q)
                saveChanges()
            }
        }
    }

    override fun deleteAnswer(answerId: Long) {
        currentQuestion?.let { q ->
            q.answers.removeAll { it.id == answerId }
            updateUI(q)
            saveChanges()
        }
    }

    private fun saveChanges() {
        currentQuestion?.let {
            presenterScope?.launch {
                repository.updateQuestion(it)
            }
        }
    }
}
