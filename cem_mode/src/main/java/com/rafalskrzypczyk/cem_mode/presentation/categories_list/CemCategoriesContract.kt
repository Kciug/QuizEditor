package com.rafalskrzypczyk.cem_mode.presentation.categories_list

import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem

interface CemCategoriesContract {
    interface View : BaseContract.View {
        fun displayCategories(categories: List<CemCategory>)
        fun displayNoElementsView()
        fun displayElementsCount(count: Int)
        override fun displayLoading()
        override fun displayError(message: String)
        fun displaySortMenu(sortOptions: List<SelectableMenuItem>, sortTypes: List<SelectableMenuItem>)
        fun displayFilterMenu(filterOptions: List<SelectableMenuItem>)
        fun updateBreadcrumbs(path: List<CemCategory>)
        fun openCategoryDetails(categoryId: Long?, parentId: Long?)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getData(parentId: Long)
        fun searchBy(query: String)
        fun onCategoryClicked(category: CemCategory)
        fun removeCategory(category: CemCategory)
        fun onSortMenuOpened()
        fun onFilterMenuOpened()
        fun sortByOption(optionId: Int)
        fun sortByType(typeId: Int)
        fun filterBy(filterId: Int)
        fun onAddNewCategory()
        fun onBreadcrumbClicked(categoryId: Long)
        fun onBackAction()
        fun refreshUI()
    }
}
