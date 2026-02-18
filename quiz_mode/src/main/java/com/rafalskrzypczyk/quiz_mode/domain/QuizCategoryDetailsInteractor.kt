package com.rafalskrzypczyk.quiz_mode.domain

import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.domain.models.CategoryStatus
import com.rafalskrzypczyk.core.extensions.formatToDataDate
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class QuizCategoryDetailsInteractor @Inject constructor(
    private val repository: QuizModeRepository,
    private val resourceProvider: ResourceProvider,
    private val dataUpdateManager: DataUpdateManager
) : CheckablePickerInteractor {
    private lateinit var categoryInitialState: Category
    private var categoryReference: Category? = null

    fun getCategory(categoryId: Long): Flow<Response<Category>> =
        repository.getCategoryById(categoryId).map {
            when (it) {
                is Response.Success -> {
                    categoryReference = it.data
                    categoryInitialState = it.data.copy()
                    Response.Success(it.data)
                }
                is Response.Error -> it
                is Response.Loading -> it
            }
        }

    fun getUpdatedCategory(): Flow<Category?> =
        repository.getUpdatedCategories().map { categories ->
            categories.find { it.id == categoryReference?.id }?.also {
                categoryReference = it
                categoryInitialState = it.copy()
            }
        }

    suspend fun instantiateNewCategory(categoryTitle: String): Response<Category> {
        val newCategory = Category.new(categoryTitle, resourceProvider.getColor(R.color.colorable_default_color))
        return when (val response = repository.addCategory(newCategory)) {
            is Response.Success -> {
                categoryReference = newCategory
                categoryInitialState = newCategory.copy()
                Response.Success(newCategory)
            }
            is Response.Error -> response
            is Response.Loading -> response
        }
    }

    fun updateCategoryTitle(title: String) {
        if(title.isEmpty()) categoryReference?.title = categoryInitialState.title
        else categoryReference?.title = title
        categoryReference?.modifiedDate = Date().formatToDataDate()
    }

    fun updateCategoryDescription(description: String) {
        categoryReference?.description = description
        categoryReference?.modifiedDate = Date().formatToDataDate()
    }

    fun updateColor(color: Int) {
        categoryReference?.color = color
        categoryReference?.modifiedDate = Date().formatToDataDate()
    }

    fun updateStatus(status: CategoryStatus) {
        categoryReference?.status = status
        categoryReference?.modifiedDate = Date().formatToDataDate()
    }

    fun updateIsFree(isFree: Boolean) {
        categoryReference?.isFree = isFree
        categoryReference?.modifiedDate = Date().formatToDataDate()
    }

    fun saveCachedCategory() {
        if(categoryReference?.equals(categoryInitialState) == true) return
        categoryReference?.let { dataUpdateManager.updateCategory(categoryReference!!) }
    }

    fun getCategoryName(): String = categoryReference?.title ?: ""

    fun getCategoryId(): Long = categoryReference?.id ?: -1

    fun getCategoryColor(): Int = categoryReference?.color ?: 0

    fun getCategoryStatus(): CategoryStatus = categoryReference?.status ?: CategoryStatus.DRAFT

    override fun getItemList(): Flow<Response<List<Checkable>>> =
        repository.getAllQuestions().map {
            when (it) {
                is Response.Success -> Response.Success(it.data.map {
                    Checkable(
                        id = it.id,
                        title = it.text,
                        isChecked = categoryReference?.linkedQuestions?.contains(it.id) == true,
                        isLocked = false,
                    )
                })
                is Response.Error -> it
                is Response.Loading -> it
            }
        }

    override fun onItemSelected(selectedItem: Checkable) {
        categoryReference?.id?.let { categoryId ->
            dataUpdateManager.bindQuestionWithCategory(
                questionId = selectedItem.id,
                categoryId = categoryId
            )
        }
    }

    override fun onItemDeselected(deselectedItem: Checkable) {
        categoryReference?.id?.let { categoryId ->
            dataUpdateManager.unbindQuestionWithCategory(
                questionId = deselectedItem.id,
                categoryId = categoryId
            )
        }
    }

    override fun getPickerTitle(): String = resourceProvider.getString(com.rafalskrzypczyk.quiz_mode.R.string.title_picker_questions)

    override fun getPickerNoItemsMessage(): String = resourceProvider.getString(com.rafalskrzypczyk.quiz_mode.R.string.picker_no_questions)
}