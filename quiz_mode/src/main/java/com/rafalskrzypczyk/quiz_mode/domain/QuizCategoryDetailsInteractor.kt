package com.rafalskrzypczyk.quiz_mode.domain

import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizCategoryDetailsInteractor @Inject constructor(
    private val repository: QuizModeRepository,
    private val resourceProvider: ResourceProvider,
) : CheckablePickerInteractor {
    private var cachedCategory: Category? = null

    fun getCategory(categoryId: Long): Flow<Response<Category>> = flow {
        val fetchedCategory = repository.getCategoryById(categoryId)
        if (fetchedCategory is Response.Success) cachedCategory = fetchedCategory.data
        emit(fetchedCategory)
    }

    suspend fun instantiateNewCategory(categoryTitle: String): Response<Category> {
        val newCategory =
            Category.new(categoryTitle, resourceProvider.getColor(R.color.colorable_default_color))
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

    fun updateStatus(status: CategoryStatus) {
        cachedCategory?.status = status
    }

    fun getLinkedQuestions(): Flow<Response<List<Question>>> {
        return repository.getAllQuestions()
            .map {
                when (it) {
                    is Response.Success -> Response.Success(it.data.filter {
                        cachedCategory?.linkedQuestions?.contains(it.id) == true })

                    is Response.Error -> Response.Error(it.error)
                    is Response.Loading -> Response.Loading
                }
            }
    }

    fun getLinkedQuestionsAmount(): Int = cachedCategory?.linkedQuestions?.count() ?: 0

    suspend fun saveCachedCategory() {
        cachedCategory?.let { repository.updateCategory(it) }
    }

    fun getCategoryId(): Long = cachedCategory?.id ?: -1

    fun getCategoryColor(): Int = cachedCategory?.color ?: 0

    fun getCategoryStatus(): CategoryStatus = cachedCategory?.status ?: CategoryStatus.DRAFT

    override fun getItemList(): Flow<Response<List<Checkable>>> {
        return repository.getAllQuestions().map {
            when (it) {
                is Response.Success -> Response.Success(it.data.map { Checkable(
                    id = it.id,
                    title = it.text,
                    isChecked = cachedCategory?.linkedQuestions?.contains(it.id) == true,
                    isLocked = false,
                ) })
                is Response.Error -> Response.Error(it.error)
                is Response.Loading -> Response.Loading
            }
        }
    }

    override suspend fun onItemSelected(selectedItem: Checkable) {
        repository.bindQuestionWithCategory(
            questionId = selectedItem.id,
            categoryId = cachedCategory?.id ?: -1
        )
    }

    override suspend fun onItemDeselected(deselectedItem: Checkable) {
        repository.unbindQuestionWithCategory(
            questionId = deselectedItem.id,
            categoryId = cachedCategory?.id ?: -1
        )
    }
}