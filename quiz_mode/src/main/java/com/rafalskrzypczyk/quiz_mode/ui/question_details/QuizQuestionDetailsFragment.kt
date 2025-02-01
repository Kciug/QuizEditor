package com.rafalskrzypczyk.quiz_mode.ui.question_details

import android.os.Bundle
import android.view.View
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizQuestionDetailsBinding
import com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.quiz_mode.ui.view_models.AnswerUIModel
import com.rafalskrzypczyk.quiz_mode.utils.ViewState

class QuizQuestionDetailsFragment(
    val bundle: Bundle? = null,
    onDismiss: () -> Unit,
) : BaseBottomSheetFragment<FragmentQuizQuestionDetailsBinding>(
    FragmentQuizQuestionDetailsBinding::inflate,
    onDismiss
), QuizQuestionDetailsContract.View {

    private lateinit var presenter: QuizQuestionDetailsPresenter
    private lateinit var categoriesPreviewAdapter: CategoriesPreviewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = QuizQuestionDetailsPresenter(this)
        presenter.getData(bundle)
    }

    override fun onViewBound() {
        super.onViewBound()

        binding.buttonClose.setOnClickListener {
            dismiss()
        }

        binding.buttonSave.setOnClickListener {
            presenter.saveNewQuestion(binding.questionTextField.text.toString())
        }

        binding.buttonAssignCategory.setOnClickListener {
            TODO("not yet implemented")
        }
    }

    override fun displayQuestionText(questionText: String) {
        binding.questionTextField.setText(questionText)
    }

    override fun displayAnswersList(
        answers: List<AnswerUIModel>,
        answersCount: Int,
        correctAnswersCount: Int
    ) {
        //categoriesPreviewAdapter.updateData(answers)
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

        binding.buttonSave.visibility = View.GONE
        binding.sectionAssignedCategories.visibility = View.VISIBLE
        binding.sectionCreationDetails.visibility = View.VISIBLE
        binding.sectionAnswersDetails.visibility = View.VISIBLE
        binding.answersRecyclerView.visibility = View.VISIBLE
    }

    override fun setupNewElementView() {
        binding.buttonSave.visibility = View.VISIBLE
        binding.sectionAssignedCategories.visibility = View.GONE
        binding.sectionCreationDetails.visibility = View.GONE
        binding.sectionAnswersDetails.visibility = View.GONE
        binding.answersRecyclerView.visibility = View.GONE
    }
}