package com.rafalskrzypczyk.quiz_mode.ui

import androidx.lifecycle.LiveData
import com.rafalskrzypczyk.quiz_mode.models.Category

interface QuizCategoriesView {
    fun displayCategories(categories: LiveData<List<Category>>)
}