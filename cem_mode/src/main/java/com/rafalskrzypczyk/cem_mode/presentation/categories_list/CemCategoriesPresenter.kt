package com.rafalskrzypczyk.cem_mode.presentation.categories_list

import com.rafalskrzypczyk.cem_mode.domain.CemModeRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Stack
import javax.inject.Inject

class CemCategoriesPresenter @Inject constructor(
    private val repository: CemModeRepository
) : BasePresenter<CemCategoriesContract.View>(), CemCategoriesContract.Presenter {

    private var allCategories: List<CemCategory> = emptyList()
    private var currentCategories: List<CemCategory> = emptyList()
    private var currentParentId: Long = CemCategory.ROOT_ID
    private var searchQuery: String = ""
    
    private val parentIdStack = Stack<Long>()

    override fun getData(parentId: Long) {
        currentParentId = parentId
        parentIdStack.clear()
        
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
        val filterId = if (currentParentId == CemCategory.ROOT_ID) null else currentParentId
        
        currentCategories = allCategories.filter { 
            it.parentCategoryID == filterId && (searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true))
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
            val parentId = current!!.parentCategoryID
            current = if (parentId == null) null else allCategories.find { it.id == parentId }
        }
        view.updateBreadcrumbs(path)
    }

    override fun searchBy(query: String) {
        searchQuery = query
        updateDisplay()
    }

    override fun onCategoryClicked(category: CemCategory) {
        parentIdStack.push(currentParentId)
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
        if (categoryId == currentParentId) return
        
        parentIdStack.clear()
        
        if (categoryId != CemCategory.ROOT_ID) {
            val path = mutableListOf<Long>()
            var current: CemCategory? = allCategories.find { it.id == categoryId }
            
            while (current != null) {
                val parentId = current!!.parentCategoryID
                if (parentId != null) {
                    path.add(0, parentId)
                    current = allCategories.find { it.id == parentId }
                } else {
                    path.add(0, CemCategory.ROOT_ID)
                    current = null
                }
            }
            path.forEach { parentIdStack.push(it) }
        }
        
        currentParentId = categoryId
        updateDisplay()
    }

    override fun onBackAction() {
        if (parentIdStack.isNotEmpty()) {
            val previousParentId = currentParentId
            currentParentId = parentIdStack.pop()
            updateDisplay()
            if (previousParentId != CemCategory.ROOT_ID) {
                view.openCategoryDetails(previousParentId, null)
            }
        }
    }

    override fun refreshUI() {
        updateDisplay()
    }
}
