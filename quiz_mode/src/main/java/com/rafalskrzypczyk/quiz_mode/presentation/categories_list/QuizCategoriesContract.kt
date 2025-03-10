package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.domain.models.Category

interface QuizCategoriesContract {
    interface View : BaseContract.View {
        fun displayCategories(categories: List<Category>)
        fun displaySortMenu(sortOptions: List<SelectableMenuItem>, sortTypes: List<SelectableMenuItem>)
        fun displayFilterMenu(filterOptions: List<SelectableMenuItem>)
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun removeCategory(category: Category)
        fun searchBy(query: String)
        fun onSortMenuOpened()
        fun onFilterMenuOpened()
        fun sortByOption(sort: SelectableMenuItem)
        fun sortByType(sort: SelectableMenuItem)
        fun filterBy(filter: SelectableMenuItem)
    }
}
