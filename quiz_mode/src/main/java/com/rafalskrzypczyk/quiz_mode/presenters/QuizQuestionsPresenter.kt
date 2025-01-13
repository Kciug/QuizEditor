package com.rafalskrzypczyk.quiz_mode.presenters

import com.rafalskrzypczyk.quiz_mode.TestQuestions
import com.rafalskrzypczyk.quiz_mode.ui.QuizQuestionsView

class QuizQuestionsPresenter(
    private val view: QuizQuestionsView
) {
    fun loadAllQuestions(){
        view.displayAllQuestions(TestQuestions.questions)
    }
}