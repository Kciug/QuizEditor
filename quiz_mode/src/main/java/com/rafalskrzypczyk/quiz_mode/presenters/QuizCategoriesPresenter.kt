package com.rafalskrzypczyk.quiz_mode.presenters

import com.rafalskrzypczyk.quiz_mode.TestCategories
import com.rafalskrzypczyk.quiz_mode.ui.QuizCategoriesView

class QuizCategoriesPresenter(
    private val view: QuizCategoriesView
) {
    fun loadCategories(){
        view.displayCategories(TestCategories.categories)
    }
}