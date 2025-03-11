package com.rafalskrzypczyk.quiz_mode.presentation.categeory_details

import android.os.Bundle
import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.domain.models.CategoryStatus
import com.rafalskrzypczyk.quiz_mode.domain.models.Question

interface QuizCategoryDetailsContract {
    interface View : BaseContract.View {
        fun setupView()
        fun setupNewElementView()
        fun displayCategoryDetails(categoryTitle: String, categoryDescription: String)
        fun displayCategoryColor(color: Int)
        fun displayCategoryStatus(status: CategoryStatus)
        fun displayQuestionCount(questionCount: Int)
        fun displayQuestionList(questions: List<Question>)
        fun displayCategoryStatusMenu(options: List<SelectableMenuItem>)
        fun displayColorPicker(currentColor: Int)
        fun displayQuestionsPicker()
        fun displayNewQuestionSheet(parentCategoryId: Long)
        fun displayQuestionListLoading()
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
        fun updateQuestionList()
        fun onQuestionFromList()
        fun onNewQuestion()
    }
}