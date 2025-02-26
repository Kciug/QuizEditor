package com.rafalskrzypczyk.quiz_mode.presentation.categeory_details

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.color_picker.ColorPickerDialogFragment
import com.rafalskrzypczyk.core.utils.KeyboardController
import com.rafalskrzypczyk.quiz_mode.databinding.Temp2Binding
import com.rafalskrzypczyk.quiz_mode.domain.QuizCategoryDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.presentation.checkable_picker.CheckablePickerFragment
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.QuizQuestionDetailsFragment
import com.rafalskrzypczyk.quiz_mode.utils.CategoryStatus
import com.rafalskrzypczyk.quiz_mode.utils.getColor
import com.rafalskrzypczyk.quiz_mode.utils.getTitle
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizCategoryDetailsFragment : BaseBottomSheetFragment<Temp2Binding>(
    Temp2Binding::inflate
), QuizCategoryDetailsContract.View {
    @Inject
    lateinit var presenter: QuizCategoryDetailsContract.Presenter
    @Inject
    lateinit var parentInteractor: QuizCategoryDetailsInteractor

    private lateinit var adapter: QuestionsSimpleAdapter

    private lateinit var keyboardController: KeyboardController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keyboardController = KeyboardController(requireContext())
        presenter.getData(arguments)
    }

    override fun onViewBound() {
        super.onViewBound()

        binding.sectionNavbar.buttonClose.setOnClickListener {
            dismiss()
        }

        binding.sectionNavbar.buttonSave.setOnClickListener {
            presenter.createNewCategory(binding.sectionCategoryDetails.categoryNameField.text.toString())
        }

        binding.sectionCategoryDetails.buttonChangeColor.setOnClickListener {
            val bundle = Bundle().apply { putInt("currentColor", presenter.getCategoryColor()) }
            val colorPickerFragment = ColorPickerDialogFragment().apply { arguments = bundle }
            colorPickerFragment.show(parentFragmentManager, "ColorPickerDialog")
        }

        binding.sectionCategoryDetails.buttonChangeStatus.setOnClickListener {

        }

        binding.sectionQuestionsList.buttonNewQuestion.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("parentCategoryId", presenter.getCategoryId())
            }
            val newQuestionSheetFragment = QuizQuestionDetailsFragment().apply { arguments = bundle }
            newQuestionSheetFragment.setOnDismiss { presenter.updateQuestionList() }
            newQuestionSheetFragment.show(parentFragmentManager, "NewQuestionFromCategoryBS")
        }

        binding.sectionQuestionsList.buttonAddFromDb.setOnClickListener {
            val linkedQuestionsPicker = CheckablePickerFragment(parentInteractor)
            linkedQuestionsPicker.setOnDismiss { presenter.updateQuestionList() }
            linkedQuestionsPicker.show(parentFragmentManager, "CategoriesPickerBS")
        }
    }

    override fun onDestroy() {
        presenter.onViewClosed()
        super.onDestroy()
    }

    override fun setupView() {
        binding.groupEditionFields.visibility = View.VISIBLE
        binding.sectionCategoryDetails.gruopEditionFields.visibility = View.VISIBLE
        binding.sectionNavbar.buttonSave.visibility = View.GONE

        adapter = QuestionsSimpleAdapter()
        binding.sectionQuestionsList.questionsRecyclerView.adapter = adapter

        val categoryTitle = binding.sectionCategoryDetails.categoryNameField
        val categoryDescription = binding.sectionCategoryDetails.categoryDescriptionField

        categoryTitle.imeOptions = EditorInfo.IME_ACTION_NEXT
        categoryTitle.setRawInputType(InputType.TYPE_CLASS_TEXT)
        categoryTitle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                categoryDescription.requestFocus()
                true
            } else false
        }

        categoryDescription.imeOptions = EditorInfo.IME_ACTION_DONE
        categoryDescription.setRawInputType(InputType.TYPE_CLASS_TEXT)
        categoryDescription.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                keyboardController.hideKeyboard(categoryDescription)
                true
            } else false
        }

        categoryTitle.addTextChangedListener(
            afterTextChanged = {
                presenter.updateCategoryTitle(categoryTitle.text.toString())
            }
        )
        categoryDescription.addTextChangedListener(
            afterTextChanged = {
                presenter.updateCategoryDescription(categoryDescription.text.toString())
            }
        )

        if(binding.sectionCategoryDetails.categoryNameField.hasFocus())
            binding.sectionCategoryDetails.categoryNameField.clearFocus()

        setFragmentResultListener("selectedColor") { requestKey, bundle ->
            val result = bundle.getInt("selectedColor")
            presenter.updateCategoryColor(result)
        }
    }

    override fun setupNewElementView() {
        binding.groupEditionFields.visibility = View.GONE
        binding.sectionCategoryDetails.gruopEditionFields.visibility = View.GONE
        binding.sectionNavbar.buttonSave.visibility = View.VISIBLE

        val categoryTitle = binding.sectionCategoryDetails.categoryNameField
        categoryTitle.imeOptions = EditorInfo.IME_ACTION_DONE
        categoryTitle.setRawInputType(InputType.TYPE_CLASS_TEXT)
        categoryTitle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.createNewCategory(binding.sectionCategoryDetails.categoryNameField.text.toString())
                keyboardController.hideKeyboard(categoryTitle)
                true
            } else false
        }

        keyboardController.showKeyboardWithDelay(binding.sectionCategoryDetails.categoryNameField)
    }

    override fun displayCategoryDetails(
        categoryTitle: String,
        categoryDescription: String
    ) {
        val detailsSection = binding.sectionCategoryDetails
        detailsSection.categoryNameField.setText(categoryTitle)
        detailsSection.categoryDescriptionField.setText(categoryDescription)
    }

    override fun displayCategoryColor(color: Int) {
        val colorPreview = binding.sectionCategoryDetails.colorPreview.background as GradientDrawable
        colorPreview.setColor(color)
    }

    override fun displayCategoryStatus(status: CategoryStatus) {
        binding.sectionCategoryDetails.indicatorStatus.setColorAndText(
            status.getColor(requireContext()),
            status.getTitle(requireContext())
        )
    }

    override fun displayQuestionCount(questionCount: Int) {
        binding.categoryQuestionsCount.text = String.format(questionCount.toString())
    }

    override fun displayQuestionList(questions: List<Question>) {
        adapter.submitList(questions)
    }
}