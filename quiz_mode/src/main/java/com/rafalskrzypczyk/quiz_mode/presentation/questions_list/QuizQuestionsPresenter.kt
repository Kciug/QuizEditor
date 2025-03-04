package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.util.Log
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.toSimplePresentation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizQuestionsPresenter @Inject constructor(
    private val view: QuizQuestionsContract.View,
    private val repository: QuizModeRepository
) : BasePresenter(), QuizQuestionsContract.Presenter {
    private val searchQuery = MutableStateFlow("")
    private val sortOption = MutableStateFlow<QuestionSort.SortOptions>(QuestionSort.defaultSortOption)
    private val sortType = MutableStateFlow<QuestionSort.SortTypes>(QuestionSort.defaultSortType)
    private val filterType = MutableStateFlow<QuestionFilter>(QuestionFilter.defaultFilter)

    override fun loadQuestions() {
        presenterScope.launch {
            combine(
                repository.getAllQuestions(),
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

                    is Response.Error -> {
                        response
                    }
                    is Response.Loading -> {
                        Response.Loading
                    }
                }
            }.collect { filteredResponse ->
                when (filteredResponse) {
                    is Response.Success -> displayQuestionsList(filteredResponse.data)
                    is Response.Error -> Log.e(
                        "QuizQuestionsPresenter",
                        "Error: ${filteredResponse.error}"
                    )

                    is Response.Loading -> Log.d("QuizQuestionsPresenter", "Loading")
                }
            }
        }
    }

    private fun displayQuestionsList(questions: List<Question>) {
        view.displayQuestions(questions.map { it.toUIModel(getCategoryForQuestion(it.linkedCategories)) })
    }

    private fun getCategoryForQuestion(categoryIds: List<Int>): List<SimpleCategoryUIModel> {
        val simpleCategoriesList = mutableListOf<SimpleCategoryUIModel>()
        presenterScope.launch {
            categoryIds.forEach { id ->
                repository.getCategoryById(id).collect { response ->
                    if (response is Response.Success) {
                        simpleCategoriesList.add(response.data.toSimplePresentation())
                    }
                }
            }
        }
        return simpleCategoriesList
    }

    override fun removeQuestion(question: QuestionUIModel) {
        presenterScope.launch {
            val response = repository.deleteQuestion(question.id)
            if (response is Response.Error) Log.e(
                "QuizQuestionsPresenter",
                "Error: ${response.error}"
            )
            loadQuestions()
        }
    }

    override fun searchBy(query: String) {
        searchQuery.value = query
    }

    override fun sortByOption(sort: QuestionSort.SortOptions) {
        sortOption.value = sort
    }

    override fun sortByType(sort: QuestionSort.SortTypes) {
        sortType.value = sort
    }

    override fun filterBy(filter: QuestionFilter) {
        filterType.value = filter
    }

    override fun getCurrentSortOption(): QuestionSort.SortOptions = sortOption.value

    override fun getCurrentSortType(): QuestionSort.SortTypes = sortType.value

    override fun getCurrentFilter(): QuestionFilter = filterType.value
}