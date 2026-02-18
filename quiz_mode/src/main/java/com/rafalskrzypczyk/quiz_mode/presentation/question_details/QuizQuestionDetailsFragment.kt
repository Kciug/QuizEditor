package com.rafalskrzypczyk.quiz_mode.presentation.question_details

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeInvisible
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.core.extensions.setupMultilineWithIMEAction
import com.rafalskrzypczyk.core.utils.KeyboardController
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizQuestionDetailsBinding
import com.rafalskrzypczyk.quiz_mode.domain.QuizQuestionDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.presentation.checkable_picker.CheckablePickerFragment
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.AnswerUIModel
import com.rafalskrzypczyk.core.presentation.ui_models.SimpleCategoryUIModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizQuestionDetailsFragment :
    BaseBottomSheetFragment<FragmentQuizQuestionDetailsBinding, QuizQuestionDetailsContract.View, QuizQuestionDetailsContract.Presenter>(
        FragmentQuizQuestionDetailsBinding::inflate,
    ), QuizQuestionDetailsContract.View {

    @Inject
    lateinit var parentInteractor: QuizQuestionDetailsInteractor

    private lateinit var categoriesPreviewAdapter: CategoriesPreviewAdapter
    private lateinit var answersListAdapter: AnswersListAdapter

    private lateinit var keyboardController: KeyboardController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keyboardController = KeyboardController(requireContext())
        presenter.getData(arguments)
    }

    override fun onViewBound() {
        super.onViewBound()

        with(binding) {
            questionDetails.inputQuestionText.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_DONE)
            questionDetails.inputQuestionText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    presenter.updateQuestionText(questionDetails.inputQuestionText.text.toString())
                    if(questionDetails.inputQuestionText.text.isNotEmpty()) keyboardController.hideKeyboard(questionDetails.inputQuestionText)
                    true
                } else false
            }

            newAnswerBar.inputNewAnswer.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_DONE)
            newAnswerBar.inputNewAnswer.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    presenter.addAnswer(newAnswerBar.inputNewAnswer.text.toString())
                    newAnswerBar.inputNewAnswer.text.clear()
                    true
                } else false
            }

            sectionNavbar.buttonClose.setOnClickListener { dismiss() }
            sectionNavbar.buttonSave.setOnClickListener { presenter.saveNewQuestion(questionDetails.inputQuestionText.text.toString()) }

            questionDetails.btnAssignCategory.setOnClickListener { presenter.onAssignCategory() }

            newAnswerBar.btnAddAnswer.setOnClickListener {
                presenter.addAnswer(newAnswerBar.inputNewAnswer.text.toString())
                newAnswerBar.inputNewAnswer.text.clear()
            }
        }
    }

    override fun displayContent() {
        binding.loading.root.makeGone()
        binding.content.makeVisible()
        binding.contentDetails.makeVisible()
    }

    override fun displayQuestionText(questionText: String) {
        binding.questionDetails.inputQuestionText.setText(questionText)
    }

    override fun displayAnswersDetails(answersCount: Int, correctAnswersCount: Int) {
        with(binding.answersDetails) {
            countAnswers.text = String.format(answersCount.toString())
            countCorrectAnswers.text = String.format(correctAnswersCount.toString())
        }
    }

    override fun displayAnswersList(answers: List<AnswerUIModel>) {
        answersListAdapter.submitList(answers)
    }

    override fun displayLinkedCategories(categories: List<SimpleCategoryUIModel>) {
        with (binding.questionDetails) {
            categoriesLoading.makeGone()
            if(categories.isEmpty()) {
                categoriesPreviewAdapter.submitList(categories)
                categoriesRecyclerView.makeGone()
                QuizEditorAnimations.animateFadeIn(labelNoCategories)
            } else {
                categoriesPreviewAdapter.submitList(categories)
                labelNoCategories.makeGone()
                QuizEditorAnimations.animateFadeIn(categoriesRecyclerView)
            }
        }
    }

    override fun displayCreatedOn(date: String, user: String) {
        binding.creationDetails.labelCreationDate.text = date
    }

    override fun setupView() {
        categoriesPreviewAdapter = CategoriesPreviewAdapter()
        answersListAdapter = AnswersListAdapter(
            keyboardController = keyboardController,
            onAnswerChanged = { presenter.updateAnswer(it) },
            onAnswerRemoved = { presenter.removeAnswer(it) }
        )

        with(binding){
            questionDetails.categoriesRecyclerView.adapter = categoriesPreviewAdapter
            rvAnswers.adapter = answersListAdapter

            sectionNavbar.buttonSave.makeGone()
            questionDetails.groupDetailsEditionFields.makeVisible()
            content.makeVisible()

            if (questionDetails.inputQuestionText.hasFocus()) questionDetails.inputQuestionText.clearFocus()

            questionDetails.inputQuestionText.addTextChangedListener(
                afterTextChanged = {
                    presenter.updateQuestionText(it.toString())
                }
            )
        }
    }

    override fun setupNewElementView() {
        with(binding){
            sectionNavbar.buttonSave.makeVisible()
            questionDetails.groupDetailsEditionFields.makeGone()
            content.makeGone()

            keyboardController.showKeyboardWithDelay(questionDetails.inputQuestionText)
        }
    }

    override fun displayCategoryPicker() {
        val linkedCategoriesPicker = CheckablePickerFragment(parentInteractor)
        linkedCategoriesPicker.show(parentFragmentManager, "CategoriesPickerBS")
    }

    override fun displayCategoriesListLoading() {
        binding.questionDetails.categoriesLoading.makeVisible()
    }

    override fun displayToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun displayLoading() {
        binding.content.makeInvisible()
        binding.contentDetails.makeInvisible()
        binding.loading.root.makeVisible()
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}