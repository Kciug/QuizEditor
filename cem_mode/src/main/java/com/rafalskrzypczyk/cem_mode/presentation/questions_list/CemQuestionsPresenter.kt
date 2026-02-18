package com.rafalskrzypczyk.cem_mode.presentation.questions_list

import com.rafalskrzypczyk.cem_mode.domain.CemModeRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.cem_mode.domain.models.CemQuestion
import com.rafalskrzypczyk.cem_mode.presentation.questions_list.ui_models.toUIModel
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class CemQuestionsPresenter @Inject constructor(
    private val repository: CemModeRepository
) : BasePresenter<CemQuestionsContract.View>(), CemQuestionsContract.Presenter {

    private var allQuestions: List<CemQuestion> = emptyList()
    private var allCategories: List<CemCategory> = emptyList()
    private var searchQuery: String = ""
    private var filterCategoryId: Long? = null
    private var isFirstLoad = true

    override fun getData() {
        combine(
            repository.getQuestions(),
            repository.getCategories().filter { it is Response.Success }
        ) { questionsResponse, categoriesResponse ->
            if (questionsResponse is Response.Loading && isFirstLoad) view.displayLoading()
            
            if (questionsResponse is Response.Success && categoriesResponse is Response.Success) {
                isFirstLoad = false
                allQuestions = questionsResponse.data
                allCategories = categoriesResponse.data
                updateDisplay()
            }
            
            if (questionsResponse is Response.Error) view.displayError(questionsResponse.error)
        }.launchIn(presenterScope!!)

        repository.getUpdatedQuestions()
            .onEach { questions ->
                allQuestions = questions
                updateDisplay()
            }.launchIn(presenterScope!!)
            
        repository.getUpdatedCategories()
            .onEach { categories ->
                allCategories = categories
                updateDisplay()
            }.launchIn(presenterScope!!)
    }

    private fun updateDisplay() {
        val filteredList = allQuestions.filter { question ->
            val matchesCategory = filterCategoryId == null || filterCategoryId in question.linkedCategories
            val matchesSearch = searchQuery.isEmpty() || question.text.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }

        if (filteredList.isEmpty() && searchQuery.isEmpty()) {
            view.displayNoElementsView()
        } else {
            view.displayQuestions(filteredList.map { it.toUIModel(allCategories) })
        }
        view.displayElementsCount(filteredList.size)
    }

    override fun filterByCategory(categoryId: Long?) {
        filterCategoryId = if (categoryId == -1L) null else categoryId
        updateDisplay()
    }

    override fun searchBy(query: String) {
        searchQuery = query
        updateDisplay()
    }

    override fun onQuestionClicked(question: com.rafalskrzypczyk.cem_mode.presentation.questions_list.ui_models.CemQuestionUIModel) {
        view.openQuestionDetails(question.id)
    }

    override fun removeQuestion(question: com.rafalskrzypczyk.cem_mode.presentation.questions_list.ui_models.CemQuestionUIModel) {
        presenterScope!!.launch {
            repository.deleteQuestion(question.id)
        }
    }

    override fun onSortMenuOpened() {}
    override fun onFilterMenuOpened() {}
    override fun sortByOption(optionId: Int) {}
    override fun sortByType(typeId: Int) {}
    override fun filterBy(filterId: Int) {}

    override fun onAddNewQuestion() {
        view.openQuestionDetails(null)
    }
}
