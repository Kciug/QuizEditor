package com.rafalskrzypczyk.quiz_mode.presenters

import androidx.lifecycle.MutableLiveData
import com.rafalskrzypczyk.quiz_mode.TestCategories
import com.rafalskrzypczyk.quiz_mode.models.Category
import com.rafalskrzypczyk.quiz_mode.ui.QuizCategoriesView

class QuizCategoriesPresenter(
    private val view: QuizCategoriesView
) {
    private val _categoriesLiveData = MutableLiveData<List<Category>>()
    val categories = _categoriesLiveData

    fun loadCategories(){
        _categoriesLiveData.value = TestCategories.cat

        view.displayCategories(categories)
    }
}