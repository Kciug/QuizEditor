package com.rafalskrzypczyk.quiz_mode.ui.questions_list

import com.rafalskrzypczyk.quiz_mode.TestQuestions

class QuizQuestionsPresenter(
    private val view: QuizQuestionsView
) {
    fun loadAllQuestions(){
        view.displayAllQuestions(TestQuestions.questions)
    }
}