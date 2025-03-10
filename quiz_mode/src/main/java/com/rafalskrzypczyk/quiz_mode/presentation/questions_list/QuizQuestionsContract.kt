package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem

interface QuizQuestionsContract{
    interface View : BaseContract.View {
        fun displayQuestions(questions: List<QuestionUIModel>)
        fun displaySortMenu(sortOptions: List<SelectableMenuItem>, sortTypes: List<SelectableMenuItem>)
        fun displayFilterMenu(filterOptions: List<SelectableMenuItem>)
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun removeQuestion(question: QuestionUIModel)
        fun searchBy(query: String)
        fun onSortMenuOpened()
        fun onFilterMenuOpened()
        fun sortByOption(sort: SelectableMenuItem)
        fun sortByType(sort: SelectableMenuItem)
        fun filterBy(filter: SelectableMenuItem)
    }
}