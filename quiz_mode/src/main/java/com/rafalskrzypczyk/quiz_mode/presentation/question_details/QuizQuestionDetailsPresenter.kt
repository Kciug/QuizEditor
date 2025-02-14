package com.rafalskrzypczyk.quiz_mode.presentation.question_details

import android.os.Bundle
import android.util.Log
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.utils.formatDate
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.domain.models.Answer
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.presentation.editable_picker.Checkable
import com.rafalskrzypczyk.quiz_mode.presentation.editable_picker.EditablePickerContract
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.AnswerUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.toDomain
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.toPresentation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map

class QuizQuestionDetailsPresenter @Inject constructor(
    private val view: QuizQuestionDetailsContract.View,
    private val repository: QuizModeRepository,
) : BasePresenter(), QuizQuestionDetailsContract.Presenter, EditablePickerContract.Presenter {
    private var cachedQuestion: Question? = null
    private var parentCategoryId: Int? = null
    private lateinit var pickerView: EditablePickerContract.View

    private val searchQuery = MutableStateFlow("")

    override fun getData(bundle: Bundle?) {
        val questionId = bundle?.getInt("questionId")
        if (questionId == null) {
            view.setupNewElementView()
            parentCategoryId = bundle?.getInt("parentCategoryId")
            return
        }

        presenterScope.launch { loadQuestion(questionId) }
    }

    private suspend fun loadQuestion(questionId: Int) {
        repository.getQuestionById(questionId)
            .collectLatest {
                when (it) {
                    is Response.Success -> {
                        cachedQuestion = it.data
                        updateUI(it.data)
                    }

                    is Response.Error -> {
                        Log.e("QuizQuestionDetailsPresenter", "Error: ${it.error}")
                    }

                    is Response.Loading -> {
                        Log.d("QuizQuestionDetailsPresenter", "Loading")
                    }
                }
            }
    }

    private fun updateUI(question: Question) {
        view.setupView()
        view.displayQuestionText(question.text)
        view.displayAnswersDetails(
            question.answers.count(),
            question.answers.count { it.isCorrect })
        view.displayAnswersList(question.answers.map { it.toPresentation() })
        view.displayCreatedOn(String.formatDate(question.creationDate), question.createdBy)
        updateLinkedCategories()
    }

    override fun saveNewQuestion(questionText: String) {
        if (questionText.isEmpty()) return
        presenterScope.launch {
            val response = repository.saveQuestion(questionText)
            when (response) {
                is Response.Success -> {
                    loadQuestion(response.data)
                }

                is Response.Error -> {
                    Log.e("QuizQuestionDetailsPresenter", "Error: ${response.error}")
                }

                is Response.Loading -> {
                    Log.d("QuizQuestionDetailsPresenter", "Loading")
                }
            }
        }
    }

    override fun updateQuestionText(questionText: String) {
        if (questionText.isEmpty()) return
        if(cachedQuestion == null) {
            saveNewQuestion(questionText)
            return
        }
        cachedQuestion!!.text = questionText
    }

    override fun addAnswer(answerText: String) {
        if (answerText.isEmpty()) return
        cachedQuestion?.let {
            it.answers.add(Answer.new(answerText))
            view.addNewAnswer(it.answers.last().toPresentation())
            view.displayAnswersDetails(it.answers.count(), it.answers.count { it.isCorrect })
        }
    }

    override fun updateAnswer(answer: AnswerUIModel) {
        cachedQuestion?.let {
            val answers = it.answers
            val answerIndex = answers.indexOfFirst { it.id == answer.id }
            if (answerIndex != -1) answers[answerIndex] = answer.toDomain()
            view.displayAnswersDetails(answers.count(), answers.count { it.isCorrect })
        }
    }

    override fun removeAnswer(answer: AnswerUIModel, answerPosition: Int) {
        cachedQuestion?.let {
            it.answers.remove(answer.toDomain())
            view.displayAnswersDetails(it.answers.count(), it.answers.count { it.isCorrect })
            view.removeAnswer(answerPosition)
        }
    }

    override fun updateLinkedCategories() {
        presenterScope.launch {
            repository.getAllCategories()
                .filter { it is Response.Success }
                .map { (it as Response.Success).data }
                .collectLatest { categories ->
                    val selectedCategories =
                        categories.filter { cachedQuestion?.linkedCategories?.contains(it.id) == false }
                    view.displayLinkedCategories(selectedCategories.map { it.toPresentation() })
                }
        }
    }

    override fun onViewClosed() {
        presenterScope.launch{
            cachedQuestion?.let { repository.updateQuestion(it) }
        }
    }

    // Picker Presenter
    override fun setPickerView(view: EditablePickerContract.View) {
        pickerView = view
    }

    override fun getItemList() {
        presenterScope.launch {
            combine(
                repository.getAllCategories()
                    .filter { it is Response.Success }
                    .map { (it as Response.Success).data },
                searchQuery
            ) { categories, query ->
                categories.filter { it.title.contains(query, ignoreCase = true) }
            }.collectLatest { filteredCategories ->
                pickerView.displayData(filteredCategories.map {
                    Checkable(
                        id = it.id,
                        title = it.title,
                        isChecked = cachedQuestion?.linkedCategories?.contains(it.id) == false,
                        isLocked = parentCategoryId == it.id
                    )
                })
            }
        }
    }

    override fun onItemSelected(selectedItem: Checkable) {
        cachedQuestion?.linkedCategories?.add(selectedItem.id)
    }

    override fun onItemDeselected(deselectedItem: Checkable) {
        cachedQuestion?.linkedCategories?.remove(deselectedItem.id)
    }

    override fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}