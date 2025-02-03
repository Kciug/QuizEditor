package com.rafalskrzypczyk.quiz_mode.ui.question_details

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.utils.KeyboardController
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizQuestionDetailsBinding
import com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models.AnswerUIModel
import com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models.SimpleCategoryUIModel

class QuizQuestionDetailsFragment(
    val bundle: Bundle? = null,
    onDismiss: () -> Unit,
) : BaseBottomSheetFragment<FragmentQuizQuestionDetailsBinding>(
    FragmentQuizQuestionDetailsBinding::inflate,
    onDismiss
), QuizQuestionDetailsContract.View {

    private lateinit var presenter: QuizQuestionDetailsPresenter
    private lateinit var categoriesPreviewAdapter: CategoriesPreviewAdapter
    private lateinit var answersListAdapter: AnswersListAdapter

    private lateinit var keyboardController: KeyboardController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keyboardController = KeyboardController(requireContext())
        presenter = QuizQuestionDetailsPresenter(this)
        presenter.getData(bundle)
    }

    override fun onViewBound() {
        super.onViewBound()

        val fieldQuestionText = binding.fieldQuestionText
        val fieldNewAnswer = binding.fieldNewAnswer

        fieldQuestionText.imeOptions = EditorInfo.IME_ACTION_DONE
        fieldQuestionText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        fieldQuestionText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                keyboardController.hideKeyboard(fieldQuestionText)
                true
            } else false
        }

        fieldNewAnswer.imeOptions = EditorInfo.IME_ACTION_DONE
        fieldNewAnswer.setRawInputType(InputType.TYPE_CLASS_TEXT)
        fieldNewAnswer.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                keyboardController.hideKeyboard(fieldNewAnswer)
                true
            } else false
        }

        binding.buttonClose.setOnClickListener {
            dismiss()
        }

        binding.buttonSave.setOnClickListener {
            presenter.saveNewQuestion(binding.fieldQuestionText.text.toString())
        }

        binding.buttonAssignCategory.setOnClickListener {

        }

        binding.buttonAddAnswer.setOnClickListener {
            presenter.addAnswer(binding.fieldNewAnswer.text.toString())
        }
    }

    override fun displayQuestionText(questionText: String) {
        binding.fieldQuestionText.setText(questionText)
    }

    override fun displayAnswersDetails(answersCount: Int, correctAnswersCount: Int) {
        binding.allAnswersCount.text = String.format(answersCount.toString())
        binding.correctAnswersCount.text = String.format(correctAnswersCount.toString())
    }

    override fun displayAnswersList(answers: List<AnswerUIModel>) {
        answersListAdapter.updateData(answers)
    }

    override fun addNewAnswer(answer: AnswerUIModel) {
        answersListAdapter.itemAdded(answer)
        binding.fieldNewAnswer.setText("")
    }

    override fun removeAnswer(answerPosition: Int) {
        answersListAdapter.itemRemoved(answerPosition)
    }

    override fun displayLinkedCategories(categories: List<SimpleCategoryUIModel>) {
        categoriesPreviewAdapter.updateData(categories)
    }

    override fun displayCreatedOn(date: String, user: String) {
        binding.createdOnLabel.text = date
        binding.createdByLabel.text = user
    }

    override fun setupView() {
        categoriesPreviewAdapter = CategoriesPreviewAdapter()
        binding.categoriesRecyclerView.adapter = categoriesPreviewAdapter

        answersListAdapter = AnswersListAdapter(
            onAnswerChanged = { presenter.updateAnswer(it) }
        ) { answer: AnswerUIModel, position: Int -> presenter.removeAnswer(answer, position) }
        binding.answersRecyclerView.adapter = answersListAdapter
        binding.answersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.buttonSave.visibility = View.GONE
        binding.sectionAssignedCategories.visibility = View.VISIBLE
        binding.sectionCreationDetails.visibility = View.VISIBLE
        binding.sectionAnswersDetails.visibility = View.VISIBLE
        binding.sectionAnswers.visibility = View.VISIBLE
    }

    override fun setupNewElementView() {
        binding.buttonSave.visibility = View.VISIBLE
        binding.sectionAssignedCategories.visibility = View.GONE
        binding.sectionCreationDetails.visibility = View.GONE
        binding.sectionAnswersDetails.visibility = View.GONE
        binding.sectionAnswers.visibility = View.GONE

        keyboardController.showKeyboardWithDelay(binding.fieldQuestionText)
    }
}