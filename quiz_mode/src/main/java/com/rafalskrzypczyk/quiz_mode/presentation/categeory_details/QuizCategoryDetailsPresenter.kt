package com.rafalskrzypczyk.quiz_mode.presentation.categeory_details

import android.os.Bundle
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.di.MainDispatcher
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.domain.QuizCategoryDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.CategoryStatus
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.CategoryFilters.Companion.toSelectableMenuItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizCategoryDetailsPresenter @Inject constructor(
    private val interactor: QuizCategoryDetailsInteractor,
    @MainDispatcher dispatcher: CoroutineDispatcher,
) : BasePresenter<QuizCategoryDetailsContract.View>(), QuizCategoryDetailsContract.Presenter {
    private val presenterScope = CoroutineScope(SupervisorJob() + dispatcher)
    private var isDataLoaded = false

    override fun onViewCreated() {
        super.onViewCreated()
        attachChangeListener()
    }

    override fun getData(bundle: Bundle?) {
        val categoryId = bundle?.getLong("categoryId")
        if (categoryId == null) {
            view.setupNewElementView()
            return
        }

        presenterScope.launch {
            interactor.getCategory(categoryId).collectLatest { handleCategoryResponse(it) }
        }
    }

    private fun handleCategoryResponse(response: Response<Category>) {
        when (response) {
            is Response.Success -> {
                isDataLoaded = true
                updateUI(response.data)
            }

            is Response.Error -> view.displayError(response.error)
            is Response.Loading -> view.displayLoading()
        }
    }

    private fun attachChangeListener(){
        presenterScope.launch {
            interactor.getUpdatedCategory().collectLatest {
                it?.let { updateUI(it) }
            }
        }
    }

    private fun updateUI(category: Category) {
        if (!isDataLoaded) return

        with(view){
            setupView()
            displayCategoryDetails(category.title, category.description)
            displayCategoryColor(category.color)
            displayCategoryStatus(category.status)
            displayQuestionCount(category.linkedQuestions.count())
        }
        updateQuestionList()
    }

    override fun createNewCategory(categoryTitle: String) {
        presenterScope.launch {
            handleCategoryResponse(interactor.instantiateNewCategory(categoryTitle))
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
        CategoryStatus.entries.find { it.hashCode() == status.itemHashCode }?.let {
            interactor.updateStatus(it)
            view.displayCategoryStatus(it)
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
                    is Response.Error -> view.displayError(it.error)
                    is Response.Loading -> view.displayQuestionListLoading()
                }
            }
        }
    }

    override fun onQuestionFromList() {
        view.displayQuestionsPicker()
    }

    override fun onNewQuestion() {
        view.displayNewQuestionSheet(interactor.getCategoryId())
    }

    override fun onDestroy() {
        interactor.saveCachedCategory()
        presenterScope.cancel()
        super.onDestroy()
    }
}