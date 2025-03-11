package com.rafalskrzypczyk.quiz_mode.presentation.question_details

import android.os.Bundle
import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.AnswerUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.SimpleCategoryUIModel

interface QuizQuestionDetailsContract {
    interface View : BaseContract.View {
        fun displayQuestionText(questionText: String)
        fun displayAnswersDetails(answersCount: Int, correctAnswersCount: Int)
        fun displayAnswersList(answers: List<AnswerUIModel>)
        fun updateInputOnAnswerAdded()
        fun displayLinkedCategories(categories: List<SimpleCategoryUIModel>)
        fun displayCreatedOn(date: String, user: String)
        fun setupView()
        fun setupNewElementView()
        fun displayCategoryPicker()
        fun displayCategoriesListLoading()
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun getData(bundle: Bundle?)
        fun saveNewQuestion(questionText: String)
        fun updateQuestionText(questionText: String)
        fun onQuestionTextSubmitted(questionText: String)
        fun addAnswer(answerText: String)
        fun updateAnswer(answer: AnswerUIModel)
        fun removeAnswer(answer: AnswerUIModel)
        fun updateLinkedCategories()
        fun onAssignCategory()
    }
}