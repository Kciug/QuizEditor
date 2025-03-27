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

    override fun getData(arguments: Bundle?) {
        val questionId = arguments?.getLong("questionId")
        if (questionId == null || questionId == 0.toLong()) {
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
                    }
                    is Response.Error -> view.displayError(it.error)
                    is Response.Loading -> view.displayLoading()
                }
            }
        }
    }

    override fun updateQuestionText(questionText: String) {
        question?.text = questionText
        updateQuestion()
    }

    override fun updateIsCorrect(isCorrect: Boolean?) {
        if(question == null) return
        if(isCorrect == null) {
            view.displayToastMessage(resourceProvider.getString(R.string.warning_question_is_correct_not_set))
            return
        }

        question?.isCorrect = isCorrect
        updateQuestion()
    }

    override fun saveNewQuestion(questionText: String, isCorrect: Boolean?) {
        if(!validateQuestion(questionText, isCorrect)) return
        if(question != null) return

        presenterScope?.launch {
            val newQuestion = SwipeQuestion.new(questionText, isCorrect!!)
            var response = repository.addQuestion(newQuestion)
            if(response is Response.Error) view.displayError(response.error)
            if(response is Response.Success) {
                question = newQuestion
                view.displayCreatedDetails(String.formatDate(question!!.dateCreated))
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
            var response = repository.addQuestion(SwipeQuestion.new(questionText, isCorrect!!))
            if(response is Response.Error) view.displayError(response.error)
            if(response is Response.Success) {
                replaceWithNewQuestion()
            }
        }
    }

    private fun replaceWithNewQuestion() {
        question = null
        view.replaceWithNewQuestion()
        view.openKeyboard()
    }

    private fun validateQuestion(questionText: String, isCorrect: Boolean?) : Boolean {
        if(questionText.isEmpty()) {
            view.displayToastMessage(resourceProvider.getString(R.string.warning_question_text_is_empty))
            return false
        }

        if(isCorrect == null) {
            view.displayToastMessage(resourceProvider.getString(R.string.warning_question_is_correct_not_set))
            return false
        }

        return true
    }

    private fun updateQuestion() {
        if(question == null) return
        presenterScope?.launch {
            val response = repository.updateQuestion(question!!)
            if(response is Response.Error) view.displayError(response.error)
        }
    }
}