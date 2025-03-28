package com.rafalskrzypczyk.swipe_mode.presentation.question_list

import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models.SwipeQuestionSimpleUIModel

interface SwipeQuestionsContract {
    interface View : BaseContract.View {
        fun displayQuestions(questions: List<SwipeQuestionSimpleUIModel>)
        fun displaySortMenu(sortOptions: List<SelectableMenuItem>, sortTypes: List<SelectableMenuItem>)
        fun displayFilterMenu(filterOptions: List<SelectableMenuItem>)
        fun displayNoElementsView()
        fun displayElementsCount(count: Int)
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun removeCategory(questionId: Long)
        fun searchBy(query: String)
        fun onSortMenuOpened()
        fun onFilterMenuOpened()
        fun sortByOption(sort: SelectableMenuItem)
        fun sortByType(sort: SelectableMenuItem)
        fun filterBy(filter: SelectableMenuItem)
    }
}