package com.rafalskrzypczyk.quiz_mode.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.quiz_mode.domain.models.Answer
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizQuestionDetailsInteractor @Inject constructor(
    private val repository: QuizModeRepository,
    private val dataUpdateManager: DataUpdateManager
) : CheckablePickerInteractor {
    private var cachedQuestion: Question? = null
    private var parentCategoryId: Long? = null

    fun getQuestion(questionId: Long): Flow<Response<Question>> = repository.getQuestionById(questionId).map {
        when (it) {
            is Response.Success -> {
                cachedQuestion = it.data
                Response.Success(cachedQuestion!!)
            }
            is Response.Error -> Response.Error(it.error)
            is Response.Loading -> Response.Loading
        }
    }

    fun getUpdatedQuestion(): Flow<Question?> = repository.getUpdatedQuestions().map {
        it.find { it.id == cachedQuestion?.id }?.let { cachedQuestion = it }
        cachedQuestion
    }

    suspend fun instantiateNewQuestion(questionText: String): Response<Question> {
        val newQuestion = Question.new(questionText)
        val response = repository.addQuestion(newQuestion)
        return when (response) {
            is Response.Success -> {
                cachedQuestion = newQuestion
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

    fun bindWithParentCategory() {
        if(cachedQuestion == null || parentCategoryId == null) return
        dataUpdateManager.bindQuestionWithCategory(
            questionId = cachedQuestion?.id ?: -1,
            categoryId = parentCategoryId ?: -1
        )
    }

    fun setParentCategoryId(categoryId: Long) {
        parentCategoryId = categoryId
    }

    fun updateQuestionText(text: String) {
        cachedQuestion?.text = text
    }

    fun addAnswer(text: String) {
        cachedQuestion?.answers?.add(Answer.new(text))
    }

    fun updateAnswer(answerId: Long, answerText: String, answerIsCorrect: Boolean) {
        cachedQuestion?.answers?.find { it.id == answerId }
            ?.let { it.answerText = answerText; it.isCorrect = answerIsCorrect }
    }

    fun removeAnswer(answerId: Long) {
        val answerToRemove = cachedQuestion?.answers?.find { it.id == answerId } ?: return
        cachedQuestion?.answers?.remove(answerToRemove)
    }

    fun getLinkedCategories(): Flow<Response<List<Category>>> {
        return repository.getAllCategories()
            .map {
                when (it) {
                    is Response.Success -> Response.Success(it.data.filter { cachedQuestion?.linkedCategories?.contains(it.id) == true })
                    is Response.Error -> Response.Error(it.error)
                    is Response.Loading -> Response.Loading
                }
            }
    }

    fun answerCount() = cachedQuestion?.answers?.count() ?: 0

    fun correctAnswerCount() = cachedQuestion?.answers?.count { it.isCorrect } ?: 0

    fun getAnswers() = cachedQuestion?.answers ?: emptyList()

    fun saveCachedQuestion() {
        cachedQuestion?.let { dataUpdateManager.updateQuestion(cachedQuestion!!) }
    }

    override fun getItemList(): Flow<Response<List<Checkable>>> {
        return repository.getAllCategories().map {
            when (it) {
                is Response.Success -> Response.Success(it.data.map { Checkable(
                    id = it.id,
                    title = it.title,
                    isChecked = cachedQuestion?.linkedCategories?.contains(it.id) == true,
                    isLocked = parentCategoryId?.equals(it.id) == true,
                ) })
                is Response.Error -> Response.Error(it.error)
                is Response.Loading -> Response.Loading
            }
        }
    }

    override fun onItemSelected(selectedItem: Checkable) {
        dataUpdateManager.bindQuestionWithCategory(
            questionId = cachedQuestion?.id ?: -1,
            categoryId = selectedItem.id
        )
    }

    override fun onItemDeselected(deselectedItem: Checkable) {
        dataUpdateManager.unbindQuestionWithCategory(
            questionId = cachedQuestion?.id ?: -1,
            categoryId = deselectedItem.id
        )
    }
}