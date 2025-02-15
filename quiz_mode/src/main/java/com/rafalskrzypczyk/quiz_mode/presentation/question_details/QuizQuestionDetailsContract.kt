package com.rafalskrzypczyk.quiz_mode.presentation.question_details

import android.os.Bundle
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.AnswerUIModel

interface QuizQuestionDetailsContract {
    interface View {
        fun displayQuestionText(questionText: String)
        fun displayAnswersDetails(answersCount: Int, correctAnswersCount: Int)
        fun displayAnswersList(answers: List<AnswerUIModel>)
        fun addNewAnswer(answer: AnswerUIModel)
        fun removeAnswer(answerPosition: Int)
        fun displayLinkedCategories(categories: List<SimpleCategoryUIModel>)
        fun displayCreatedOn(date: String, user: String)
        fun setupView()
        fun setupNewElementView()
    }
    interface Presenter {
        fun getData(bundle: Bundle?)
        fun saveNewQuestion(questionText: String)
        fun updateQuestionText(questionText: String)
        fun onQuestionTextSubmitted(questionText: String)
        fun addAnswer(answerText: String)
        fun updateAnswer(answer: AnswerUIModel)
        fun removeAnswer(answer: AnswerUIModel, answerPosition: Int)
        fun updateLinkedCategories()
        fun onViewClosed()
    }
}