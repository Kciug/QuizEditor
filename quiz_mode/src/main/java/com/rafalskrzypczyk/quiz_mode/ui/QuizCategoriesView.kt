package com.rafalskrzypczyk.quiz_mode.ui

import com.rafalskrzypczyk.quiz_mode.models.Category

interface QuizCategoriesView {
    fun displayCategories(categories: List<Category>)
}