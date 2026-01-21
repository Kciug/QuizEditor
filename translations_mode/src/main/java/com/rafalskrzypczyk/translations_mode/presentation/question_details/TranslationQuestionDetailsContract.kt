package com.rafalskrzypczyk.translations_mode.presentation.question_details

import android.os.Bundle
import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.translations_mode.presentation.question_details.ui_models.TranslationEditUIModel

interface TranslationQuestionDetailsContract {
    interface View : BaseContract.View {
        fun displayPhrase(phrase: String)
        fun displayTranslations(translations: List<TranslationEditUIModel>)
        fun displayCreatedOn(date: String)
        fun setupView()
        fun setupNewElementView()
        fun displayToastMessage(message: String)
        fun dismiss()
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun getData(bundle: Bundle?)
        fun updatePhrase(phrase: String)
        fun addTranslation(translation: String)
        fun updateTranslation(index: Int, translation: String)
        fun removeTranslation(index: Int)
        fun saveNewQuestion(phrase: String)
    }
}
