package com.rafalskrzypczyk.quiz_mode.presentation.categeory_details

import android.os.Bundle
import android.util.Log
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.di.MainDispatcher
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.domain.CategoryStatus
import com.rafalskrzypczyk.quiz_mode.domain.QuizCategoryDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.CategoryFilters.Companion.toSelectableMenuItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizCategoryDetailsPresenter @Inject constructor(
    private val interactor: QuizCategoryDetailsInteractor,
    @MainDispatcher dispatcher: CoroutineDispatcher,
) : BasePresenter<QuizCategoryDetailsContract.View>(), QuizCategoryDetailsContract.Presenter {
    private val presenterScope = CoroutineScope(SupervisorJob() + dispatcher)

    private var isDataLoaded = false

    override fun getData(bundle: Bundle?) {
        val categoryId = bundle?.getLong("categoryId")

        if (categoryId == null) {
            view.setupNewElementView()
            return
        }

        presenterScope.launch {
            combine(
                interactor.getCategory(categoryId),
                interactor.getUpdatedCategory()
            ) { categoryResponse, updatedCategory ->
                when (categoryResponse) {
                    is Response.Success -> Response.Success(updatedCategory)
                    is Response.Error -> categoryResponse
                    is Response.Loading -> categoryResponse
                }
            }.collectLatest { displayCategoryData(it) }
        }
    }

    private fun displayCategoryData(response: Response<Category>) {
        when (response) {
            is Response.Success -> {
                isDataLoaded = true
                updateUI(response.data)
            }

            is Response.Error -> {
                view.showError(response.error)
            }

            is Response.Loading -> {
                view.showLoading()
            }
        }
    }

    private fun updateUI(category: Category) {
        view.setupView()
        view.displayCategoryDetails(category.title, category.description)
        view.displayCategoryColor(category.color)
        view.displayCategoryStatus(category.status)
        view.displayQuestionCount(category.linkedQuestions.count())
        updateQuestionList()
    }

    override fun createNewCategory(categoryTitle: String) {
        presenterScope.launch {
            displayCategoryData(interactor.instantiateNewCategory(categoryTitle))
        }
    }

    override fun updateCategoryTitle(categoryTitle: String) {
        interactor.updateCategoryTitle(categoryTitle)
    }

    override fun updateCategoryDescription(categoryDescription: String) {
        interactor.updateCategoryDescription(categoryDescription)
    }

    override fun onChangeColor() {
        view.displayColorPicker(interactor.getCategoryColor())
    }

    override fun updateCategoryColor(color: Int) {
        interactor.updateColor(color)
        view.displayCategoryColor(color)
    }

    override fun onChangeCategoryStatus() {
        view.displayCategoryStatusMenu(
            CategoryStatus.entries
                .map { it.toSelectableMenuItem(it == interactor.getCategoryStatus()) }
        )
    }

    override fun updateCategoryStatus(status: SelectableMenuItem) {
        val newStatus = CategoryStatus.entries.find { it.hashCode() == status.itemHashCode }
        newStatus?.let {
            interactor.updateStatus(newStatus)
            view.displayCategoryStatus(newStatus)
        }
    }

    override fun updateQuestionList() {
        presenterScope.launch {
            interactor.getLinkedQuestions().collectLatest {
                when (it) {
                    is Response.Success -> {
                        view.displayQuestionList(it.data)
                        view.displayQuestionCount(interactor.getLinkedQuestionsAmount())
                    }

                    is Response.Error -> view.showError(it.error)
                    is Response.Loading -> view.showLoading()
                }
            }
        }
    }

    override fun onQuestionFromList() {
        view.displayQuestionsPicker()
    }

    override fun onNewQuestion() {
        Log.d("KURWA", "QuizCategoryDetailsPresenter:onNewQuestion: categoryId: ${interactor.getCategoryId()}")
        view.displayNewQuestionSheet(interactor.getCategoryId())
    }

    override fun onDestroy() {
        super.onDestroy()
        interactor.saveCachedCategory()
        presenterScope.cancel()
    }
}