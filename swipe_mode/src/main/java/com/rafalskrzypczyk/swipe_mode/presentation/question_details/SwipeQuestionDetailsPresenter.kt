package com.rafalskrzypczyk.swipe_mode.presentation.question_details

import android.os.Bundle
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.swipe_mode.R
import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestion
import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestionDetailsInteractor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class SwipeQuestionDetailsPresenter @Inject constructor(
    private val interactor: SwipeQuestionDetailsInteractor,
    private val resourceProvider: ResourceProvider
) : BasePresenter<SwipeQuestionDetailsContract.View>(), SwipeQuestionDetailsContract.Presenter {

    private var isQuestionLoaded = false

    override fun getData(arguments: Bundle?) {
        val questionId = arguments?.getLong("questionId") ?: 0L
        if (questionId == 0L) {
            view.openKeyboard()
            return
        }

        presenterScope?.launch{
            interactor.getQuestion(questionId).collectLatest {
                when (it) {
                    is Response.Success -> {
                        isQuestionLoaded = true
                        view.displayQuestionDetails(it.data.text, it.data.explanation, it.data.isCorrect)
                        view.displayCreatedDetails(String.formatDate(it.data.dateCreated))
                    }
                    is Response.Error -> view.displayError(it.error)
                    is Response.Loading -> view.displayLoading()
                }
            }
        }
    }

    override fun updateQuestionText(questionText: String) {
        if (isQuestionLoaded) {
            if(questionText.isBlank()) {
                view.displayToastMessage(resourceProvider.getString(R.string.warning_question_text_is_empty))
            }
            interactor.updateQuestionText(questionText)
        }
    }

    override fun updateExplanation(explanation: String) {
        if (isQuestionLoaded) {
            interactor.updateExplanation(explanation)
        }
    }

    override fun updateIsCorrect(isCorrect: Boolean?) {
        if (isQuestionLoaded && isCorrect != null) {
            interactor.updateIsCorrect(isCorrect)
        }
    }

    override fun saveNewQuestion(questionText: String, explanation: String, isCorrect: Boolean?) {
        if(isQuestionLoaded || !validateQuestion(questionText, isCorrect)) return

        presenterScope?.launch {
            when(val response = interactor.instantiateNewQuestion(questionText, isCorrect!!, explanation)){
                is Response.Success -> {
                    isQuestionLoaded = true
                    view.displayCreatedDetails(String.formatDate(response.data.dateCreated))
                }
                is Response.Error -> view.displayError(response.error)
                is Response.Loading -> view.displayLoading()
            }
        }
    }

    override fun saveAndOpenNewQuestion(questionText: String, explanation: String, isCorrect: Boolean?) {
        if(!validateQuestion(questionText, isCorrect)) return

        if(isQuestionLoaded) {
            replaceWithNewQuestion()
            return
        }

        presenterScope?.launch {
            when(val response = interactor.instantiateNewQuestion(questionText, isCorrect!!, explanation)){
                is Response.Success -> replaceWithNewQuestion()
                is Response.Error -> view.displayError(response.error)
                is Response.Loading -> view.displayLoading()
            }
        }
    }

    private fun replaceWithNewQuestion() {
        isQuestionLoaded = false
        interactor.clearReference()
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

    override fun onDestroy() {
        interactor.saveCachedQuestion()
        super.onDestroy()
    }
}