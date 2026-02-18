package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.os.Bundle
import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.presentation.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionUIModel

interface QuizQuestionsContract{
    interface View : BaseContract.View {
        fun displayQuestions(questions: List<QuestionUIModel>)
        fun displaySortMenu(sortOptions: List<SelectableMenuItem>, sortTypes: List<SelectableMenuItem>)
        fun displayFilterMenu(filterOptions: List<SelectableMenuItem>)
        fun displayNoElementsView()
        fun displayElementsCount(count: Int)
        fun displayNewElementSheet(categoryId: Long?)
        fun displayCategoryBadge(category: SimpleCategoryUIModel)
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun getData(bundle: Bundle?)
        fun removeQuestion(question: QuestionUIModel)
        fun searchBy(query: String)
        fun onSortMenuOpened()
        fun onFilterMenuOpened()
        fun sortByOption(sort: SelectableMenuItem)
        fun sortByType(sort: SelectableMenuItem)
        fun filterBy(filter: SelectableMenuItem)
        fun onNewElement()
    }
}