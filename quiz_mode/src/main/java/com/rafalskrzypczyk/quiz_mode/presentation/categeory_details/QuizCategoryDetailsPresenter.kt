package com.rafalskrzypczyk.quiz_mode.presentation.categeory_details

import android.os.Bundle
import android.util.Log
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.quiz_mode.domain.QuizCategoryDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.utils.CategoryStatus
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizCategoryDetailsPresenter @Inject constructor(
    private val view: QuizCategoryDetailsContract.View,
    private val interactor: QuizCategoryDetailsInteractor
) : BasePresenter(), QuizCategoryDetailsContract.Presenter {
    private var isDataLoaded = false

    override fun getData(bundle: Bundle?) {
        val categoryId = bundle?.getInt("categoryId")

        if (categoryId == null) {
            view.setupNewElementView()
            return
        }

        presenterScope.launch { displayCategoryData(interactor.getCategory(categoryId)) }
    }

    private fun displayCategoryData(response: Response<Category>) {
        when (response) {
            is Response.Success -> {
                isDataLoaded = true
                updateUI(response.data)
            }

            is Response.Error -> {
                Log.e("QuizCategoryDetailsPresenter", "Error: ${response.error}")
            }

            is Response.Loading -> {
                Log.d("QuizCategoryDetailsPresenter", "Loading")
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
        presenterScope.launch { displayCategoryData(interactor.instantiateNewCategory(categoryTitle)) }
    }

    override fun updateCategoryTitle(categoryTitle: String) {
        interactor.updateCategoryTitle(categoryTitle)
    }

    override fun updateCategoryDescription(categoryDescription: String) {
        interactor.updateCategoryDescription(categoryDescription)
    }

    override fun updateCategoryColor(color: Int) {
        interactor.updateColor(color)
        view.displayCategoryColor(color)
    }

    override fun onChangeCategoryStatusClicked() {
        view.displayCategoryStatusMenu(interactor.getAvailableStatuses())
    }

    override fun updateCategoryStatus(status: CategoryStatus) {
        interactor.updateStatus(status)
        view.displayCategoryStatus(interactor.getCategoryStatus())
    }

    override fun updateQuestionList() {
        presenterScope.launch {
            view.displayQuestionList(interactor.getLinkedQuestions())
            view.displayQuestionCount(interactor.getLinkedQuestionsAmount())
        }
    }

    override fun getCategoryId(): Int = interactor.getCategoryId()

    override fun getCategoryColor(): Int = interactor.getCategoryColor()

    override fun saveUpdatedData() {
        presenterScope.launch{ interactor.saveCachedCategory() }
    }
}