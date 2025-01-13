package com.rafalskrzypczyk.quiz_mode.ui

import com.rafalskrzypczyk.quiz_mode.models.Category

interface QuizCategoryDetailsView {
    fun displayCategoryDetails(category: Category)
}