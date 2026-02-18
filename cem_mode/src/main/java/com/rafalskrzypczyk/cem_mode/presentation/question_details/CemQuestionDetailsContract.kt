package com.rafalskrzypczyk.cem_mode.presentation.question_details

import android.os.Bundle
import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.presentation.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.cem_mode.presentation.question_details.ui_models.CemAnswerUIModel

interface CemQuestionDetailsContract {
    interface View : BaseContract.View {
        fun setupView()
        fun setupNewElementView()
        fun displayQuestionDetails(questionText: String, explanation: String)
        fun displayCreatedDetails(date: String)
        fun displayAnswersCount(total: Int, correct: Int)
        fun displayAnswers(answers: List<CemAnswerUIModel>)
        fun displayLinkedCategories(categories: List<SimpleCategoryUIModel>)
        fun displayCategoriesPicker()
        fun displayToastMessage(message: String)
        fun displayContent()
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun getData(bundle: Bundle?)
        fun createNewQuestion(text: String, explanation: String)
        fun updateQuestionText(text: String)
        fun updateExplanation(explanation: String)
        fun onAssignCategory()
        fun addAnswer(text: String)
        fun updateAnswerText(answerId: Long, text: String)
        fun updateAnswerCorrectness(answerId: Long, isCorrect: Boolean)
        fun deleteAnswer(answerId: Long)
    }
}
