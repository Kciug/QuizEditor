package com.rafalskrzypczyk.quiz_mode.ui.question_details

import android.os.Bundle
import android.util.Log
import com.rafalskrzypczyk.core.base.NamedEntity
import com.rafalskrzypczyk.core.utils.formatDate
import com.rafalskrzypczyk.quiz_mode.TestCategories
import com.rafalskrzypczyk.quiz_mode.TestQuestions
import com.rafalskrzypczyk.quiz_mode.domain.models.Answer
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.ui.editable_picker.Checkable
import com.rafalskrzypczyk.quiz_mode.ui.editable_picker.EditablePickerContract
import com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models.AnswerUIModel
import com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models.toDomain
import com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models.toPresentation

class QuizQuestionDetailsPresenter(
    private val view: QuizQuestionDetailsContract.View
) : QuizQuestionDetailsContract.Presenter, EditablePickerContract.Presenter {
    private var parentCategoryId: Int? = null
    private lateinit var question: Question
    private lateinit var categories: List<Category>
    private lateinit var pickerView: EditablePickerContract.View

    init {
        categories = TestCategories.cat
    }

    override fun getData(bundle: Bundle?) {
        val questionId = bundle?.getInt("questionId")

        if (questionId == null) {
            view.setupNewElementView()
            return
        }

        question = TestQuestions.questions.find { it.id == questionId }!!

        view.setupView()
        view.displayQuestionText(question.text)
        view.displayAnswersDetails(question.answers.count(), question.answers.count { it.isCorrect })
        view.displayAnswersList(question.answers.map { it.toPresentation() })
        view.displayCreatedOn(String.formatDate(question.creationDate), question.createdBy)
        updateLinkedCategories()
    }

    override fun saveNewQuestion(questionText: String) {
        if(questionText.isEmpty()) return

        question = Question.new(questionText)

        TestQuestions.questions.add(question)
        view.setupView()
        view.displayAnswersDetails(question.answers.count(), question.answers.count { it.isCorrect })
        view.displayCreatedOn(String.formatDate(question.creationDate), question.createdBy)
    }

    override fun updateQuestionText(questionText: String) {
        question.text = questionText
    }

    override fun addAnswer(answerText: String) {
        if(answerText.isEmpty()) return
        val newAnswer = Answer.new(answerText)
        question.answers.add(newAnswer)
        view.addNewAnswer(newAnswer.toPresentation())
        view.displayAnswersDetails(question.answers.count(), question.answers.count { it.isCorrect })
    }

    override fun updateAnswer(answer: AnswerUIModel) {
        val answerIndex = question.answers.indexOfFirst { it.id == answer.id }
        if(answerIndex != -1) question.answers[answerIndex] = answer.toDomain()
        view.displayAnswersDetails(question.answers.count(), question.answers.count { it.isCorrect })
    }

    override fun removeAnswer(answer: AnswerUIModel, answerPosition: Int) {
        question.answers.remove(answer.toDomain())
        view.displayAnswersDetails(question.answers.count(), question.answers.count { it.isCorrect })
        view.removeAnswer(answerPosition)
    }

    override fun updateLinkedCategories() {
        val categoriesToDisplay = categories.filter { question.linkedCategories.contains(it.id) }
        view.displayLinkedCategories(categoriesToDisplay.map { it.toPresentation() })
    }

    override fun setPickerView(view: EditablePickerContract.View) {
        pickerView = view
    }

    override fun getItemList() {
        pickerView.displayData(categories.map { Checkable(
            id = it.id,
            title = it.title,
            isChecked = if(question.linkedCategories.contains(it.id)) true else false,
            isLocked = if(parentCategoryId == null) false else parentCategoryId == it.id
        )
        })
    }

    override fun onItemSelected(selectedItem: Checkable) {
        question.linkedCategories.add(categories.first { it.id == selectedItem.id }.id)
    }

    override fun onItemDeselected(deselectedItem: Checkable) {
        question.linkedCategories.remove(categories.first { it.id == deselectedItem.id }.id)
    }

    override fun onSearchQueryChanged(query: String) {
        if(query.isEmpty()) {
            getItemList()
            return
        }

        val filteredCategories = categories.filter { it.title.contains(query) }
        pickerView.displayData(filteredCategories.map { Checkable(
            id = it.id,
            title = it.title,
            isChecked = if(question.linkedCategories.contains(it.id)) true else false,
            isLocked = if(parentCategoryId == null) false else parentCategoryId == it.id
        )
        })
    }
}