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

    fun getQuestion(questionId: Long): Flow<Response<Question>> =
        repository.getQuestionById(questionId).map {
            when (it) {
                is Response.Success -> {
                    cachedQuestion = it.data
                    Response.Success(it.data)
                }
                is Response.Error -> it
                is Response.Loading -> it
            }
        }

    fun getUpdatedQuestion(): Flow<Question?> =
        repository.getUpdatedQuestions().map { categories ->
            categories.find { it.id == cachedQuestion?.id }?.also { cachedQuestion = it }
        }

    suspend fun instantiateNewQuestion(questionText: String): Response<Question> {
        val newQuestion = Question.new(questionText)
        return when (val response = repository.addQuestion(newQuestion)) {
            is Response.Success -> {
                cachedQuestion = newQuestion
                bindWithParentCategory()
                Response.Success(newQuestion)
            }
            is Response.Error -> response
            is Response.Loading -> response
        }
    }

    fun bindWithParentCategory() {
        cachedQuestion?.let { question ->
            parentCategoryId?.let { categoryId ->
                dataUpdateManager.bindQuestionWithCategory(question.id, categoryId)
            }
        }
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
        cachedQuestion?.answers?.find { it.id == answerId }?.apply {
            this.answerText = answerText; this.isCorrect = answerIsCorrect
        }
    }

    fun removeAnswer(answerId: Long) {
        cachedQuestion?.answers?.removeIf { it.id == answerId }
    }

    fun getLinkedCategories(): Flow<Response<List<Category>>> =
        repository.getAllCategories().map {
            when (it) {
                is Response.Success -> Response.Success(it.data.filter {
                    cachedQuestion?.linkedCategories?.contains(it.id) == true
                })
                is Response.Error -> it
                is Response.Loading -> it
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
                is Response.Success -> Response.Success(it.data.map {
                    Checkable(
                        id = it.id,
                        title = it.title,
                        isChecked = cachedQuestion?.linkedCategories?.contains(it.id) == true,
                        isLocked = parentCategoryId?.equals(it.id) == true,
                    )
                })
                is Response.Error -> it
                is Response.Loading -> it
            }
        }
    }

    override fun onItemSelected(selectedItem: Checkable) {
        cachedQuestion?.id?.let { questionId ->
            dataUpdateManager.bindQuestionWithCategory(
                questionId = questionId,
                categoryId = selectedItem.id
            )
        }
    }

    override fun onItemDeselected(deselectedItem: Checkable) {
        cachedQuestion?.id?.let { questionId ->
            dataUpdateManager.unbindQuestionWithCategory(
                questionId = questionId,
                categoryId = deselectedItem.id
            )
        }
    }
}