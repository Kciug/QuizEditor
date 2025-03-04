package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import android.util.Log
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizCategoriesPresenter @Inject constructor(
    private val view: QuizCategoriesContract.View,
    private val repository: QuizModeRepository
) : BasePresenter(), QuizCategoriesContract.Presenter {
    private val searchQuery = MutableStateFlow("")
    private val sortOption = MutableStateFlow<CategorySort.SortOptions>(CategorySort.defaultSortOption)
    private val sortType = MutableStateFlow<CategorySort.SortTypes>(CategorySort.defaultSortType)
    private val filterType = MutableStateFlow<CategoryFilters>(CategoryFilters.defaultFilter)

    override fun loadCategories() {
        presenterScope.launch {
            combine(
                repository.getAllCategories(),
                searchQuery,
                sortOption,
                sortType,
                filterType
            ) { response, query, sortOption, sortType, filter ->
                when (response) {
                    is Response.Success -> {
                        var categories = response.data.filter { it.title.contains(query, ignoreCase = true) }

                        categories = when (sortOption) {
                            CategorySort.SortOptions.ByDate -> categories.sortedBy { it.creationDate }
                            CategorySort.SortOptions.ByQuestionsAmount -> categories.sortedBy { it.linkedQuestions.count() }
                            CategorySort.SortOptions.ByTitle -> categories.sortedBy { it.title }
                        }
                        if(sortType == CategorySort.SortTypes.Descending) categories = categories.reversed()

                        categories = when(filter){
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
            }.collect { filteredResponse ->
                when (filteredResponse) {
                    is Response.Success -> view.displayCategories(filteredResponse.data.map { it.copy() })
                    is Response.Error -> Log.e("QuizQuestionsPresenter", "Error: ${filteredResponse.error}")
                    is Response.Loading -> Log.d("QuizQuestionsPresenter", "Loading")
                }
            }
        }
    }

    override fun removeCategory(category: Category) {
        presenterScope.launch {
            val response = repository.deleteCategory(category.id)
            if (response is Response.Error) Log.e(
                "QuizQuestionsPresenter",
                "Error: ${response.error}"
            )
            loadCategories()
        }
    }

    override fun searchBy(query: String) {
        searchQuery.value = query
    }

    override fun sortByOption(option: CategorySort.SortOptions) {
        sortOption.value = option
    }

    override fun sortByType(type: CategorySort.SortTypes) {
        sortType.value = type
    }

    override fun filterBy(filter: CategoryFilters) {
        filterType.value = filter
    }

    override fun getCurrentSortOption(): CategorySort.SortOptions = sortOption.value

    override fun getCurrentSortType(): CategorySort.SortTypes = sortType.value

    override fun getCurrentFilter(): CategoryFilters = filterType.value
}