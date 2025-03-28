package com.rafalskrzypczyk.swipe_mode.presentation.question_list

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeRepository
import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestion
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models.SwipeQuestionsFilters
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models.SwipeQuestionsFilters.Companion.toFilterOption
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models.SwipeQuestionsFilters.Companion.toSelectableMenuItem
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models.SwipeQuestionsSort
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models.SwipeQuestionsSort.Companion.toSelectableMenuItem
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models.SwipeQuestionsSort.Companion.toSortOption
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models.SwipeQuestionsSort.Companion.toSortType
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models.toSimpleUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class SwipeQuestionsPresenter @Inject constructor(
    private val repository: SwipeModeRepository,
) : BasePresenter<SwipeQuestionsContract.View>(), SwipeQuestionsContract.Presenter {
    private val questionsData = MutableStateFlow<List<SwipeQuestion>>(emptyList())
    private val searchQuery = MutableStateFlow("")
    private val sortOption = MutableStateFlow<SwipeQuestionsSort.SortOptions>(SwipeQuestionsSort.Companion.defaultSortOption)
    private val sortType = MutableStateFlow<SwipeQuestionsSort.SortTypes>(SwipeQuestionsSort.Companion.defaultSortType)
    private val filterType = MutableStateFlow<SwipeQuestionsFilters>(SwipeQuestionsFilters.Companion.defaultFilter)

    override fun onViewCreated() {
        super.onViewCreated()

        presenterScope?.launch{
            repository.getAllQuestions().collectLatest {
                when(it){
                    is Response.Success -> {
                        questionsData.value = it.data
                        observeDataChanges()
                        displayData()
                    }
                    is Response.Error -> view.displayError(it.error)
                    is Response.Loading -> view.displayLoading()
                }
            }
        }
    }

    private fun displayData() {
        if (questionsData.value.isEmpty()) {
            view.displayNoElementsView()
            return
        }

        presenterScope?.launch {
            combine(
                questionsData,
                searchQuery,
                sortOption,
                sortType,
                filterType
            ) { questions, query, sortOption, sortType, filter ->
                var searchedQuestions = questions.filter { it.text.contains(query, ignoreCase = true) }
                searchedQuestions = sortData(searchedQuestions, sortOption, sortType)
                searchedQuestions = filterData(searchedQuestions, filter)
                searchedQuestions
            }.collectLatest {
                view.displayQuestions(it.map { it.toSimpleUIModel() })
                view.displayElementsCount(it.size)
            }
        }
    }

    private fun sortData(
        data: List<SwipeQuestion>,
        sortOption: SwipeQuestionsSort.SortOptions,
        sortType: SwipeQuestionsSort.SortTypes
    ) : List<SwipeQuestion> {
        return when (sortOption) {
            SwipeQuestionsSort.SortOptions.ByDate -> data.sortedByDescending { it.dateCreated }
            SwipeQuestionsSort.SortOptions.ByTitle -> data.sortedBy { it.text.lowercase() }
        }.let { if (sortType == SwipeQuestionsSort.SortTypes.Descending) it.reversed() else it }
    }

    private fun filterData(data: List<SwipeQuestion>, filter: SwipeQuestionsFilters) : List<SwipeQuestion> {
        return when (filter) {
            SwipeQuestionsFilters.None -> data
            SwipeQuestionsFilters.AreCorrect -> data.filter { it.isCorrect }
            SwipeQuestionsFilters.AreIncorrect -> data.filter { it.isCorrect.not() }
        }
    }

    private fun observeDataChanges() {
        presenterScope?.launch {
            repository.getUpdatedQuestions().collectLatest { questionsData.value = it }
        }
    }

    override fun removeCategory(questionId: Long) {
        presenterScope?.launch {
            val response = repository.deleteQuestion(questionId)
            if (response is Response.Error) view.displayError(response.error)
        }
    }

    override fun searchBy(query: String) {
        searchQuery.value = query
    }

    override fun onSortMenuOpened() {
        view.displaySortMenu(
            sortOptions = SwipeQuestionsSort.Companion.getSortOptions().map { it.toSelectableMenuItem(sortOption.value == it) },
            sortTypes = SwipeQuestionsSort.Companion.getSortTypes().map { it.toSelectableMenuItem(sortType.value == it) }
        )
    }

    override fun onFilterMenuOpened() {
        view.displayFilterMenu(
            filterOptions = SwipeQuestionsFilters.Companion.getFilters().map { it.toSelectableMenuItem(filterType.value == it) }
        )
    }

    override fun sortByOption(sort: SelectableMenuItem) {
        sortOption.value = sort.toSortOption() ?: SwipeQuestionsSort.Companion.defaultSortOption
    }

    override fun sortByType(sort: SelectableMenuItem) {
        sortType.value = sort.toSortType() ?: SwipeQuestionsSort.Companion.defaultSortType
    }

    override fun filterBy(filter: SelectableMenuItem) {
        filterType.value = filter.toFilterOption() ?: SwipeQuestionsFilters.Companion.defaultFilter
    }
}