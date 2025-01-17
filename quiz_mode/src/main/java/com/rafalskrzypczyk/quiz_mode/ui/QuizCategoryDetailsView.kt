package com.rafalskrzypczyk.quiz_mode.ui

import com.rafalskrzypczyk.quiz_mode.models.Category
import com.rafalskrzypczyk.quiz_mode.models.Question

interface QuizCategoryDetailsView {
    fun displayCategoryDetails(category: Category, questions: List<Question>)
}