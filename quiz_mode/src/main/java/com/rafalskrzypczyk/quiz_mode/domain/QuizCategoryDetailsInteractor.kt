package com.rafalskrzypczyk.quiz_mode.domain

import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.CategoryStatus
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
        categoryReference?.title = title
    }

    fun updateCategoryDescription(description: String) {
        categoryReference?.description = description
    }

    fun updateColor(color: Int) {
        categoryReference?.color = color
    }

    fun updateStatus(status: CategoryStatus) {
        categoryReference?.status = status
    }

    fun getLinkedQuestions(): Flow<Response<List<Question>>> =
        repository.getAllQuestions().map {
            when (it) {
                is Response.Success -> Response.Success(it.data.filter {
                    categoryReference?.linkedQuestions?.contains(it.id) == true
                })
                is Response.Error -> Response.Error(it.error)
                is Response.Loading -> Response.Loading
            }
        }

    fun getLinkedQuestionsAmount(): Int = categoryReference?.linkedQuestions?.count() ?: 0

    fun saveCachedCategory() {
        if(categoryReference?.equals(categoryInitialState) == true) return
        categoryReference?.let { dataUpdateManager.updateCategory(categoryReference!!) }
    }

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
}