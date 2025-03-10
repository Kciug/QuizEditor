package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.toSimplePresentation
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.QuestionFilter.Companion.toFilterOption
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.QuestionFilter.Companion.toSelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.QuestionSort.Companion.toSelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.QuestionSort.Companion.toSortOption
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.QuestionSort.Companion.toSortType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizQuestionsPresenter @Inject constructor(
    private val repository: QuizModeRepository,
    dispatcher: CoroutineDispatcher
) : BasePresenter<QuizQuestionsContract.View>(), QuizQuestionsContract.Presenter {
    private val presenterScope = CoroutineScope(SupervisorJob() + dispatcher)

    private val searchQuery = MutableStateFlow("")
    private val sortOption = MutableStateFlow<QuestionSort.SortOptions>(QuestionSort.defaultSortOption)
    private val sortType = MutableStateFlow<QuestionSort.SortTypes>(QuestionSort.defaultSortType)
    private val filterType = MutableStateFlow<QuestionFilter>(QuestionFilter.defaultFilter)

    override fun onViewCreated() {
        super.onViewCreated()

        presenterScope.launch {
            val combinedData = combine(
                repository.getAllQuestions(),
                repository.getUpdatedQuestions()
            ) { response, questions ->
                when (response) {
                    is Response.Success -> Response.Success(questions)
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
            ) { response, query, sortOption, sortType, filterType ->
                when (response) {
                    is Response.Success -> {
                        var questions = response.data.filter { it.text.contains(query, ignoreCase = true) }

                        questions = when (sortOption) {
                            QuestionSort.SortOptions.ByDate -> questions.sortedBy { it.creationDate }
                            QuestionSort.SortOptions.ByAnswersAmount -> questions.sortedBy { it.answers.count() }
                            QuestionSort.SortOptions.ByTitle -> questions.sortedBy { it.text }
                        }
                        if(sortType == QuestionSort.SortTypes.Descending) questions = questions.reversed()

                        questions = when(filterType){
                            QuestionFilter.None -> questions
                            QuestionFilter.WithAnswers -> questions.filter { it.answers.isNotEmpty() }
                            QuestionFilter.WithCategories -> questions.filter { it.linkedCategories.isNotEmpty() }
                            QuestionFilter.WithCorrectAnswers -> questions.filter { it.answers.any { it.isCorrect } }
                            QuestionFilter.WithoutAnswers -> questions.filter { it.answers.isEmpty()}
                            QuestionFilter.WithoutCategories -> questions.filter { it.linkedCategories.isEmpty() }
                            QuestionFilter.WithoutCorrectAnswers -> questions.filter { it.answers.any { it.isCorrect.not() } || it.answers.isEmpty() }
                        }
                        Response.Success(questions)
                    }

                    is Response.Error -> response

                    is Response.Loading -> Response.Loading

                }
            }.collect { filteredResponse ->
                when (filteredResponse) {
                    is Response.Success -> displayQuestionsList(filteredResponse.data)
                    is Response.Error -> view.showError(filteredResponse.error)
                    is Response.Loading -> view.showLoading()
                }
            }
        }
    }

    private fun displayQuestionsList(questions: List<Question>) {
        view.displayQuestions(questions.map { it.toUIModel(getCategoryForQuestion(it.linkedCategories)) })
    }

    private fun getCategoryForQuestion(categoryIds: List<Long>): List<SimpleCategoryUIModel> {
        val simpleCategoriesList = mutableListOf<SimpleCategoryUIModel>()
        categoryIds.forEach { id ->
            val response = repository.getCategoryById(id)
            if(response is Response.Success) simpleCategoriesList.add(response.data.toSimplePresentation())
        }
        return simpleCategoriesList
    }

    override fun removeQuestion(question: QuestionUIModel) {
        presenterScope.launch {
            val response = repository.deleteQuestion(question.id)
            if (response is Response.Error) view.showError(response.error)
        }
    }

    override fun searchBy(query: String) {
        searchQuery.value = query
    }

    override fun onSortMenuOpened() {
        view.displaySortMenu(
            sortOptions = QuestionSort.getSortOptions().map { it.toSelectableMenuItem(sortOption.value == it) },
            sortTypes = QuestionSort.getSortTypes().map { it.toSelectableMenuItem(sortOption.value == it) }
        )
    }

    override fun onFilterMenuOpened() {
        view.displayFilterMenu(
            filterOptions = QuestionFilter.getFilters().map { it.toSelectableMenuItem(filterType.value == it) }
        )
    }

    override fun sortByOption(sort: SelectableMenuItem) {
        sortOption.value = sort.toSortOption() ?: QuestionSort.defaultSortOption
    }

    override fun sortByType(sort: SelectableMenuItem) {
        sortType.value = sort.toSortType() ?: QuestionSort.defaultSortType
    }

    override fun filterBy(filter: SelectableMenuItem) {
        filterType.value = filter.toFilterOption() ?: QuestionFilter.defaultFilter
    }

    override fun onDestroy() {
        presenterScope.cancel()
        super.onDestroy()
    }
}