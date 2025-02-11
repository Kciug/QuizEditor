package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import androidx.lifecycle.MutableLiveData
import com.rafalskrzypczyk.quiz_mode.TestCategories
import com.rafalskrzypczyk.quiz_mode.domain.models.Category

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