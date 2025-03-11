package com.rafalskrzypczyk.quiz_mode.presentation.question_details

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
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

        val fieldQuestionText = binding.fieldQuestionText
        val fieldNewAnswer = binding.fieldNewAnswer

        fieldQuestionText.imeOptions = EditorInfo.IME_ACTION_DONE
        fieldQuestionText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        fieldQuestionText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.onQuestionTextSubmitted(fieldQuestionText.text.toString())
                keyboardController.hideKeyboard(fieldQuestionText)
                true
            } else false
        }

        fieldNewAnswer.imeOptions = EditorInfo.IME_ACTION_DONE
        fieldNewAnswer.setRawInputType(InputType.TYPE_CLASS_TEXT)
        fieldNewAnswer.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.addAnswer(fieldNewAnswer.text.toString())
                //keyboardController.hideKeyboard(fieldNewAnswer)
                true
            } else false
        }

        binding.bottomSheetBar.buttonClose.setOnClickListener {
            dismiss()
        }

        binding.bottomSheetBar.buttonSave.setOnClickListener {
            presenter.saveNewQuestion(binding.fieldQuestionText.text.toString())
        }

        binding.buttonAssignCategory.setOnClickListener {
            presenter.onAssignCategory()
        }

        binding.buttonAddAnswer.setOnClickListener {
            presenter.addAnswer(binding.fieldNewAnswer.text.toString())
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

    override fun updateInputOnAnswerAdded() {
        binding.fieldNewAnswer.text.clear()
    }

    override fun displayLinkedCategories(categories: List<SimpleCategoryUIModel>) {
        if(categories.isEmpty()) {
            binding.labelNoCategories.visibility = View.VISIBLE
            categoriesPreviewAdapter.submitList(categories)
            return
        }
        binding.labelNoCategories.visibility = View.GONE
        categoriesPreviewAdapter.submitList(categories)
    }

    override fun displayCreatedOn(date: String, user: String) {
        binding.labelCreationDate.text = date
        binding.createdByLabel.text = user
    }

    override fun setupView() {
        categoriesPreviewAdapter = CategoriesPreviewAdapter()
        binding.categoriesRecyclerView.adapter = categoriesPreviewAdapter

        answersListAdapter = AnswersListAdapter(
            onAnswerChanged = { presenter.updateAnswer(it) },
            onAnswerRemoved = { presenter.removeAnswer(it) }
        )
        binding.answersRecyclerView.adapter = answersListAdapter
        binding.answersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        if(binding.fieldQuestionText.hasFocus()) binding.fieldQuestionText.clearFocus()

        binding.fieldQuestionText.addTextChangedListener(
            afterTextChanged = {
                presenter.updateQuestionText(it.toString())
            }
        )

        binding.bottomSheetBar.buttonSave.visibility = View.GONE
        binding.sectionAssignedCategories.visibility = View.VISIBLE
        binding.sectionCreationDetails.visibility = View.VISIBLE
        binding.sectionAnswersDetails.visibility = View.VISIBLE
        binding.sectionAnswers.visibility = View.VISIBLE
    }

    override fun setupNewElementView() {
        binding.bottomSheetBar.buttonSave.visibility = View.VISIBLE
        binding.sectionAssignedCategories.visibility = View.GONE
        binding.sectionCreationDetails.visibility = View.GONE
        binding.sectionAnswersDetails.visibility = View.GONE
        binding.sectionAnswers.visibility = View.GONE

        keyboardController.showKeyboardWithDelay(binding.fieldQuestionText)
    }

    override fun displayCategoryPicker() {
        val linkedCategoriesPicker = CheckablePickerFragment(parentInteractor)
        linkedCategoriesPicker.show(parentFragmentManager, "CategoriesPickerBS")
    }

    override fun displayCategoriesListLoading() {
    }

    override fun displayLoading() {
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}