package com.rafalskrzypczyk.quiz_mode.presentation.category_details

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.color_picker.ColorPickerDialogFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.setupMultilineWithIMEAction
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.utils.KeyboardController
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizCategoryDetailsBinding
import com.rafalskrzypczyk.quiz_mode.domain.QuizCategoryDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.domain.models.CategoryStatus
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

        with(binding){
            sectionNavbar.buttonClose.setOnClickListener { dismiss() }
            sectionNavbar.buttonSave.setOnClickListener {
                presenter.createNewCategory(binding.sectionCategoryDetails.categoryNameField.text.toString())
            }
            sectionCategoryDetails.buttonChangeColor.setOnClickListener { presenter.onChangeColor() }
            sectionCategoryDetails.buttonChangeStatus.setOnClickListener { presenter.onChangeCategoryStatus() }
            sectionQuestionsList.buttonNewQuestion.setOnClickListener { presenter.onNewQuestion() }
            sectionQuestionsList.buttonAddFromDb.setOnClickListener { presenter.onQuestionFromList() }
        }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun setupView() {
        adapter = QuestionsSimpleAdapter()

        with(binding){
            groupEditionFields.visibility = View.VISIBLE
            sectionCategoryDetails.groupDetailsEditionFields.visibility = View.VISIBLE
            sectionNavbar.buttonSave.visibility = View.GONE

            sectionQuestionsList.questionsRecyclerView.adapter = adapter

            with(sectionCategoryDetails){
                categoryNameField.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_NEXT)
                categoryNameField.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        if(categoryNameField.text.isNotEmpty()) categoryDescriptionField.requestFocus()
                        true
                    } else false
                }

                categoryDescriptionField.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_DONE)
                categoryDescriptionField.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        keyboardController.hideKeyboard(categoryDescriptionField)
                        true
                    } else false
                }

                categoryNameField.addTextChangedListener(
                    afterTextChanged = {
                        presenter.updateCategoryTitle(it.toString())
                    }
                )
                categoryDescriptionField.addTextChangedListener(
                    afterTextChanged = {
                        presenter.updateCategoryDescription(it.toString())
                    }
                )

                if (categoryNameField.hasFocus()) categoryNameField.clearFocus()
            }
        }
    }

    override fun setupNewElementView() {
        with(binding){
            groupEditionFields.visibility = View.GONE
            sectionCategoryDetails.groupDetailsEditionFields.visibility = View.GONE
            sectionNavbar.buttonSave.visibility = View.VISIBLE

            with(sectionCategoryDetails){
                categoryNameField.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_DONE)
                categoryNameField.setOnEditorActionListener { tv, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        presenter.createNewCategory(tv.text.toString())
                        if(categoryNameField.text.isNotEmpty()) keyboardController.hideKeyboard(tv)
                        true
                    } else false
                }
                keyboardController.showKeyboardWithDelay(categoryNameField)
            }
        }
    }

    override fun displayCategoryDetails(categoryTitle: String, categoryDescription: String) {
        with(binding.sectionCategoryDetails) {
            categoryNameField.setText(categoryTitle)
            categoryDescriptionField.setText(categoryDescription)
        }
    }

    override fun displayCreatedDetails(date: String) {
        binding.sectionCreationDetails.labelCreationDate.text = date
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
            options.find { it.hashCode() == item.itemId }?.let { presenter.updateCategoryStatus(it) }
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
        val newQuestionSheetFragment = QuizQuestionDetailsFragment().apply { arguments = bundle }
        newQuestionSheetFragment.show(parentFragmentManager, "NewQuestionFromCategoryBS")
    }

    override fun displayQuestionListLoading() {
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