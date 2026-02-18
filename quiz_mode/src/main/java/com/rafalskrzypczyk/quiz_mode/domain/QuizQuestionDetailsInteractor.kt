package com.rafalskrzypczyk.quiz_mode.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.domain.models.Answer
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizQuestionDetailsInteractor @Inject constructor(
    private val repository: QuizModeRepository,
    private val dataUpdateManager: DataUpdateManager,
    private val resourceProvider: ResourceProvider
) : CheckablePickerInteractor {
    private lateinit var questionInitialState: Question
    private lateinit var answersInitialState: List<Answer>
    private var questionReference: Question? = null
    private var parentCategoryId: Long? = null

    private val answersBuffer: MutableList<Answer> = mutableListOf()

    fun getQuestion(questionId: Long): Flow<Response<Question>> =
        repository.getQuestionById(questionId).map {
            if(it is Response.Success) {
                questionReference = it.data
                updateInitialState(it.data)
                Response.Success(it.data)
            } else it
        }

    fun getUpdatedQuestion(): Flow<Question?> =
        repository.getUpdatedQuestions().map { categories ->
            categories.find { it.id == questionReference?.id }?.also {
                questionReference = it
                updateInitialState(it)
            }
        }

    private fun updateInitialState(question: Question) {
        questionInitialState = question.copy()
        answersInitialState = question.answers.map { it.copy() }
    }

    suspend fun instantiateNewQuestion(questionText: String): Response<Question> {
        val newQuestion = Question.new(questionText)
        return when (val response = repository.addQuestion(newQuestion)) {
            is Response.Success -> {
                questionReference = newQuestion
                updateInitialState(newQuestion)
                bindWithParentCategory()
                Response.Success(newQuestion)
            }
            is Response.Error -> response
            is Response.Loading -> response
        }
    }

    fun bindWithParentCategory() {
        questionReference?.let { question ->
            parentCategoryId?.let { categoryId ->
                dataUpdateManager.bindQuestionWithCategory(question.id, categoryId)
            }
        }
    }

    fun setParentCategoryId(categoryId: Long) {
        parentCategoryId = categoryId
    }

    fun updateQuestionText(text: String) {
        if(text.isEmpty()) questionReference?.text = questionInitialState.text
        else questionReference?.text = text
    }

    fun updateExplanation(explanation: String) {
        questionReference?.explanation = explanation
    }

    fun addAnswer(text: String) {
        Answer.new(text).let {
            questionReference?.answers?.add(it)
            answersBuffer.add(it.copy())
        }
    }

    fun updateAnswer(answerId: Long, answerText: String, answerIsCorrect: Boolean) {
        questionReference?.answers?.find { it.id == answerId }?.apply {
            this.answerText = if(answerText.isEmpty()) answersInitialState.find { it.id == answerId }?.answerText ?:
                answersBuffer.find { it.id == answerId }?.answerText ?: "" else answerText
            this.isCorrect = answerIsCorrect
        }
    }

    fun removeAnswer(answerId: Long) {
        questionReference?.answers?.removeIf { it.id == answerId }
    }

    fun getLinkedCategories(): Flow<Response<List<Category>>> =
        repository.getAllCategories().map {
            if(it is Response.Success){
                Response.Success(it.data.filter {
                    questionReference?.linkedCategories?.contains(it.id) == true
                })
            }
            else it
        }


    fun answerCount() = questionReference?.answers?.count() ?: 0

    fun correctAnswerCount() = questionReference?.answers?.count { it.isCorrect } ?: 0

    fun getAnswers() = questionReference?.answers ?: emptyList()

    fun saveCachedQuestion() {
        questionReference?.let {
            if(it == questionInitialState) {
                if(answersEqual(it.answers, answersInitialState)) return
            }
        }
        questionReference?.let { dataUpdateManager.updateQuestion(questionReference!!) }
    }

    private fun answersEqual(currentAnswers: List<Answer>, initialAnswers: List<Answer>): Boolean {
        if(currentAnswers.size != initialAnswers.size) return false
        return currentAnswers.zip(initialAnswers).all { (current, initial) ->
            current == initial
        }
    }

    override fun getItemList(): Flow<Response<List<Checkable>>> {
        return repository.getAllCategories().map {
            when (it) {
                is Response.Success -> Response.Success(it.data.map {
                    Checkable(
                        id = it.id,
                        title = it.title,
                        isChecked = questionReference?.linkedCategories?.contains(it.id) == true,
                        isLocked = parentCategoryId?.equals(it.id) == true,
                    )
                })
                is Response.Error -> it
                is Response.Loading -> it
            }
        }
    }

    override fun onItemSelected(selectedItem: Checkable) {
        questionReference?.id?.let { questionId ->
            dataUpdateManager.bindQuestionWithCategory(
                questionId = questionId,
                categoryId = selectedItem.id
            )
        }
    }

    override fun onItemDeselected(deselectedItem: Checkable) {
        questionReference?.id?.let { questionId ->
            dataUpdateManager.unbindQuestionWithCategory(
                questionId = questionId,
                categoryId = deselectedItem.id
            )
        }
    }

    override fun getPickerTitle(): String = resourceProvider.getString(R.string.title_picker_categories)

    override fun getPickerNoItemsMessage(): String = resourceProvider.getString(R.string.picker_no_categories)
}