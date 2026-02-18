package com.rafalskrzypczyk.cem_mode.presentation.category_details

import android.os.Bundle
import com.rafalskrzypczyk.cem_mode.domain.CemModeRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.domain.models.CategoryStatus
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.core.R as coreR
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class CemCategoryDetailsPresenter @Inject constructor(
    private val repository: CemModeRepository,
    private val resourceProvider: ResourceProvider
) : BasePresenter<CemCategoryDetailsContract.View>(), CemCategoryDetailsContract.Presenter {

    private var currentCategory: CemCategory? = null
    private var isDataLoaded = false
    private var parentCategoryID: Long = CemCategory.ROOT_ID

    override fun getData(bundle: Bundle?) {
        val categoryId = bundle?.getLong("categoryId", -1L) ?: -1L
        parentCategoryID = if (bundle?.containsKey("parentCategoryID") == true) {
            bundle.getLong("parentCategoryID")
        } else {
            CemCategory.ROOT_ID
        }

        if (categoryId == -1L) {
            view.setupNewElementView()
            return
        }

        presenterScope?.launch {
            repository.getCategoryById(categoryId).collectLatest { response ->
                when (response) {
                    is Response.Success -> {
                        isDataLoaded = true
                        currentCategory = response.data
                        updateUI(response.data)
                    }
                    is Response.Error -> view.displayError(response.error)
                    is Response.Loading -> view.displayLoading()
                }
            }
        }
    }

    private fun updateUI(category: CemCategory) {
        with(view) {
            setupView()
            displayCategoryDetails(category.title, category.description)
            displayCreatedDetails(String.formatDate(category.creationDate))
            displayCategoryColor(category.color)
            displayCategoryStatus(category.status)
            displayIsFree(category.isFree)
            displayQuestionCount(category.linkedQuestions.count())
            displaySubcategoryCount(category.linkedSubcategories.count())
            displayContent()
        }
    }

    override fun createNewCategory(categoryTitle: String) {
        if (categoryTitle.isEmpty()) {
            view.displayToastMessage("Title cannot be empty")
            return
        }
        val defaultColor = resourceProvider.getColor(coreR.color.primary)
        val parentId = if (parentCategoryID == CemCategory.ROOT_ID) null else parentCategoryID
        val newCategory = CemCategory.new(categoryTitle, defaultColor, parentId)
        
        presenterScope?.launch {
            val result = repository.addCategory(newCategory)
            if (result is Response.Success) {
                isDataLoaded = true
                currentCategory = newCategory
                updateUI(newCategory)
            } else if (result is Response.Error) {
                view.displayError(result.error)
            }
        }
    }

    override fun updateCategoryTitle(categoryTitle: String) {
        currentCategory?.let {
            it.title = categoryTitle
            saveChanges()
        }
    }

    override fun updateCategoryDescription(categoryDescription: String) {
        currentCategory?.let {
            it.description = categoryDescription
            saveChanges()
        }
    }

    override fun onChangeColor() {
        currentCategory?.let { view.displayColorPicker(it.color) }
    }

    override fun updateCategoryColor(color: Int) {
        currentCategory?.let {
            it.color = color
            view.displayCategoryColor(color)
            saveChanges()
        }
    }

    override fun onChangeCategoryStatus() {
        currentCategory?.let { cat ->
            view.displayCategoryStatusMenu(
                CategoryStatus.entries.map { 
                    SelectableMenuItem(it.hashCode(), it.title, it == cat.status)
                }
            )
        }
    }

    override fun updateCategoryStatus(status: SelectableMenuItem) {
        val newStatus = CategoryStatus.entries.find { it.hashCode() == status.itemHashCode }
        if (newStatus != null && currentCategory != null) {
            currentCategory!!.status = newStatus
            view.displayCategoryStatus(newStatus)
            saveChanges()
        }
    }

    override fun updateIsFree(isFree: Boolean) {
        currentCategory?.let {
            it.isFree = isFree
            saveChanges()
        }
    }

    override fun onCategorySubcategories() {
        currentCategory?.let { view.displaySubcategoriesList(it.id) }
    }

    override fun onCategoryQuestions() {
        currentCategory?.let { view.displayQuestionsList(it.id, it.title, it.color.toLong()) }
    }

    override fun onNewQuestion() {
        currentCategory?.let { view.displayNewQuestionSheet(it.id) }
    }

    override fun onNewSubcategory() {
        currentCategory?.let { view.displayNewSubcategorySheet(it.id) }
    }

    private fun saveChanges() {
        currentCategory?.let {
            presenterScope?.launch {
                repository.updateCategory(it)
            }
        }
    }
}
