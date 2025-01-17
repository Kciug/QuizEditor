package com.rafalskrzypczyk.quiz_mode.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rafalskrzypczyk.quiz_mode.QuestionsSimpleAdapter
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizCategoryDetailsBinding
import com.rafalskrzypczyk.quiz_mode.models.Category
import com.rafalskrzypczyk.quiz_mode.models.Question
import com.rafalskrzypczyk.quiz_mode.presenters.QuizCategoryDetailsPresenter

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
class QuizCategoryDetailsFragment(val bundle: Bundle? = null) : BottomSheetDialogFragment(), QuizCategoryDetailsView {

    private var _binding: FragmentQuizCategoryDetailsBinding? = null
    private val binding get() = _binding!!

    private var _bottomSheet: View? = null
    private val bottomSheet get() = _bottomSheet!!

    private lateinit var presenter: QuizCategoryDetailsPresenter
    private lateinit var adapter: QuestionsSimpleAdapter

    private var valuesChanged = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizCategoryDetailsBinding.inflate(inflater, container, false)
        presenter = QuizCategoryDetailsPresenter(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        setupBottomSheetDialog()

        val categoryId = bundle?.getInt("categoryId")
        if(categoryId == null) {
            displayNewCategorySheet()

        } else {
            presenter.loadCategoryById(categoryId)
            setupBindings()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // PRIVATE FUNCTIONS

    /**
     * Set up the [BottomSheetDialog][com.google.android.material.bottomsheet.BottomSheetDialog]
     * with expanded state and height adjustments using [BottomSheetBehavior]
     */
    private fun setupBottomSheetDialog(){
        bottomSheet.let {
            it.layoutParams.height = (resources.displayMetrics.heightPixels * 0.97f).toInt()

            val behavior = BottomSheetBehavior.from(it)
            behavior.skipCollapsed = true

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    /**
     * Set up event listeners and input handling.
     */
    private fun setupBindings(){
        binding.buttonClose.setOnClickListener {
            if (valuesChanged) {
                displayWarningDialog { dialog?.dismiss() }
            } else dialog?.dismiss()
        }

        binding.buttonEditCategory.setOnClickListener {
            enableEditing()
        }

        setupCategoryFieldHandlers()
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

        // Text watchers to detect changes in the category fields
        binding.categoryNameField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onValueChanged()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.categoryDescriptionField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onValueChanged()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    /**
     * Set up the RecyclerView for managing the list of questions, including swipe to delete functionality.
     * @param questions List of questions to display in the RecyclerView.
     */
    private fun setupQuestionsListRecyclerView(questions: List<Question>){
        val recyclerView = binding.questionsRecyclerView
        adapter = QuestionsSimpleAdapter(questions.toMutableList()) { onValueChanged() }

        val swipeItemCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = viewHolder.itemView
                val backgroundDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.background_swipe_element)
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
        enableEditing()
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
     * Handles the detection of changes in editable fields, enabling the save button.
     */
    private fun onValueChanged(){
        if(!valuesChanged){
            valuesChanged = true
            disableHiding()
            binding.buttonSave.visibility = View.VISIBLE
        }
    }

    /**
     * Enables editing for the category fields and hides the edit button.
     */
    private fun enableEditing(){
        binding.categoryNameField.isEnabled = true
        binding.categoryDescriptionField.isEnabled = true
        binding.buttonEditCategory.visibility = View.INVISIBLE
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
    override fun displayCategoryDetails(category: Category, questions: List<Question>) {
        setupQuestionsListRecyclerView(questions)
        binding.categoryNameField.setText(category.title)
        binding.categoryDescriptionField.setText(category.description)
        binding.categoryQuestionsCount.text = String.format(adapter.itemCount.toString())

    }
}