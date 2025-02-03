package com.rafalskrzypczyk.quiz_mode.ui.categeory_details

import android.icu.util.Calendar
import androidx.lifecycle.MutableLiveData
import com.rafalskrzypczyk.quiz_mode.utils.CategoryStatus
import com.rafalskrzypczyk.quiz_mode.TestCategories
import com.rafalskrzypczyk.quiz_mode.TestQuestions
import com.rafalskrzypczyk.quiz_mode.domain.models.Category

class QuizCategoryDetailsPresenter(
    private val view: QuizCategoryDetailsView
) {
    private val _categoryLiveData = MutableLiveData<Category>()
    val category = _categoryLiveData

    fun loadCategoryById(categoryId: Int) {
        val fetched = TestCategories.cat.find {
            it.id == categoryId
        }

        _categoryLiveData.value = (fetched ?: return)

        view.displayCategoryDetails(category , TestQuestions.questions)
    }

    fun createNewCategory(categoryTitle: String, categoryDescription: String, color: Long?){
        _categoryLiveData.value = (Category(
            id = TestCategories.cat.size + 1,
            title = categoryTitle,
            description = categoryDescription,
            creationDate = Calendar.getInstance().time,
        ))
        view.displayCategoryDetails(category , TestQuestions.questions)
        category.value?.let { TestCategories.cat.add(it) }
    }

    fun updateCategoryDetails(categoryTitle: String, categoryDescription: String){
        if(category.value == null){
            createNewCategory(categoryTitle, categoryDescription, null)
        }

        _categoryLiveData.value?.let {
            it.title = categoryTitle
            it.description = categoryDescription
        }
        _categoryLiveData.value = category.value
    }

    fun updateCategoryStatus(categoryStatus: CategoryStatus){
        category.value?.status = categoryStatus
        _categoryLiveData.value = category.value
    }
}