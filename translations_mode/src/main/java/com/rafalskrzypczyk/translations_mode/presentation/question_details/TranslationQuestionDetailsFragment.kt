package com.rafalskrzypczyk.translations_mode.presentation.question_details

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeInvisible
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.core.extensions.setupMultilineWithIMEAction
import com.rafalskrzypczyk.core.utils.KeyboardController
import com.rafalskrzypczyk.translations_mode.databinding.FragmentTranslationQuestionDetailsBinding
import com.rafalskrzypczyk.translations_mode.presentation.question_details.ui_models.TranslationEditUIModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TranslationQuestionDetailsFragment :
    BaseBottomSheetFragment<FragmentTranslationQuestionDetailsBinding, TranslationQuestionDetailsContract.View, TranslationQuestionDetailsContract.Presenter>(
        FragmentTranslationQuestionDetailsBinding::inflate
    ), TranslationQuestionDetailsContract.View {

    private lateinit var keyboardController: KeyboardController
    private lateinit var translationsAdapter: TranslationsEditAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keyboardController = KeyboardController(requireContext())
        presenter.getData(arguments)
    }

    override fun onViewBound() {
        super.onViewBound()

        translationsAdapter = TranslationsEditAdapter(
            onTranslationChanged = { index, text -> presenter.updateTranslation(index, text) },
            onTranslationRemoved = { index -> presenter.removeTranslation(index) }
        )

        with(binding) {
            rvTranslations.adapter = translationsAdapter

            phraseDetails.inputPhrase.addTextChangedListener(afterTextChanged = {
                presenter.updatePhrase(it.toString())
            })

            newTranslationBar.inputNewTranslation.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_DONE)
            newTranslationBar.inputNewTranslation.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    presenter.addTranslation(newTranslationBar.inputNewTranslation.text.toString())
                    newTranslationBar.inputNewTranslation.text?.clear()
                    true
                } else false
            }

            newTranslationBar.btnAddTranslation.setOnClickListener {
                presenter.addTranslation(newTranslationBar.inputNewTranslation.text.toString())
                newTranslationBar.inputNewTranslation.text?.clear()
            }

            sectionNavbar.buttonClose.setOnClickListener { dismiss() }
            sectionNavbar.buttonSave.setOnClickListener {
                presenter.saveNewQuestion(phraseDetails.inputPhrase.text.toString())
            }
        }
    }

    override fun displayPhrase(phrase: String) {
        binding.phraseDetails.inputPhrase.setText(phrase)
    }

    override fun displayTranslations(translations: List<TranslationEditUIModel>) {
        translationsAdapter.submitList(translations)
    }

    override fun displayCreatedOn(date: String) {
        binding.creationDetails.labelCreationDate.text = date
    }

    override fun setupView() {
        binding.sectionNavbar.buttonSave.makeGone()
        binding.creationDetails.root.makeVisible()
        binding.loading.root.makeGone()
    }

    override fun setupNewElementView() {
        binding.sectionNavbar.buttonSave.makeVisible()
        binding.creationDetails.root.makeGone()
        binding.loading.root.makeGone()
        keyboardController.showKeyboardWithDelay(binding.phraseDetails.inputPhrase)
    }

    override fun displayToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun displayLoading() {
        binding.loading.root.makeVisible()
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}
