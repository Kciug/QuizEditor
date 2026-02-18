package com.rafalskrzypczyk.cem_mode.presentation.questions_list

import com.rafalskrzypczyk.cem_mode.presentation.questions_list.ui_models.CemQuestionUIModel
import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem

interface CemQuestionsContract {
    interface View : BaseContract.View {
        fun displayQuestions(questions: List<CemQuestionUIModel>)
        fun displayNoElementsView()
        fun displayElementsCount(count: Int)
        override fun displayLoading()
        override fun displayError(message: String)
        fun displaySortMenu(sortOptions: List<SelectableMenuItem>, sortTypes: List<SelectableMenuItem>)
        fun displayFilterMenu(filterOptions: List<SelectableMenuItem>)
        fun openQuestionDetails(questionId: Long?)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getData()
        fun searchBy(query: String)
        fun onQuestionClicked(question: CemQuestionUIModel)
        fun removeQuestion(question: CemQuestionUIModel)
        fun onSortMenuOpened()
        fun onFilterMenuOpened()
        fun sortByOption(optionId: Int)
        fun sortByType(typeId: Int)
        fun filterBy(filterId: Int)
        fun onAddNewQuestion()
    }
}
