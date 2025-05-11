package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.os.Bundle
import android.util.Log
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.database_management.DatabaseEventBus
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.utils.Constants
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.toSimplePresentation
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionFilter
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionFilter.Companion.toFilterOption
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionFilter.Companion.toSelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionSort
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionSort.Companion.toSelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionSort.Companion.toSortOption
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionSort.Companion.toSortType
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.toUIModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizQuestionsPresenter @Inject constructor(
    private val repository: QuizModeRepository,
) : BasePresenter<QuizQuestionsContract.View>(), QuizQuestionsContract.Presenter {
    private val data = MutableStateFlow<List<Question>>(emptyList())
    private val categoriesData = MutableStateFlow<List<Category>>(emptyList())
    private val searchQuery = MutableStateFlow("")
    private val sortOption = MutableStateFlow<QuestionSort.SortOptions>(QuestionSort.Companion.defaultSortOption)
    private val sortType = MutableStateFlow<QuestionSort.SortTypes>(QuestionSort.Companion.defaultSortType)
    private val filterType = MutableStateFlow<QuestionFilter>(QuestionFilter.Companion.defaultFilter)

    private var categoryId: Long? = null
    private var categoryTitle: String? = null
    private var categoryColor: Long? = null

    override fun onViewCreated() {
        super.onViewCreated()
        observeDatabaseEvent()
    }

    override fun getData(bundle: Bundle?) {
        bundle?.let {
            categoryId = it.getLong("categoryId")
            categoryTitle = it.getString("categoryTitle")
            categoryColor = it.getLong("categoryColor")
        }

        Log.d("KURWA", "categoryId: $categoryId, categoryTitle: $categoryTitle, categoryColor: $categoryColor")

        if(categoryTitle != null && categoryColor != null) {
            view.displayCategoryBadge(
                category = SimpleCategoryUIModel(
                    name = categoryTitle!!,
                    color = categoryColor!!
                )
            )
        }

        fetchData()
    }

    private fun fetchData(){
        presenterScope?.launch {
            delay(Constants.PRESENTER_INITIAL_DELAY)
            repository.getAllQuestions().collectLatest{
                when (it) {
                    is Response.Success -> {
                        data.value = it.data
                        observeDataChanges()
                        getCategoriesData()
                        displayData()
                    }
                    is Response.Error -> view.displayError(it.error)
                    is Response.Loading -> view.displayLoading()
                }
            }
        }
    }

    private fun observeDataChanges(){
        presenterScope?.launch {
            repository.getUpdatedQuestions().collectLatest { data.value = it }
        }
    }

    private fun observeDatabaseEvent() {
        presenterScope?.launch {
            DatabaseEventBus.eventReloadData.collectLatest {
                fetchData()
            }
        }
    }

    private fun displayData() {
        presenterScope?.launch {
            combine(
                data,
                searchQuery,
                sortOption,
                sortType,
                filterType
            ) { questions, query, sortOption, sortType, filter ->
                var searchedQuestions = filterByCategory(questions)
                searchedQuestions = searchedQuestions.filter { it.text.contains(query, ignoreCase = true) }
                searchedQuestions = sortData(searchedQuestions, sortOption, sortType)
                searchedQuestions = filterData(searchedQuestions, filter)
                searchedQuestions
            }.let { displayCombinedData(it) }
        }
    }

    private fun displayCombinedData(questions: Flow<List<Question>>) {
        presenterScope?.launch{
            combine(
                questions,
                categoriesData
            ) { questions, categories ->
                questions.map { question ->
                    question.toUIModel(
                        categoriesData.value.filter { question.linkedCategories.contains(it.id) }
                            .map { it.toSimplePresentation() }
                    )
                }
            }.collectLatest {
                if (data.value.isEmpty()) view.displayNoElementsView()
                else view.displayQuestions(it)
                view.displayElementsCount(it.size)
            }
        }
    }

    private fun getCategoriesData() {
        presenterScope?.launch {
            repository.getAllCategories().collectLatest { if (it is Response.Success) {
                    categoriesData.value = it.data
                    observeCategoriesDataChanges()
                }
            }
        }
    }

    private fun observeCategoriesDataChanges() {
        presenterScope?.launch {
            repository.getUpdatedCategories().collectLatest { categoriesData.value = it }
        }
    }

    private fun filterByCategory(data: List<Question>): List<Question> {
        if(categoryId == null) return data
        return data.filter { it.linkedCategories.contains(categoryId) }
    }

    private fun sortData(data: List<Question>, sortOption: QuestionSort.SortOptions, sortType: QuestionSort.SortTypes) : List<Question> {
        return when (sortOption) {
            QuestionSort.SortOptions.ByDate -> data.sortedByDescending { it.creationDate }
            QuestionSort.SortOptions.ByAnswersAmount -> data.sortedBy { it.answers.count() }
            QuestionSort.SortOptions.ByTitle -> data.sortedBy { it.text.lowercase() }
        }.let { if (sortType == QuestionSort.SortTypes.Descending) it.reversed() else it }
    }

    private fun filterData(data: List<Question>, filter: QuestionFilter) : List<Question> {
        return when (filter) {
            QuestionFilter.None -> data
            QuestionFilter.WithAnswers -> data.filter { it.answers.isNotEmpty() }
            QuestionFilter.WithCategories -> data.filter { it.linkedCategories.isNotEmpty() }
            QuestionFilter.WithCorrectAnswers -> data.filter { it.answers.any { it.isCorrect } }
            QuestionFilter.WithoutAnswers -> data.filter { it.answers.isEmpty()}
            QuestionFilter.WithoutCategories -> data.filter { it.linkedCategories.isEmpty() }
            QuestionFilter.WithoutCorrectAnswers -> data.filter { it.answers.none { it.isCorrect } }
        }
    }

    override fun removeQuestion(question: QuestionUIModel) {
        presenterScope?.launch {
            val response = repository.deleteQuestion(question.id)
            if (response is Response.Error) view.displayError(response.error)
        }
    }

    override fun searchBy(query: String) {
        searchQuery.value = query
    }

    override fun onSortMenuOpened() {
        view.displaySortMenu(
            sortOptions = QuestionSort.Companion.getSortOptions().map { it.toSelectableMenuItem(sortOption.value == it) },
            sortTypes = QuestionSort.Companion.getSortTypes().map { it.toSelectableMenuItem(sortType.value == it) }
        )
    }

    override fun onFilterMenuOpened() {
        view.displayFilterMenu(
            filterOptions = QuestionFilter.Companion.getFilters().map { it.toSelectableMenuItem(filterType.value == it) }
        )
    }

    override fun sortByOption(sort: SelectableMenuItem) {
        sortOption.value = sort.toSortOption() ?: QuestionSort.Companion.defaultSortOption
    }

    override fun sortByType(sort: SelectableMenuItem) {
        sortType.value = sort.toSortType() ?: QuestionSort.Companion.defaultSortType
    }

    override fun filterBy(filter: SelectableMenuItem) {
        filterType.value = filter.toFilterOption() ?: QuestionFilter.Companion.defaultFilter
    }

    override fun onNewElement() {
        view.displayNewElementSheet(categoryId)
    }
}