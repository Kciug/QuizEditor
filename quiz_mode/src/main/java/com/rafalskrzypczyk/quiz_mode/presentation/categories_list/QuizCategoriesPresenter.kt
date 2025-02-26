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

    override fun loadCategories() {
        presenterScope.launch {
            combine(
                repository.getAllCategories(),
                searchQuery
            ) { response, query ->
                when (response) {
                    is Response.Success -> {
                        Response.Success(response.data.filter { it.title.contains(query, ignoreCase = true) })
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
//        presenterScope.launch {
//            val response = repository.deleteCategory(category)
//            if (response is Response.Error) Log.e(
//                "QuizQuestionsPresenter",
//                "Error: ${response.error}"
//            )
//            loadCategories()
//        }
    }

    override fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}