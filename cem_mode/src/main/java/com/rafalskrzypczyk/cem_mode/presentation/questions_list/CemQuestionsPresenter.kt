package com.rafalskrzypczyk.cem_mode.presentation.questions_list

import com.rafalskrzypczyk.cem_mode.domain.CemModeRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.cem_mode.domain.models.CemQuestion
import com.rafalskrzypczyk.cem_mode.presentation.questions_list.ui_models.CemQuestionUIModel
import com.rafalskrzypczyk.cem_mode.presentation.questions_list.ui_models.toUIModel
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.presentation.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import kotlinx.coroutines.flow.combine
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

    override fun getData() {
        combine(
            repository.getQuestions(),
            repository.getCategories()
        ) { questionsResponse, categoriesResponse ->
            if (questionsResponse is Response.Loading || categoriesResponse is Response.Loading) {
                view.displayLoading()
                return@combine
            }

            if (questionsResponse is Response.Error) {
                view.displayError(questionsResponse.error)
                return@combine
            }
            if (categoriesResponse is Response.Error) {
                view.displayError(categoriesResponse.error)
                return@combine
            }

            allQuestions = (questionsResponse as Response.Success).data
            allCategories = (categoriesResponse as Response.Success).data
            updateDisplay()
        }.launchIn(presenterScope!!)

        repository.getUpdatedQuestions()
            .onEach { getData() }
            .launchIn(presenterScope!!)
    }

    private fun updateDisplay() {
        val filtered = allQuestions.filter { q ->
            (filterCategoryId == null || q.linkedCategories.contains(filterCategoryId)) &&
            (searchQuery.isEmpty() || q.text.contains(searchQuery, ignoreCase = true))
        }

        val uiModels = filtered.map { q ->
            val linkedCatUI = allCategories.filter { it.id in q.linkedCategories }
                .map { SimpleCategoryUIModel(it.title, it.color.toLong()) }
            q.toUIModel(linkedCatUI)
        }

        if (uiModels.isEmpty()) {
            view.displayNoElementsView()
        } else {
            view.displayQuestions(uiModels)
        }
        view.displayElementsCount(uiModels.size)
    }

    override fun searchBy(query: String) {
        searchQuery = query
        updateDisplay()
    }

    override fun filterByCategory(categoryId: Long?) {
        filterCategoryId = categoryId
        updateDisplay()
    }

    override fun onQuestionClicked(question: CemQuestionUIModel) {
        view.openQuestionDetails(question.id)
    }

    override fun removeQuestion(question: CemQuestionUIModel) {
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
