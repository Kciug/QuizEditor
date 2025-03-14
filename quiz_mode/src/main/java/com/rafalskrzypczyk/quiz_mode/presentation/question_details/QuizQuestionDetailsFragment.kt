package com.rafalskrzypczyk.quiz_mode.presentation.question_details

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.setupMultilineWithIMEAction
import com.rafalskrzypczyk.core.utils.KeyboardController
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizQuestionDetailsBinding
import com.rafalskrzypczyk.quiz_mode.domain.QuizQuestionDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.presentation.checkable_picker.CheckablePickerFragment
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.AnswerUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.SimpleCategoryUIModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizQuestionDetailsFragment : BaseBottomSheetFragment<FragmentQuizQuestionDetailsBinding>(
    FragmentQuizQuestionDetailsBinding::inflate,
), QuizQuestionDetailsContract.View {

    @Inject
    lateinit var presenter: QuizQuestionDetailsContract.Presenter

    @Inject
    lateinit var parentInteractor: QuizQuestionDetailsInteractor

    private lateinit var categoriesPreviewAdapter: CategoriesPreviewAdapter
    private lateinit var answersListAdapter: AnswersListAdapter

    private lateinit var keyboardController: KeyboardController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.onAttach(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keyboardController = KeyboardController(requireContext())
        presenter.onViewCreated()
        presenter.getData(arguments)
    }

    override fun onViewBound() {
        super.onViewBound()

        with(binding){
            fieldQuestionText.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_DONE)
            fieldQuestionText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    presenter.updateQuestionText(fieldQuestionText.text.toString())
                    if(fieldQuestionText.text.isNotEmpty()) keyboardController.hideKeyboard(fieldQuestionText)
                    true
                } else false
            }

            fieldNewAnswer.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_DONE)
            fieldNewAnswer.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    presenter.addAnswer(fieldNewAnswer.text.toString())
                    fieldNewAnswer.text.clear()
                    true
                } else false
            }

            bottomSheetBar.buttonClose.setOnClickListener { dismiss() }
            bottomSheetBar.buttonSave.setOnClickListener { presenter.saveNewQuestion(fieldQuestionText.text.toString()) }

            buttonAssignCategory.setOnClickListener { presenter.onAssignCategory() }

            buttonAddAnswer.setOnClickListener {
                presenter.addAnswer(binding.fieldNewAnswer.text.toString())
                fieldNewAnswer.text.clear()
            }
        }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun displayQuestionText(questionText: String) {
        binding.fieldQuestionText.setText(questionText)
    }

    override fun displayAnswersDetails(answersCount: Int, correctAnswersCount: Int) {
        binding.allAnswersCount.text = String.format(answersCount.toString())
        binding.correctAnswersCount.text = String.format(correctAnswersCount.toString())
    }

    override fun displayAnswersList(answers: List<AnswerUIModel>) {
        answersListAdapter.submitList(answers)
    }

    override fun displayLinkedCategories(categories: List<SimpleCategoryUIModel>) {
        if (categories.isEmpty()) {
            binding.labelNoCategories.alpha = 0f
            binding.labelNoCategories.visibility = View.VISIBLE
            binding.labelNoCategories.animate()
                .alpha(1f)
                .setDuration(200)
                .start()
        } else {
            categoriesPreviewAdapter.submitList(categories)
            binding.categoriesRecyclerView.alpha = 0f
            binding.categoriesRecyclerView.visibility = View.VISIBLE
            binding.categoriesRecyclerView.animate()
                .alpha(1f)
                .setDuration(200)
                .start()
        }
    }

    override fun displayCreatedOn(date: String, user: String) {
        binding.labelCreationDate.text = date
        binding.createdByLabel.text = user
    }

    override fun setupView() {
        categoriesPreviewAdapter = CategoriesPreviewAdapter()
        answersListAdapter = AnswersListAdapter(
            keyboardController = keyboardController,
            onAnswerChanged = { presenter.updateAnswer(it) },
            onAnswerRemoved = { presenter.removeAnswer(it) }
        )

        with(binding){
            categoriesRecyclerView.adapter = categoriesPreviewAdapter
            answersRecyclerView.adapter = answersListAdapter

            bottomSheetBar.buttonSave.visibility = View.GONE
            sectionAssignedCategories.visibility = View.VISIBLE
            sectionCreationDetails.visibility = View.VISIBLE
            sectionAnswersDetails.visibility = View.VISIBLE
            sectionAnswers.visibility = View.VISIBLE

            if (fieldQuestionText.hasFocus()) fieldQuestionText.clearFocus()

            fieldQuestionText.addTextChangedListener(
                afterTextChanged = {
                    presenter.updateQuestionText(it.toString())
                }
            )
        }
    }

    override fun setupNewElementView() {
        with(binding){
            bottomSheetBar.buttonSave.visibility = View.VISIBLE
            sectionAssignedCategories.visibility = View.GONE
            sectionCreationDetails.visibility = View.GONE
            sectionAnswersDetails.visibility = View.GONE
            sectionAnswers.visibility = View.GONE
        }
        keyboardController.showKeyboardWithDelay(binding.fieldQuestionText)
    }

    override fun displayCategoryPicker() {
        val linkedCategoriesPicker = CheckablePickerFragment(parentInteractor)
        linkedCategoriesPicker.show(parentFragmentManager, "CategoriesPickerBS")
    }

    override fun displayCategoriesListLoading() {
    }

    override fun displayToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun displayLoading() {
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}