package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import com.rafalskrzypczyk.quiz_mode.domain.models.Category

interface QuizCategoriesContract {
    interface View {
        fun displayCategories(categories: List<Category>)
    }
    interface Presenter {
        fun loadCategories()
        fun removeCategory(category: Category)
        fun searchBy(query: String)
        fun sortByOption(sort: CategorySort.SortOptions)
        fun sortByType(sort: CategorySort.SortTypes)
        fun filterBy(filter: CategoryFilters)
        fun getCurrentSortOption(): CategorySort.SortOptions
        fun getCurrentSortType(): CategorySort.SortTypes
        fun getCurrentFilter(): CategoryFilters
    }
}
