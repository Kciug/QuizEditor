package com.rafalskrzypczyk.cem_mode.presentation.question_details

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.rafalskrzypczyk.cem_mode.databinding.FragmentCemQuestionDetailsBinding
import com.rafalskrzypczyk.cem_mode.presentation.question_details.ui_models.CemAnswerUIModel
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeInvisible
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.core.extensions.setupMultilineWithIMEAction
import com.rafalskrzypczyk.core.presentation.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.core.utils.KeyboardController
import com.rafalskrzypczyk.core.presentation.adapters.CategoriesPreviewAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CemQuestionDetailsFragment :
    BaseBottomSheetFragment<FragmentCemQuestionDetailsBinding, CemQuestionDetailsContract.View, CemQuestionDetailsContract.Presenter>(
        FragmentCemQuestionDetailsBinding::inflate
    ), CemQuestionDetailsContract.View {

    private lateinit var keyboardController: KeyboardController
    private lateinit var answersAdapter: CemAnswersAdapter
    private lateinit var categoriesPreviewAdapter: CategoriesPreviewAdapter
    private var isSilentUpdate = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keyboardController = KeyboardController(requireContext())
        presenter.getData(arguments)
    }

    override fun onViewBound() {
        super.onViewBound()
        keyboardController = KeyboardController(requireContext())
        
        answersAdapter = CemAnswersAdapter(
            keyboardController = keyboardController,
            onAnswerChanged = { 
                if (!isSilentUpdate) {
                    presenter.updateAnswerText(it.id, it.answerText)
                    presenter.updateAnswerCorrectness(it.id, it.isCorrect) 
                }
            },
            onAnswerRemoved = { presenter.deleteAnswer(it.id) }
        )
        categoriesPreviewAdapter = CategoriesPreviewAdapter()

        with(binding) {
            rvAnswers.adapter = answersAdapter
            questionDetails.categoriesRecyclerView.adapter = categoriesPreviewAdapter
            
            sectionNavbar.buttonClose.setOnClickListener { dismiss() }
            sectionNavbar.buttonSave.setOnClickListener {
                presenter.createNewQuestion(
                    questionDetails.inputQuestionText.text.toString(),
                    questionDetails.inputExplanation.text.toString()
                )
            }
            questionDetails.btnAssignCategory.setOnClickListener { presenter.onAssignCategory() }
            newAnswerBar.btnAddAnswer.setOnClickListener {
                presenter.addAnswer(newAnswerBar.inputNewAnswer.text.toString())
                newAnswerBar.inputNewAnswer.text?.clear()
            }
        }
    }

    override fun setupView() {
        with(binding) {
            content.visibility = View.VISIBLE
            contentDetails.visibility = View.VISIBLE
            sectionNavbar.buttonSave.visibility = View.GONE

            with(questionDetails) {
                inputQuestionText.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_DONE)
                inputQuestionText.addTextChangedListener(afterTextChanged = { 
                    if (!isSilentUpdate) presenter.updateQuestionText(it.toString()) 
                })

                inputExplanation.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_DONE)
                inputExplanation.addTextChangedListener(afterTextChanged = { 
                    if (!isSilentUpdate) presenter.updateExplanation(it.toString()) 
                })
            }
        }
    }

    override fun setupNewElementView() {
        with(binding) {
            content.visibility = View.GONE
            contentDetails.visibility = View.VISIBLE
            sectionNavbar.buttonSave.visibility = View.VISIBLE

            questionDetails.inputQuestionText.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_DONE)
            questionDetails.inputExplanation.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_DONE)
            keyboardController.showKeyboardWithDelay(questionDetails.inputQuestionText)
        }
    }

    override fun displayQuestionDetails(questionText: String, explanation: String) {
        isSilentUpdate = true
        binding.questionDetails.inputQuestionText.setText(questionText)
        binding.questionDetails.inputExplanation.setText(explanation)
        isSilentUpdate = false
    }

    override fun displayCreatedDetails(date: String) {
        binding.creationDetails.labelCreationDate.text = date
    }

    override fun displayAnswersCount(total: Int, correct: Int) {
        with(binding.answersDetails) {
            countAnswers.text = total.toString()
            countCorrectAnswers.text = correct.toString()
        }
    }

    override fun displayAnswers(answers: List<CemAnswerUIModel>) {
        isSilentUpdate = true
        answersAdapter.submitList(answers)
        isSilentUpdate = false
    }

    override fun displayLinkedCategories(categories: List<SimpleCategoryUIModel>) {
        categoriesPreviewAdapter.submitList(categories)
        binding.questionDetails.labelNoCategories.visibility = if (categories.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun displayCategoriesPicker() {
        // TODO: Implement categories picker for CEM
    }

    override fun displayToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun displayContent() {
        binding.content.makeVisible()
        binding.contentDetails.makeVisible()
        binding.loading.root.makeGone()
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
