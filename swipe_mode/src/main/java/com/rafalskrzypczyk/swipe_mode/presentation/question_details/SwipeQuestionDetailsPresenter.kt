package com.rafalskrzypczyk.swipe_mode.presentation.question_details

import android.os.Bundle
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.swipe_mode.R
import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeRepository
import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestion
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class SwipeQuestionDetailsPresenter @Inject constructor(
    private val repository: SwipeModeRepository,
    private val resourceProvider: ResourceProvider
) : BasePresenter<SwipeQuestionDetailsContract.View>(), SwipeQuestionDetailsContract.Presenter {

    private var question: SwipeQuestion? = null

    private var allowChange: Boolean = false

    override fun getData(arguments: Bundle?) {
        val questionId = arguments?.getLong("questionId") ?: 0L
        if (questionId == 0L) {
            view.openKeyboard()
            return
        }

        presenterScope?.launch{
            repository.getQuestionById(questionId).collectLatest {
                when (it) {
                    is Response.Success -> {
                        question = it.data
                        view.displayQuestionDetails(it.data.text, it.data.isCorrect)
                        view.displayCreatedDetails(String.formatDate(it.data.dateCreated))
                        allowChange = true
                    }
                    is Response.Error -> view.displayError(it.error)
                    is Response.Loading -> view.displayLoading()
                }
            }
        }
    }

    override fun updateQuestionText(questionText: String) {
        question?.let {
            if(questionText.isBlank()) {
                view.displayToastMessage(resourceProvider.getString(R.string.warning_question_text_is_empty))
                return
            }
            it.text = questionText
            updateQuestion()
        }
    }

    override fun updateIsCorrect(isCorrect: Boolean?) {
        question?.let {
            if(isCorrect == null) {
                view.displayToastMessage(resourceProvider.getString(R.string.warning_question_is_correct_not_set))
                return
            }
            it.isCorrect = isCorrect
            updateQuestion()
        }
    }

    override fun saveNewQuestion(questionText: String, isCorrect: Boolean?) {
        if(question != null || !validateQuestion(questionText, isCorrect)) return

        presenterScope?.launch {
            val newQuestion = SwipeQuestion.new(questionText, isCorrect!!)
            when(val response = repository.addQuestion(newQuestion)){
                is Response.Success -> view.displayCreatedDetails(String.formatDate(newQuestion.dateCreated))
                is Response.Error -> view.displayError(response.error)
                is Response.Loading -> view.displayLoading()
            }
        }
    }

    override fun saveAndOpenNewQuestion(questionText: String, isCorrect: Boolean?) {
        if(!validateQuestion(questionText, isCorrect)) return

        if(question != null) {
            replaceWithNewQuestion()
            return
        }

        presenterScope?.launch {
            when(val response = repository.addQuestion(SwipeQuestion.new(questionText, isCorrect!!))){
                is Response.Success -> replaceWithNewQuestion()
                is Response.Error -> view.displayError(response.error)
                is Response.Loading -> view.displayLoading()
            }
        }
    }

    private fun replaceWithNewQuestion() {
        question = null
        view.replaceWithNewQuestion()
        view.openKeyboard()
    }

    private fun validateQuestion(questionText: String, isCorrect: Boolean?) : Boolean {
        return when {
            questionText.isBlank() -> {
                view.displayToastMessage(resourceProvider.getString(R.string.warning_question_text_is_empty))
                false
            }
            isCorrect == null -> {
                view.displayToastMessage(resourceProvider.getString(R.string.warning_question_is_correct_not_set))
                false
            }
            else -> true
        }
    }

    private fun updateQuestion() {
        if(!allowChange) return

        presenterScope?.launch {
            repository.updateQuestion(question!!).let {
                if(it is Response.Error) view.displayError(it.error)
            }
        }
    }
}