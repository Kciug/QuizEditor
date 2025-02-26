package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.util.Log
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.QuestionUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.toUIModel
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

    override fun loadQuestions() {
        presenterScope.launch {
            combine(
                repository.getAllQuestions(),
                searchQuery
            ) { response, query ->
                when (response) {
                    is Response.Success -> {
                        Response.Success(response.data.filter {
                            it.text.contains(
                                query,
                                ignoreCase = true
                            )
                        })
                    }

                    is Response.Error -> response
                    is Response.Loading -> Response.Loading
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
//        presenterScope.launch {
//            val response = repository.deleteQuestion(question)
//            if (response is Response.Error) Log.e(
//                "QuizQuestionsPresenter",
//                "Error: ${response.error}"
//            )
//            loadQuestions()
//        }
    }

    override fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}