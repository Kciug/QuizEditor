package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import android.util.Log
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.domain.CategoryStatus
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.CategoryFilters.Companion.toFilterOption
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.CategoryFilters.Companion.toSelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.CategorySort.Companion.toSelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.CategorySort.Companion.toSortOption
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.CategorySort.Companion.toSortType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizCategoriesPresenter @Inject constructor(
    private val repository: QuizModeRepository,
    private val dispatcher: CoroutineDispatcher,
) : BasePresenter<QuizCategoriesContract.View>(), QuizCategoriesContract.Presenter {
    private var presenterScope = CoroutineScope(SupervisorJob() + dispatcher)

    private val searchQuery = MutableStateFlow("")
    private val sortOption = MutableStateFlow<CategorySort.SortOptions>(CategorySort.defaultSortOption)
    private val sortType = MutableStateFlow<CategorySort.SortTypes>(CategorySort.defaultSortType)
    private val filterType = MutableStateFlow<CategoryFilters>(CategoryFilters.defaultFilter)

    override fun onAttach(view: QuizCategoriesContract.View) {
        super.onAttach(view)
        presenterScope = CoroutineScope(SupervisorJob() + dispatcher)
    }

    override fun onViewCreated() {
        super.onViewCreated()
        Log.d("KURWA", "CatPresenter:onViewCreated: invoked")
        Log.d("KURWA", "CatPresenter:onViewCreated: scope: ${presenterScope.isActive}")
        presenterScope.launch {
            val combinedData = combine(
                repository.getAllCategories(),
                repository.getUpdatedCategories()
            ) { response, categories ->
                Log.d("KURWA", "CatPresenter:combine: response: $response")
                Log.d("KURWA", "CatPresenter:combine: categories: $categories")
                when (response) {
                    is Response.Success -> Response.Success(categories)
                    is Response.Error -> response
                    is Response.Loading -> Response.Loading
                }
            }

            combine(
                combinedData,
                searchQuery,
                sortOption,
                sortType,
                filterType
            ) { response, query, sortOption, sortType, filter ->
                when (response) {
                    is Response.Success -> {
                        var categories =
                            response.data.filter { it.title.contains(query, ignoreCase = true) }

                        categories = when (sortOption) {
                            CategorySort.SortOptions.ByDate -> categories.sortedBy { it.creationDate }
                            CategorySort.SortOptions.ByQuestionsAmount -> categories.sortedBy { it.linkedQuestions.count() }
                            CategorySort.SortOptions.ByTitle -> categories.sortedBy { it.title }
                        }
                        if (sortType == CategorySort.SortTypes.Descending) categories =
                            categories.reversed()

                        categories = when (filter) {
                            CategoryFilters.None -> categories
                            is CategoryFilters.ByStatus -> categories.filter { it.status == filter.status }
                            CategoryFilters.WithQuestions -> categories.filter { it.linkedQuestions.isNotEmpty() }
                            CategoryFilters.WithoutQuestions -> categories.filter { it.linkedQuestions.isEmpty() }
                            CategoryFilters.IsMigrated -> categories
                        }

                        Response.Success(categories)
                    }

                    is Response.Error -> response
                    is Response.Loading -> Response.Loading
                }
            }.collectLatest { filteredResponse ->
                when (filteredResponse) {
                    is Response.Success -> view.displayCategories(filteredResponse.data)
                    is Response.Error -> view.showError(filteredResponse.error)
                    is Response.Loading -> view.showLoading()
                }
            }
        }
    }

    override fun removeCategory(category: Category) {
        presenterScope.launch {
            val response = repository.deleteCategory(category.id)
            if (response is Response.Error) view.showError(response.error)
        }
    }

    override fun searchBy(query: String) {
        searchQuery.value = query
    }

    override fun onSortMenuOpened() {
        view.displaySortMenu(
            sortOptions = CategorySort.getSortOptions()
                .map { it.toSelectableMenuItem(sortOption.value == it) },
            sortTypes = CategorySort.getSortTypes()
                .map { it.toSelectableMenuItem(sortOption.value == it) }
        )
    }

    override fun onFilterMenuOpened() {
        view.displayFilterMenu(
            filterOptions = CategoryFilters.getFilters().map {
                if (it is CategoryFilters.ByStatus) it.toSelectableMenuItem(
                    filterType.value == it,
                    CategoryStatus.entries.map { status -> status.toSelectableMenuItem(status == it.status) }
                )
                else it.toSelectableMenuItem(filterType.value == it)
            },
        )
    }

    override fun sortByOption(option: SelectableMenuItem) {
        sortOption.value = option.toSortOption() ?: CategorySort.defaultSortOption
    }

    override fun sortByType(type: SelectableMenuItem) {
        sortType.value = type.toSortType() ?: CategorySort.defaultSortType
    }

    override fun filterBy(filter: SelectableMenuItem) {
        filterType.value = filter.toFilterOption() ?: CategoryFilters.defaultFilter
    }

    override fun onDestroy() {
        presenterScope.cancel()
        super.onDestroy()
    }
}