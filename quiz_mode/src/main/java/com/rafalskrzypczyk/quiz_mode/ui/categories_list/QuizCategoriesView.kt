package com.rafalskrzypczyk.quiz_mode.ui.categories_list

import androidx.lifecycle.LiveData
import com.rafalskrzypczyk.quiz_mode.domain.models.Category

interface QuizCategoriesView {
    fun displayCategories(categories: LiveData<List<Category>>)
}