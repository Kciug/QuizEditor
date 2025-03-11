package com.rafalskrzypczyk.quiz_mode.presentation.categeory_details

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.color_picker.ColorPickerDialogFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.utils.KeyboardController
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizCategoryDetailsBinding
import com.rafalskrzypczyk.quiz_mode.domain.CategoryStatus
import com.rafalskrzypczyk.quiz_mode.domain.QuizCategoryDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.presentation.checkable_picker.CheckablePickerFragment
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.QuizQuestionDetailsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizCategoryDetailsFragment : BaseBottomSheetFragment<FragmentQuizCategoryDetailsBinding>(
    FragmentQuizCategoryDetailsBinding::inflate
), QuizCategoryDetailsContract.View {
    @Inject
    lateinit var presenter: QuizCategoryDetailsContract.Presenter
    @Inject
    lateinit var parentInteractor: QuizCategoryDetailsInteractor

    private lateinit var adapter: QuestionsSimpleAdapter

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

        binding.sectionNavbar.buttonClose.setOnClickListener {
            dismiss()
        }

        binding.sectionNavbar.buttonSave.setOnClickListener {
            presenter.createNewCategory(binding.sectionCategoryDetails.categoryNameField.text.toString())
        }

        binding.sectionCategoryDetails.buttonChangeColor.setOnClickListener {
            presenter.onChangeColor()
        }

        binding.sectionCategoryDetails.buttonChangeStatus.setOnClickListener {
            presenter.onChangeCategoryStatus()
        }

        binding.sectionQuestionsList.buttonNewQuestion.setOnClickListener {
            presenter.onNewQuestion()
        }

        binding.sectionQuestionsList.buttonAddFromDb.setOnClickListener {
            presenter.onQuestionFromList()
        }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun setupView() {
        binding.groupEditionFields.visibility = View.VISIBLE
        binding.sectionCategoryDetails.groupDetailsEditionFields.visibility = View.VISIBLE
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
    }

    override fun setupNewElementView() {
        binding.groupEditionFields.visibility = View.GONE
        binding.sectionCategoryDetails.groupDetailsEditionFields.visibility = View.GONE
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
        binding.sectionCategoryDetails.colorPreview.setColorAndText(color, requireContext().getString(R.string.label_category_color_preview))
    }

    override fun displayCategoryStatus(status: CategoryStatus) {
        binding.sectionCategoryDetails.indicatorStatus.setColorAndText(
            requireContext().getColor(status.color),
            requireContext().getString(status.title)
        )
    }

    override fun displayQuestionCount(questionCount: Int) {
        binding.categoryQuestionsCount.text = String.format(questionCount.toString())
    }

    override fun displayQuestionList(questions: List<Question>) {
        adapter.submitList(questions)
    }

    override fun displayCategoryStatusMenu(options: List<SelectableMenuItem>) {
        val statusPopupMenu = PopupMenu(requireContext(), binding.sectionCategoryDetails.buttonChangeStatus)
        options.forEach{
            statusPopupMenu.menu.add(Menu.NONE, it.hashCode(), Menu.NONE, requireContext().getString(it.title))
        }

        statusPopupMenu.setOnMenuItemClickListener{ item ->
            val selectedItem = options.find { it.hashCode() == item.itemId }
            selectedItem?.let { presenter.updateCategoryStatus(it) }
            true
        }

        statusPopupMenu.show()
    }

    override fun displayColorPicker(currentColor: Int) {
        val bundle = Bundle().apply { putInt("currentColor", currentColor) }
        val colorPickerFragment = ColorPickerDialogFragment().apply { arguments = bundle }
        colorPickerFragment.show(parentFragmentManager, "ColorPickerDialog")

        setFragmentResultListener("selectedColor") { requestKey, bundle ->
            val result = bundle.getInt("selectedColor")
            presenter.updateCategoryColor(result)
        }
    }

    override fun displayQuestionsPicker() {
        val linkedQuestionsPicker = CheckablePickerFragment(parentInteractor)
        linkedQuestionsPicker.show(parentFragmentManager, "CategoriesPickerBS")
    }

    override fun displayNewQuestionSheet(parentCategoryId: Long) {
        val bundle = Bundle().apply { putLong("parentCategoryId", parentCategoryId) }
        Log.d("KURWA", "QuizCategoryDetailsFragment:displayNewQuestionSheet: bundle: $bundle")
        val newQuestionSheetFragment = QuizQuestionDetailsFragment().apply { arguments = bundle }
        newQuestionSheetFragment.show(parentFragmentManager, "NewQuestionFromCategoryBS")
    }

    override fun displayQuestionListLoading() {
    }

    override fun displayLoading() {
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}