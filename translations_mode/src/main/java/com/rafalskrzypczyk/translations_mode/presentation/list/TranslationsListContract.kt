package com.rafalskrzypczyk.translations_mode.presentation.list

import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.translations_mode.presentation.list.ui_models.TranslationQuestionUIModel

interface TranslationsListContract {
    interface View : BaseContract.View {
        fun displayQuestions(questions: List<TranslationQuestionUIModel>)
        fun displaySortMenu(sortOptions: List<SelectableMenuItem>, sortTypes: List<SelectableMenuItem>)
        fun displayFilterMenu(filterOptions: List<SelectableMenuItem>)
        fun displayNoElementsView()
        fun displayElementsCount(count: Int)
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun removeQuestion(questionId: Long)
        fun searchBy(query: String)
        fun onSortMenuOpened()
        fun onFilterMenuOpened()
        fun sortByOption(sort: SelectableMenuItem)
        fun sortByType(sort: SelectableMenuItem)
        fun filterBy(filter: SelectableMenuItem)
    }
}
