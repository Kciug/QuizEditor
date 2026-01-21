package com.rafalskrzypczyk.translations_mode.presentation.question_details

import android.os.Bundle
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.di.IoDispatcher
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.translations_mode.R
import com.rafalskrzypczyk.translations_mode.domain.TranslationQuestion
import com.rafalskrzypczyk.translations_mode.domain.TranslationsRepository
import com.rafalskrzypczyk.translations_mode.presentation.question_details.ui_models.TranslationEditUIModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class TranslationQuestionDetailsPresenter @Inject constructor(
    private val repository: TranslationsRepository,
    private val resourceProvider: ResourceProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BasePresenter<TranslationQuestionDetailsContract.View>(), TranslationQuestionDetailsContract.Presenter {

    private val externalScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private var question: TranslationQuestion? = null
    private var isNewQuestion = false
    
    private var currentPhrase: String = ""
    private var currentTranslations: MutableList<String> = mutableListOf()

    override fun getData(bundle: Bundle?) {
        val questionId = bundle?.getLong("questionId") ?: 0L
        if (questionId == 0L) {
            isNewQuestion = true
            view.setupNewElementView()
            return
        }

        presenterScope?.launch {
            repository.getQuestionById(questionId).collectLatest { response ->
                when (response) {
                    is Response.Success -> {
                        question = response.data
                        currentPhrase = response.data.phrase
                        currentTranslations = response.data.translations.toMutableList()
                        
                        view.setupView()
                        view.displayPhrase(currentPhrase)
                        view.displayTranslations(currentTranslations.mapIndexed { index, s -> TranslationEditUIModel(index, s) })
                        view.displayCreatedOn(String.formatDate(response.data.dateCreated))
                    }
                    is Response.Error -> view.displayError(response.error)
                    is Response.Loading -> view.displayLoading()
                }
            }
        }
    }

    override fun updatePhrase(phrase: String) {
        currentPhrase = phrase
    }

    override fun addTranslation(translation: String) {
        if (translation.isBlank()) {
            view.displayToastMessage(resourceProvider.getString(R.string.warning_empty_translation))
            return
        }
        currentTranslations.add(translation)
        updateTranslationsUI()
    }

    override fun updateTranslation(index: Int, translation: String) {
        if (index in currentTranslations.indices) {
            currentTranslations[index] = translation
        }
    }

    override fun removeTranslation(index: Int) {
        if (index in currentTranslations.indices) {
            currentTranslations.removeAt(index)
            updateTranslationsUI()
        }
    }

    override fun saveNewQuestion(phrase: String) {
        if (phrase.isBlank()) {
            view.displayToastMessage(resourceProvider.getString(R.string.warning_empty_phrase))
            return
        }
        
        presenterScope?.launch {
            val newQuestion = TranslationQuestion.new(phrase, currentTranslations)
            when (val response = repository.addQuestion(newQuestion)) {
                is Response.Success -> view.dismiss()
                is Response.Error -> view.displayError(response.error)
                is Response.Loading -> view.displayLoading()
            }
        }
    }

    private fun updateTranslationsUI() {
        view.displayTranslations(currentTranslations.mapIndexed { index, s -> TranslationEditUIModel(index, s) })
    }

    override fun onDestroy() {
        if (!isNewQuestion && question != null) {
            saveChanges()
        }
        super.onDestroy()
    }

    private fun saveChanges() {
        question?.let {
            if (currentPhrase.isBlank()) return
            
            val updatedQuestion = it.copy(
                phrase = currentPhrase,
                translations = currentTranslations,
                dateModified = java.util.Date()
            )
            
            externalScope.launch {
                repository.updateQuestion(updatedQuestion)
            }
        }
    }
}
