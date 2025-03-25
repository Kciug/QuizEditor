package com.rafalskrzypczyk.swipe_mode.presentation.question_list

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeRepository
import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestion
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models.toSimpleUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class SwipeQuestionPresenter @Inject constructor(
    private val repository: SwipeModeRepository,
) : BasePresenter<SwipeQuestionsContract.View>(), SwipeQuestionsContract.Presenter {
    private val data = MutableStateFlow<List<SwipeQuestion>>(emptyList())
    private val searchQuery = MutableStateFlow("")
//    private val sortOption = MutableStateFlow<CategorySort.SortOptions>(CategorySort.Companion.defaultSortOption)
//    private val sortType = MutableStateFlow<CategorySort.SortTypes>(CategorySort.Companion.defaultSortType)
//    private val filterType = MutableStateFlow<CategoryFilters>(CategoryFilters.Companion.defaultFilter)

    override fun onViewCreated() {
        super.onViewCreated()

        presenterScope?.launch{
            repository.getAllQuestions().collectLatest {
                when(it){
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

    private fun displayData() {
        if (data.value.isEmpty()) {
            view.displayNoElementsView()
            return
        }

        presenterScope?.launch {
            combine(
                data,
                searchQuery,
            ) { questions, query ->
                var searchedQuestions = questions.filter { it.text.contains(query, ignoreCase = true) }
                searchedQuestions
            }.collectLatest {
                view.displayQuestions(it.map { it.toSimpleUIModel() })
            }
        }
    }

    private fun observeDataChanges() {
        presenterScope?.launch {
            repository.getUpdatedQuestions().collectLatest { data.value = it }
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
        TODO("Not yet implemented")
    }

    override fun onFilterMenuOpened() {
        TODO("Not yet implemented")
    }

    override fun sortByOption(sort: SelectableMenuItem) {
        TODO("Not yet implemented")
    }

    override fun sortByType(sort: SelectableMenuItem) {
        TODO("Not yet implemented")
    }

    override fun filterBy(filter: SelectableMenuItem) {
        TODO("Not yet implemented")
    }

}