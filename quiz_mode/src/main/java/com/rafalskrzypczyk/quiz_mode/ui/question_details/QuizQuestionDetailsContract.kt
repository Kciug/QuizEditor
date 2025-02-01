package com.rafalskrzypczyk.quiz_mode.ui.question_details

import android.os.Bundle
import com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.quiz_mode.ui.view_models.AnswerUIModel

interface QuizQuestionDetailsContract {
    interface View {
        fun displayQuestionText(questionText: String)
        fun displayAnswersList(answers: List<AnswerUIModel>, answersCount: Int, correctAnswersCount: Int)
        fun displayLinkedCategories(categories: List<SimpleCategoryUIModel>)
        fun displayCreatedOn(date: String, user: String)
        fun setupView()
        fun setupNewElementView()
    }
    interface Presenter {
        fun getData(bundle: Bundle?)
        fun saveNewQuestion(questionText: String)
        fun updateQuestionText(questionText: String)
        fun addAnswer(answer: AnswerUIModel)
        fun updateAnswer(answer: AnswerUIModel)
        fun removeAnswer(answer: AnswerUIModel)
    }
}