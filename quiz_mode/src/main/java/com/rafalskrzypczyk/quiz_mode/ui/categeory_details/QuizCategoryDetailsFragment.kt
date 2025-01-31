package com.rafalskrzypczyk.quiz_mode.ui.categeory_details

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizCategoryDetailsBinding
import com.rafalskrzypczyk.quiz_mode.models.Category
import com.rafalskrzypczyk.quiz_mode.models.Question
import com.rafalskrzypczyk.quiz_mode.utils.CategoryStatus
import com.rafalskrzypczyk.quiz_mode.utils.getColor
import com.rafalskrzypczyk.quiz_mode.utils.getTitle

/**
 * Fragment responsible for displaying and managing details of a quiz category.
 *
 * Allows users to:
 * - Add new category
 * - Edit an existing category details e.g. name and description.
 * - Add new questions to the category.
 * - Edit or delete existing questions.
 *
 * Receives the category ID as an argument.
 * Interacts with the [QuizCategoryDetailsPresenter] to manage category data.
 * @see Category
 * @see Question
 */
class QuizCategoryDetailsFragment(
    val bundle: Bundle? = null,
    onDismiss: () -> Unit,
) : BaseBottomSheetFragment<FragmentQuizCategoryDetailsBinding>(
    FragmentQuizCategoryDetailsBinding::inflate,
    onDismiss
), QuizCategoryDetailsView {

    enum class ViewState{
        VIEW,
        EDIT,
        NEW_ELEMENT
    }

    private lateinit var presenter: QuizCategoryDetailsPresenter
    private lateinit var adapter: QuestionsSimpleAdapter

    private var isInEditMode = false

    private var viewState = ViewState.VIEW

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = QuizCategoryDetailsPresenter(this)

        val categoryId = bundle?.getInt("categoryId")
        if(categoryId == null) {
            displayNewCategorySheet()
        } else {
            presenter.loadCategoryById(categoryId)
            setupBindings()
        }
    }

    // PRIVATE FUNCTIONS

    /**
     * Set up event listeners and input handling.
     */
    private fun setupBindings(){
        binding.buttonClose.setOnClickListener {
            if (isInEditMode) {
                displayWarningDialog { dialog?.dismiss() }
            } else dialog?.dismiss()
        }

        binding.buttonEditSave.setOnClickListener {
            if(isInEditMode){
                presenter.updateCategoryDetails(
                    binding.categoryNameField.text.toString(),
                    binding.categoryDescriptionField.text.toString()
                )
            }
            switchEditing()
        }

        binding.changeStatusButton.setOnClickListener {
            showStatusPopupMenu(it) {
                updateStatus(it)
            }
        }

        setupCategoryFieldHandlers()
    }

    /**
     * Displays a popup menu anchored to the specified view, allowing the user to select a category status.
     *
     * @param anchor The view to which the popup menu should be anchored.
     * @param onItemSelected A callback function that is invoked when a category status is selected.
     *                       The selected [CategoryStatus] is passed as a parameter to this callback.
     *
     * @see CategoryStatus
     */
    private fun showStatusPopupMenu(anchor: View, onItemSelected: (CategoryStatus) -> Unit) {
        val popupMenu = PopupMenu(requireContext(), anchor)

        CategoryStatus.entries.forEach {
            popupMenu.menu.add(it.getTitle(requireContext()))
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val selectedStatus = CategoryStatus.entries.find { it.getTitle(requireContext()) == menuItem.title }
            selectedStatus?.let {
                onItemSelected(it)
            }
            true
        }

        popupMenu.show()
    }

    /**
     * Updates the status display button with the provided [CategoryStatus].
     *
     * This function updates the text of the button to reflect the new status and changes the background color of the button.
     *
     * @param status The new [CategoryStatus] to be displayed.
     *
     * @see CategoryStatus
     */
    private fun updateStatus(status: CategoryStatus){
        presenter.updateCategoryStatus(status)
    }

    private fun updateStatusLabel(status: CategoryStatus){
        val statusColor = status.getColor(requireContext())
        setColorPreview(binding.categoryStatusColor, statusColor)
        binding.categoryStatusIndicator.setTextColor(statusColor)
        binding.categoryStatusIndicator.text = status.getTitle(requireContext())
    }

    private fun setColorPreview(view: View, color: Int){
        val colorPreviewBackground = view.background as GradientDrawable
        colorPreviewBackground.setColor(color)
    }

    /**
     * Set up input type and editor actions for category section fields.
     */
    private fun setupCategoryFieldHandlers() {
        // Configure the category name field
        binding.categoryNameField.imeOptions = EditorInfo.IME_ACTION_NEXT
        binding.categoryNameField.setRawInputType(InputType.TYPE_CLASS_TEXT)

        // Configure the category description field
        binding.categoryDescriptionField.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.categoryDescriptionField.setRawInputType(InputType.TYPE_CLASS_TEXT)

        // IME action listeners to move focus between fields
        binding.categoryNameField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.categoryDescriptionField.requestFocus()
                true
            } else false
        }

        binding.categoryDescriptionField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else false
        }
    }

    /**
     * Set up the RecyclerView for managing the list of questions, including swipe to delete functionality.
     * @param questions List of questions to display in the RecyclerView.
     */
    private fun setupQuestionsListRecyclerView(questions: List<Question>){
        val recyclerView = binding.questionsRecyclerView
        adapter = QuestionsSimpleAdapter(questions.toMutableList())

        val swipeItemCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = viewHolder.itemView
                val backgroundDrawable = AppCompatResources.getDrawable(requireContext(), com.rafalskrzypczyk.core.R.drawable.background_swipe_element)
                backgroundDrawable?.setBounds(
                    (itemView.right + dX).toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                backgroundDrawable?.draw(c)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeItem(viewHolder.adapterPosition)
            }
        }

        ItemTouchHelper(swipeItemCallback).attachToRecyclerView(recyclerView)
        recyclerView.adapter = adapter
    }

    /**
     * Displays a new [Category] form for creating a new category.
     * This includes enabling editing and showing the soft input keyboard.
     */
    private fun displayNewCategorySheet(){
        setupBindings()
        switchEditing()
        showKeyboardWithDelay()
        setupQuestionsListRecyclerView(mutableListOf())
    }

    /**
     * Shows the keyboard with a slight delay to ensure it is shown after focus.
     */
    private fun showKeyboardWithDelay() {
        binding.categoryNameField.postDelayed({
            binding.categoryNameField.requestFocus()
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.categoryNameField, InputMethodManager.SHOW_IMPLICIT)
        }, 200)
    }

    /**
     * Enables editing for the category fields and hides the edit button.
     */
    private fun switchEditing(){
        isInEditMode = !isInEditMode
        disableHiding()

        binding.categoryNameField.isEnabled = isInEditMode
        binding.categoryDescriptionField.isEnabled = isInEditMode

        binding.editingButtonsSection.visibility = if(isInEditMode) View.VISIBLE else View.GONE

        binding.buttonEditSave.text =
            if(isInEditMode) getString(com.rafalskrzypczyk.core.R.string.button_save)
            else getString(com.rafalskrzypczyk.core.R.string.button_edit)
    }

    /**
     * Disables the ability to hide the bottom sheet and prevents dragging.
     */
    private fun disableHiding() {
        val sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior.isHideable = false
        sheetBehavior.isDraggable = false
    }

    /**
     * Displays a warning dialog for closing the sheet, when there are unsaved changes.
     * @param onIgnore Callback to be executed when the user chooses to ignore the changes.
     */
    private fun displayWarningDialog(onIgnore: () -> Unit){
        val builder = AlertDialog.Builder(requireContext())
        builder
            .setMessage(R.string.alert_unsaved_changes_message)
            .setTitle(R.string.alert_unsaved_changes_title)
            .setNegativeButton(R.string.alert_unsaved_changes_negative_button) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.alert_unsaved_changes_positive_button) { dialog, which ->
                onIgnore()
            }
        val warningDialog = builder.create()
        warningDialog.show()
    }

    /**
     * Hides the keyboard.
     */
    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.categoryNameField.windowToken, 0)
    }

    // IMPLEMENTATIONS

    /**
     * Override from interface [QuizCategoryDetailsView].
     * Display the category details, including questions associated with the category.
     * @param category The category to display.
     * @param questions List of questions associated with the category.
     */
    override fun displayCategoryDetails(category: LiveData<Category>, questions: List<Question>) {
        setupQuestionsListRecyclerView(questions)
        binding.categoryQuestionsCount.text = String.format(adapter.itemCount.toString())
        category.observe(viewLifecycleOwner){ newValue ->
            binding.categoryNameField.setText(newValue.title)
            binding.categoryDescriptionField.setText(newValue.description)
            setColorPreview(binding.colorPreview, newValue.color.toInt())
            updateStatusLabel(newValue.status)
            binding.createdOnLabel.text = newValue.creationDate.toString()
        }
    }
}