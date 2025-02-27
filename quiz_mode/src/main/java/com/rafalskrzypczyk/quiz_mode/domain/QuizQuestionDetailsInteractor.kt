package com.rafalskrzypczyk.quiz_mode.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.extensions.updateById
import com.rafalskrzypczyk.quiz_mode.domain.models.Answer
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizQuestionDetailsInteractor @Inject constructor(
    private val repository: QuizModeRepository
) : CheckablePickerInteractorContract {
    private var cachedQuestion: Question? = null
    private var parentCategoryId: Int? = null

    suspend fun getQuestion(questionId: Int): Response<Question> {
        var response: Response<Question> = Response.Loading
        repository.getQuestionById(questionId).collectLatest {
            when (it) {
                is Response.Success -> {
                    cachedQuestion = it.data
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

    suspend fun instantiateNewQuestion(questionText: String): Response<Question> {
        val newQuestion = Question.new(questionText)
        val response = repository.saveQuestion(newQuestion)
        return when (response) {
            is Response.Success -> {
                cachedQuestion = newQuestion
                if(parentCategoryId != null) {
                    repository.bindQuestionWithCategory(newQuestion.id, parentCategoryId!!)
                }
                Response.Success(newQuestion)
            }

            is Response.Error -> {
                Response.Error(response.error)
            }

            is Response.Loading -> {
                Response.Loading
            }
        }
    }

    fun setParentCategoryId(categoryId: Int) {
        parentCategoryId = categoryId
    }

    fun updateQuestionText(text: String) {
        cachedQuestion?.text = text
    }

    fun addAnswer(text: String) {
        cachedQuestion?.answers?.add(Answer.new(text))
    }

    fun updateAnswer(answer: Answer) {
        cachedQuestion?.answers?.updateById(answer)
    }

    fun removeAnswer(answer: Answer) {
        cachedQuestion?.answers?.remove(answer)
    }

    suspend fun getLinkedCategories(): List<Category> {
        var linkedCategoriesToDisplay = listOf<Category>()
        repository.getAllCategories()
            .filter { it is Response.Success }
            .map { (it as Response.Success).data }
            .collectLatest { categories ->
                linkedCategoriesToDisplay =
                    categories.filter { cachedQuestion?.linkedCategories?.contains(it.id) == true }
            }
        return linkedCategoriesToDisplay
    }

    fun answerCount() = cachedQuestion?.answers?.count() ?: 0
    fun correctAnswerCount() = cachedQuestion?.answers?.count { it.isCorrect } ?: 0
    fun getLastAnswer(): Answer = cachedQuestion!!.answers.last()

    suspend fun saveCachedQuestion() {
        cachedQuestion?.let { repository.updateQuestion(it) }
    }

    override fun getItemList(): Flow<List<Checkable>> {
        return repository.getAllCategories().filter {
            it is Response.Success
        }.map {
            (it as Response.Success).data.map { category ->
                Checkable(
                    id = category.id,
                    title = category.title,
                    isChecked = cachedQuestion?.linkedCategories?.contains(category.id) == true,
                    isLocked = parentCategoryId == category.id
                )
            }
        }
    }

    override fun onItemSelected(selectedItem: Checkable) {
        repository.bindQuestionWithCategory(cachedQuestion?.id ?: -1, selectedItem.id)
    }

    override fun onItemDeselected(deselectedItem: Checkable) {
        repository.unbindQuestionWithCategory(cachedQuestion?.id ?: -1, deselectedItem.id)
    }
}