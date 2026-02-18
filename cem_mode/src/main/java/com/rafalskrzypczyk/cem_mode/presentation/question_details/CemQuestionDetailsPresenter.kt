package com.rafalskrzypczyk.cem_mode.presentation.question_details

import android.os.Bundle
import com.rafalskrzypczyk.cem_mode.domain.CemModeRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemAnswer
import com.rafalskrzypczyk.cem_mode.domain.models.CemQuestion
import com.rafalskrzypczyk.cem_mode.presentation.question_details.ui_models.toUIModel
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.core.presentation.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.core.utils.ResourceProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class CemQuestionDetailsPresenter @Inject constructor(
    private val repository: CemModeRepository,
    private val resourceProvider: ResourceProvider
) : BasePresenter<CemQuestionDetailsContract.View>(), CemQuestionDetailsContract.Presenter {

    private var currentQuestion: CemQuestion? = null
    private var isDataLoaded = false
    private var parentCategoryID: Long = -1L

    override fun getData(bundle: Bundle?) {
        val questionId = bundle?.getLong("questionId", -1L) ?: -1L
        parentCategoryID = if (bundle?.containsKey("parentCategoryID") == true) {
            bundle.getLong("parentCategoryID")
        } else {
            -1L
        }

        if (questionId == -1L) {
            view.setupNewElementView()
            return
        }

        presenterScope?.launch {
            repository.getQuestionById(questionId).collectLatest { response ->
                when (response) {
                    is Response.Success -> {
                        isDataLoaded = true
                        currentQuestion = response.data
                        updateUI(response.data)
                    }
                    is Response.Error -> view.displayError(response.error)
                    is Response.Loading -> view.displayLoading()
                }
            }
        }
    }

    private fun updateUI(question: CemQuestion) {
        presenterScope?.launch {
            val categoriesResult = repository.getCategories().filter { it is Response.Success }.first()
            val linkedCategories = if (categoriesResult is Response.Success) {
                categoriesResult.data.filter { it.id in question.linkedCategories }
                    .map { SimpleCategoryUIModel(it.title, it.color.toLong()) }
            } else emptyList()

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
                currentQuestion = newQuestion
                isDataLoaded = true
                updateUI(newQuestion)
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
