package com.rafalskrzypczyk.quiz_mode.presentation.category_details

import android.os.Bundle
import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.domain.models.CategoryStatus
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem

interface QuizCategoryDetailsContract {
    interface View : BaseContract.View {
        fun setupView()
        fun setupNewElementView()
        fun displayCategoryDetails(categoryTitle: String, categoryDescription: String)
        fun displayCreatedDetails(date: String)
        fun displayCategoryColor(color: Int)
        fun displayCategoryStatus(status: CategoryStatus)
        fun displayQuestionCount(questionCount: Int)
        fun displayCategoryStatusMenu(options: List<SelectableMenuItem>)
        fun displayColorPicker(currentColor: Int)
        fun displayIsFree(isFree: Boolean)
        fun displayQuestionsPicker()
        fun displayNewQuestionSheet(parentCategoryId: Long)
        fun displayQuestionsList(categoryId: Long, categoryTitle: String, categoryColor: Long)
        fun displayToastMessage(message: String)
        fun displayContent()
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun getData(bundle: Bundle?)
        fun createNewCategory(categoryTitle: String)
        fun updateCategoryTitle(categoryTitle: String)
        fun updateCategoryDescription(categoryDescription: String)
        fun onChangeColor()
        fun updateCategoryColor(color: Int)
        fun onChangeCategoryStatus()
        fun updateCategoryStatus(status: SelectableMenuItem)
        fun updateIsFree(isFree: Boolean)
        fun onQuestionFromList()
        fun onNewQuestion()
        fun onCategoryQuestions()
    }
}