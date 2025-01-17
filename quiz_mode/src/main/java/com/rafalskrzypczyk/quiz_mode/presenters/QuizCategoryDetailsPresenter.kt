package com.rafalskrzypczyk.quiz_mode.presenters

import com.rafalskrzypczyk.quiz_mode.TestCategories
import com.rafalskrzypczyk.quiz_mode.TestQuestions
import com.rafalskrzypczyk.quiz_mode.ui.QuizCategoryDetailsView

class QuizCategoryDetailsPresenter(
    private val view: QuizCategoryDetailsView
) {
    fun loadCategoryById(categoryId: Int) {
        val category = TestCategories.categories.find {
            it.id == categoryId
        }
        if(category == null) return

        view.displayCategoryDetails(category, TestQuestions.questions)
    }
}