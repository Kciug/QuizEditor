package com.rafalskrzypczyk.swipe_mode.presentation.question_details

import android.os.Bundle
import com.rafalskrzypczyk.core.base.BaseContract

interface SwipeQuestionDetailsContract {
    interface View : BaseContract.View {
        fun displayQuestionDetails(questionText: String, explanation: String, isCorrect: Boolean?)
        fun displayCreatedDetails(dateCreated: String)
        fun replaceWithNewQuestion()
        fun displayToastMessage(message: String)
        fun openKeyboard()
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun getData(arguments: Bundle?)
        fun updateQuestionText(questionText: String)
        fun updateExplanation(explanation: String)
        fun updateIsCorrect(isCorrect: Boolean?)
        fun saveNewQuestion(questionText: String, explanation: String, isCorrect: Boolean?)
        fun saveAndOpenNewQuestion(questionText: String, explanation: String, isCorrect: Boolean?)
    }
}