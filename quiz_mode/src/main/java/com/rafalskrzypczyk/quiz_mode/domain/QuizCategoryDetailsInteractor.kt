package com.rafalskrzypczyk.quiz_mode.domain

import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.utils.CategoryStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizCategoryDetailsInteractor @Inject constructor(
    private val repository: QuizModeRepository,
    private val resourceProvider: ResourceProvider
) : CheckablePickerInteractorContract {
    private var cachedCategory: Category? = null

    suspend fun getCategory(categoryId: Int): Response<Category> {
        var response: Response<Category> = Response.Loading
        repository.getCategoryById(categoryId).collectLatest {
            when (it) {
                is Response.Success -> {
                    cachedCategory = it.data
                    response = Response.Success(it.data)
                }

                is Response.Error -> {
                    response = Response.Error(it.error)
                }

                is Response.Loading -> {
                    response = Response.Loading
                }
            }
        }
        return response
    }

    suspend fun instantiateNewCategory(categoryTitle: String): Response<Category> {
        val newCategory = Category.new(categoryTitle)
        newCategory.color = resourceProvider.getColor(R.color.colorable_default_color)
        val response = repository.addCategory(newCategory)
        return when (response) {
            is Response.Success -> {
                cachedCategory = newCategory
                Response.Success(newCategory)
            }

            is Response.Error -> {
                Response.Error(response.error)
            }

            is Response.Loading -> {
                Response.Loading
            }
        }
    }

    fun updateCategoryTitle(title: String) {
        cachedCategory?.title = title
    }

    fun updateCategoryDescription(description: String) {
        cachedCategory?.description = description
    }

    fun updateColor(color: Int) {
        cachedCategory?.color = color
    }

    fun getAvailableStatuses() : List<CategoryStatus> {
        // TODO - add user role validation for all statuses
        return CategoryStatus.entries
    }

    fun updateStatus(status: CategoryStatus) {
        cachedCategory?.status = status
    }

    suspend fun getLinkedQuestions(): List<Question> {
        var linkedQuestionsToDisplay = listOf<Question>()
        repository.getAllQuestions()
            .filter { it is Response.Success }
            .map { (it as Response.Success).data }
            .collectLatest { questions ->
                linkedQuestionsToDisplay =
                    questions.filter { cachedCategory?.linkedQuestions?.contains(it.id) == true }
            }
        return linkedQuestionsToDisplay
    }

    fun getLinkedQuestionsAmount() : Int = cachedCategory?.linkedQuestions?.count() ?: 0

    suspend fun saveCachedCategory() {
        cachedCategory?.let { repository.updateCategory(it) }
    }

    override fun getItemList(): Flow<List<Checkable>> {
        return repository.getAllQuestions().filter {
            it is Response.Success
        }.map {
            (it as Response.Success).data.map { question ->
                Checkable(
                    id = question.id,
                    title = question.text,
                    isChecked = cachedCategory?.linkedQuestions?.contains(question.id) == true,
                    isLocked = false
                )
            }
        }
    }

    override fun onItemSelected(selectedItem: Checkable) {
        repository.bindQuestionWithCategory(selectedItem.id, cachedCategory?.id ?: -1)
    }

    override fun onItemDeselected(deselectedItem: Checkable) {
        repository.unbindQuestionWithCategory(deselectedItem.id, cachedCategory?.id ?: -1)
    }

    fun getCategoryId(): Int = cachedCategory?.id ?: -1

    fun getCategoryColor(): Int = cachedCategory?.color ?: 0

    fun getCategoryStatus(): CategoryStatus = cachedCategory?.status ?: CategoryStatus.DRAFT
}