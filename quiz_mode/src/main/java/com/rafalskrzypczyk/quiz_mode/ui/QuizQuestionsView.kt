package com.rafalskrzypczyk.quiz_mode.ui

import com.rafalskrzypczyk.quiz_mode.models.Question

interface QuizQuestionsView {
    fun displayAllQuestions(questions: List<Question>)
}