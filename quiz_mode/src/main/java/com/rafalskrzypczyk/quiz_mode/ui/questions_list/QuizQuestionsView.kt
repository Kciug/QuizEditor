package com.rafalskrzypczyk.quiz_mode.ui.questions_list

import com.rafalskrzypczyk.quiz_mode.domain.models.Question

interface QuizQuestionsView {
    fun displayAllQuestions(questions: List<Question>)
}