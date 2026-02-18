package com.rafalskrzypczyk.cem_mode.presentation.categories_list

import com.rafalskrzypczyk.cem_mode.domain.CemModeRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class CemCategoriesPresenter @Inject constructor(
    private val repository: CemModeRepository
) : BasePresenter<CemCategoriesContract.View>(), CemCategoriesContract.Presenter {

    private var allCategories: List<CemCategory> = emptyList()
    private var currentCategories: List<CemCategory> = emptyList()
    private var currentParentId: Long = CemCategory.ROOT_ID
    private var searchQuery: String = ""

    override fun getData(parentId: Long) {
        currentParentId = parentId
        repository.getCategories()
            .onEach { response ->
                when (response) {
                    is Response.Loading -> view.displayLoading()
                    is Response.Success -> {
                        allCategories = response.data
                        updateDisplay()
                    }
                    is Response.Error -> view.displayError(response.error)
                }
            }.launchIn(presenterScope!!)

        repository.getUpdatedCategories()
            .onEach { categories ->
                allCategories = categories
                updateDisplay()
            }.launchIn(presenterScope!!)
    }

    private fun updateDisplay() {
        currentCategories = allCategories.filter { 
            it.parentCategoryId == currentParentId && (searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true))
        }

        if (currentCategories.isEmpty()) {
            view.displayNoElementsView()
        } else {
            view.displayCategories(currentCategories)
        }
        view.displayElementsCount(currentCategories.size)
        updateBreadcrumbs()
    }

    private fun updateBreadcrumbs() {
        val path = mutableListOf<CemCategory>()
        var current: CemCategory? = allCategories.find { it.id == currentParentId }
        while (current != null) {
            path.add(0, current)
            current = allCategories.find { it.id == current!!.parentCategoryId }
        }
        view.updateBreadcrumbs(path)
    }

    override fun searchBy(query: String) {
        searchQuery = query
        updateDisplay()
    }

    override fun onCategoryClicked(category: CemCategory) {
        currentParentId = category.id
        updateDisplay()
    }

    override fun removeCategory(category: CemCategory) {
        presenterScope!!.launch {
            repository.deleteCategory(category.id)
        }
    }

    override fun onSortMenuOpened() {}
    override fun onFilterMenuOpened() {}
    override fun sortByOption(optionId: Int) {}
    override fun sortByType(typeId: Int) {}
    override fun filterBy(filterId: Int) {}

    override fun onAddNewCategory() {
        view.openCategoryDetails(null, if (currentParentId == CemCategory.ROOT_ID) null else currentParentId)
    }

    override fun onBreadcrumbClicked(categoryId: Long) {
        currentParentId = categoryId
        updateDisplay()
    }
}
