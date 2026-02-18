package com.rafalskrzypczyk.cem_mode.presentation.category_details

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import com.rafalskrzypczyk.cem_mode.databinding.FragmentCemCategoryDetailsBinding
import com.rafalskrzypczyk.cem_mode.presentation.question_details.CemQuestionDetailsFragment
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.color_picker.ColorPickerDialogFragment
import com.rafalskrzypczyk.core.domain.models.CategoryStatus
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeInvisible
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.core.extensions.setupMultilineWithIMEAction
import com.rafalskrzypczyk.core.nav_handling.DrawerNavigationHandler
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.utils.KeyboardController
import com.rafalskrzypczyk.core.R as coreR
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CemCategoryDetailsFragment :
    BaseBottomSheetFragment<FragmentCemCategoryDetailsBinding, CemCategoryDetailsContract.View, CemCategoryDetailsContract.Presenter>(
        FragmentCemCategoryDetailsBinding::inflate
    ), CemCategoryDetailsContract.View {

    private lateinit var keyboardController: KeyboardController
    private var isSilentUpdate = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keyboardController = KeyboardController(requireContext())
        presenter.getData(arguments)
    }

    override fun onViewBound() {
        super.onViewBound()
        keyboardController = KeyboardController(requireContext())

        with(binding){
            sectionNavbar.buttonClose.setOnClickListener { dismiss() }
            sectionNavbar.buttonSave.setOnClickListener {
                presenter.createNewCategory(binding.sectionCategoryDetails.categoryNameField.text.toString())
            }
            sectionCategoryDetails.buttonChangeColor.setOnClickListener { presenter.onChangeColor() }
            sectionCategoryDetails.buttonChangeStatus.setOnClickListener { presenter.onChangeCategoryStatus() }
            sectionCategoryDetails.switchIsFree.setOnCheckedChangeListener { _, isChecked ->
                if (!isSilentUpdate) presenter.updateIsFree(isChecked)
            }
            sectionSubcategoriesList.buttonDisplaySubcategories.setOnClickListener { presenter.onCategorySubcategories() }
            sectionSubcategoriesList.buttonNewSubcategory.setOnClickListener { presenter.onNewSubcategory() }
            sectionQuestionsList.buttonDisplayQuestions.setOnClickListener { presenter.onCategoryQuestions() }
            sectionQuestionsList.buttonNewQuestion.setOnClickListener { presenter.onNewQuestion() }
        }
    }

    override fun setupView() {
        with(binding){
            groupEditionFields.visibility = View.VISIBLE
            sectionCategoryDetails.groupDetailsEditionFields.visibility = View.VISIBLE
            sectionNavbar.buttonSave.visibility = View.GONE

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

                categoryNameField.addTextChangedListener(afterTextChanged = { 
                    if (!isSilentUpdate) presenter.updateCategoryTitle(it.toString()) 
                })
                categoryDescriptionField.addTextChangedListener(afterTextChanged = { 
                    if (!isSilentUpdate) presenter.updateCategoryDescription(it.toString()) 
                })

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
        isSilentUpdate = true
        with(binding.sectionCategoryDetails) {
            categoryNameField.setText(categoryTitle)
            categoryDescriptionField.setText(categoryDescription)
        }
        isSilentUpdate = false
    }

    override fun displayCreatedDetails(date: String) {
        binding.sectionCreationDetails.labelCreationDate.text = date
    }

    override fun displayCategoryColor(color: Int) {
        binding.sectionCategoryDetails.colorPreview.setColorAndText(color, requireContext().getString(coreR.string.label_category_color_preview))
    }

    override fun displayCategoryStatus(status: CategoryStatus) {
        binding.sectionCategoryDetails.indicatorStatus.setColorAndText(
            requireContext().getColor(status.color),
            requireContext().getString(status.title)
        )
    }

    override fun displayQuestionCount(questionCount: Int) {
        binding.categoryQuestionsCount.text = questionCount.toString()
    }

    override fun displaySubcategoryCount(subcategoryCount: Int) {
        binding.categorySubcategoriesCount.text = subcategoryCount.toString()
    }

    override fun displayCategoryStatusMenu(options: List<SelectableMenuItem>) {
        val statusPopupMenu = PopupMenu(requireContext(), binding.sectionCategoryDetails.buttonChangeStatus)
        options.forEach{
            statusPopupMenu.menu.add(Menu.NONE, it.itemHashCode, Menu.NONE, requireContext().getString(it.title))
        }

        statusPopupMenu.setOnMenuItemClickListener{ item ->
            options.find { it.itemHashCode == item.itemId }?.let { presenter.updateCategoryStatus(it) }
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

    override fun displayIsFree(isFree: Boolean) {
        isSilentUpdate = true
        binding.sectionCategoryDetails.switchIsFree.isChecked = isFree
        isSilentUpdate = false
    }

    override fun displaySubcategoriesList(parentId: Long) {
        parentFragmentManager.setFragmentResult("open_subcategory", Bundle().apply { putLong("parentId", parentId) })
        dismiss()
    }

    override fun displayQuestionsList(categoryId: Long, categoryTitle: String, categoryColor: Long) {
        val bundle = Bundle().apply {
            putLong("categoryId", categoryId)
            putString("categoryTitle", categoryTitle)
            putLong("categoryColor", categoryColor)
        }
        (requireActivity() as DrawerNavigationHandler).navigateToDestinationByTag("cem_category_questions_list", bundle)
        dismiss()
    }

    override fun displayNewQuestionSheet(categoryId: Long) {
        val bundle = Bundle().apply { putLong("parentCategoryID", categoryId) }
        CemQuestionDetailsFragment().apply { arguments = bundle }.show(parentFragmentManager, "NewCemQuestionBS")
    }

    override fun displayNewSubcategorySheet(parentId: Long) {
        val bundle = Bundle().apply { putLong("parentCategoryID", parentId) }
        CemCategoryDetailsFragment().apply { arguments = bundle }.show(parentFragmentManager, "NewCemSubcategoryBS")
    }

    override fun displayToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun displayContent() {
        binding.groupEditionFields.makeVisible()
        binding.groupContent.makeVisible()
        binding.loading.root.makeGone()
    }

    override fun displayLoading() {
        binding.groupEditionFields.makeInvisible()
        binding.groupContent.makeInvisible()
        binding.loading.root.makeVisible()
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}
