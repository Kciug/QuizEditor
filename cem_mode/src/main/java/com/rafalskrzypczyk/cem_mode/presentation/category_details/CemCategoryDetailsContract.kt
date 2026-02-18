package com.rafalskrzypczyk.cem_mode.presentation.category_details

import android.os.Bundle
import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.domain.models.CategoryStatus
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem

interface CemCategoryDetailsContract {
    interface View : BaseContract.View {
        fun setupView()
        fun setupNewElementView()
        fun displayCategoryDetails(categoryTitle: String, categoryDescription: String)
        fun displayCreatedDetails(date: String)
        fun displayCategoryColor(color: Int)
        fun displayCategoryStatus(status: CategoryStatus)
        fun displayQuestionCount(questionCount: Int)
        fun displaySubcategoryCount(subcategoryCount: Int)
        fun displayCategoryStatusMenu(options: List<SelectableMenuItem>)
        fun displayColorPicker(currentColor: Int)
        fun displayIsFree(isFree: Boolean)
        fun displayMigrationButton(isVisible: Boolean)
        fun openMigrationSheet(categoryId: Long)
        fun displaySubcategoriesList(parentId: Long)
        fun displayQuestionsList(categoryId: Long, categoryTitle: String, categoryColor: Long)
        fun displayNewQuestionSheet(categoryId: Long)
        fun displayNewSubcategorySheet(parentId: Long)
        fun displayToastMessage(message: String)
        fun displayContent()
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun onMigrateClicked()
        fun getData(bundle: Bundle?)
        fun createNewCategory(categoryTitle: String)
        fun updateCategoryTitle(categoryTitle: String)
        fun updateCategoryDescription(categoryDescription: String)
        fun onChangeColor()
        fun updateCategoryColor(color: Int)
        fun onChangeCategoryStatus()
        fun updateCategoryStatus(status: SelectableMenuItem)
        fun updateIsFree(isFree: Boolean)
        fun onCategorySubcategories()
        fun onCategoryQuestions()
        fun onNewQuestion()
        fun onNewSubcategory()
    }
}
