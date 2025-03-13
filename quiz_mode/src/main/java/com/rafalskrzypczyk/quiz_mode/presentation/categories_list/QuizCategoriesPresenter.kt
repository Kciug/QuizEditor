package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.di.MainDispatcher
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.CategoryStatus
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.ui_models.CategoryFilters
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.ui_models.CategoryFilters.Companion.toFilterOption
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.ui_models.CategoryFilters.Companion.toSelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.ui_models.CategorySort
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.ui_models.CategorySort.Companion.toSelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.ui_models.CategorySort.Companion.toSortOption
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.ui_models.CategorySort.Companion.toSortType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizCategoriesPresenter @Inject constructor(
    private val repository: QuizModeRepository,
    @MainDispatcher private val dispatcher: CoroutineDispatcher,
) : BasePresenter<QuizCategoriesContract.View>(), QuizCategoriesContract.Presenter {
    private var presenterScope = CoroutineScope(SupervisorJob() + dispatcher)

    private val data = MutableStateFlow<List<Category>>(emptyList())
    private val searchQuery = MutableStateFlow("")
    private val sortOption = MutableStateFlow<CategorySort.SortOptions>(CategorySort.Companion.defaultSortOption)
    private val sortType = MutableStateFlow<CategorySort.SortTypes>(CategorySort.Companion.defaultSortType)
    private val filterType = MutableStateFlow<CategoryFilters>(CategoryFilters.Companion.defaultFilter)

    override fun onViewCreated() {
        super.onViewCreated()
        presenterScope = CoroutineScope(SupervisorJob() + dispatcher)
        presenterScope.launch { getData() }
    }

    private fun getData(){
        presenterScope.launch {
            repository.getAllCategories().collectLatest {
                when (it) {
                    is Response.Success -> {
                        data.value = it.data
                        observeDataChanges()
                        displayData()
                    }
                    is Response.Error -> view.displayError(it.error)
                    is Response.Loading -> view.displayLoading()
                }
            }
        }
    }

    private fun observeDataChanges(){
        presenterScope.launch {
            repository.getUpdatedCategories().collectLatest { data.value = it }
        }
    }

    private fun displayData(){
        presenterScope.launch {
            combine(
                data,
                searchQuery,
                sortOption,
                sortType,
                filterType
            ) { categories, query, sortOption, sortType, filter ->
                var searchedCategories = categories.filter { it.title.contains(query, ignoreCase = true) }
                searchedCategories = sortData(searchedCategories, sortOption, sortType)
                searchedCategories = filterData(searchedCategories, filter)
                searchedCategories
            }.collectLatest {
                view.displayCategories(it)
            }
        }
    }

    private fun sortData(data: List<Category>, sortOption: CategorySort.SortOptions, sortType: CategorySort.SortTypes) : List<Category> {
        return when (sortOption) {
            CategorySort.SortOptions.ByDate -> data.sortedByDescending { it.creationDate }
            CategorySort.SortOptions.ByQuestionsAmount -> data.sortedBy { it.linkedQuestions.count() }
            CategorySort.SortOptions.ByTitle -> data.sortedBy { it.title.lowercase() }
        }.let { if (sortType == CategorySort.SortTypes.Descending) it.reversed() else it }
    }

    private fun filterData(data: List<Category>, filter: CategoryFilters) : List<Category> {
        return when (filter) {
            CategoryFilters.None -> data
            is CategoryFilters.ByStatus -> data.filter { it.status == filter.status }
            CategoryFilters.WithQuestions -> data.filter { it.linkedQuestions.isNotEmpty() }
            CategoryFilters.WithoutQuestions -> data.filter { it.linkedQuestions.isEmpty() }
            CategoryFilters.IsMigrated -> data.filter { it.productionTransferDate != null }
        }
    }

    override fun removeCategory(category: Category) {
        presenterScope.launch {
            val response = repository.deleteCategory(category.id)
            if (response is Response.Error) view.displayError(response.error)
        }
    }

    override fun searchBy(query: String) {
        searchQuery.value = query
    }

    override fun onSortMenuOpened() {
        view.displaySortMenu(
            sortOptions = CategorySort.Companion.getSortOptions().map { it.toSelectableMenuItem(sortOption.value == it) },
            sortTypes = CategorySort.Companion.getSortTypes().map { it.toSelectableMenuItem(sortType.value == it) }
        )
    }

    override fun onFilterMenuOpened() {
        view.displayFilterMenu(
            filterOptions = CategoryFilters.Companion.getFilters().map { filter ->
                if (filter is CategoryFilters.ByStatus) {
                    val selectedStatus = (filterType.value as? CategoryFilters.ByStatus)?.status
                    filter.toSelectableMenuItem(
                        filterType.value == filter,
                        CategoryStatus.entries.map { status ->
                            status.toSelectableMenuItem(status == selectedStatus)
                        }
                    )
                } else {
                    filter.toSelectableMenuItem(filterType.value == filter)
                }
            }
        )
    }

    override fun sortByOption(option: SelectableMenuItem) {
        sortOption.value = option.toSortOption() ?: CategorySort.Companion.defaultSortOption
    }

    override fun sortByType(type: SelectableMenuItem) {
        sortType.value = type.toSortType() ?: CategorySort.Companion.defaultSortType
    }

    override fun filterBy(filter: SelectableMenuItem) {
        filterType.value = filter.toFilterOption() ?: CategoryFilters.Companion.defaultFilter
    }

    override fun onDestroy() {
        presenterScope.cancel()
        super.onDestroy()
    }
}