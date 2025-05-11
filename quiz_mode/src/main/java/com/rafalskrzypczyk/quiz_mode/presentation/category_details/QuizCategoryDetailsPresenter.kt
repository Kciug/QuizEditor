package com.rafalskrzypczyk.quiz_mode.presentation.category_details

import android.os.Bundle
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.domain.QuizCategoryDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.CategoryStatus
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.ui_models.CategoryFilters.Companion.toSelectableMenuItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizCategoryDetailsPresenter @Inject constructor(
    private val interactor: QuizCategoryDetailsInteractor,
    private val resourceProvider: ResourceProvider
) : BasePresenter<QuizCategoryDetailsContract.View>(), QuizCategoryDetailsContract.Presenter {
    private var isDataLoaded = false

    private var initialCategoryTitle: String = ""

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

        presenterScope?.launch {
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
        presenterScope?.launch {
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
            displayCreatedDetails(String.formatDate(category.creationDate))
            displayCategoryColor(category.color)
            displayCategoryStatus(category.status)
            displayQuestionCount(category.linkedQuestions.count())
        }
        updateQuestionList()

        initialCategoryTitle = category.title
    }

    override fun createNewCategory(categoryTitle: String) {
        if(categoryTitle.isEmpty()){
            view.displayToastMessage(resourceProvider.getString(R.string.warning_empty_category_text))
            return
        }
        presenterScope?.launch {
            handleCategoryResponse(interactor.instantiateNewCategory(categoryTitle))
        }
    }

    override fun updateCategoryTitle(categoryTitle: String) {
        if(categoryTitle.isEmpty())
            view.displayToastMessage(resourceProvider.getString(R.string.warning_empty_category_text))

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
        presenterScope?.launch {
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

    override fun onCategoryQuestions() {
        view.displayCategoryQuestionsList(
            interactor.getCategoryId(),
            initialCategoryTitle
        )
    }

    override fun onDestroy() {
        interactor.saveCachedCategory()
        super.onDestroy()
    }
}