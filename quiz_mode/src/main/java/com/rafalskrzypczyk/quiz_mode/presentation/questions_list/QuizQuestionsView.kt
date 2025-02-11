package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import com.rafalskrzypczyk.quiz_mode.domain.models.Question

interface QuizQuestionsContract{
    interface View {
        fun displayAllQuestions(questions: List<Question>)
    }
    interface Presenter {
        fun loadAllQuestions()
    }
}