package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizQuestionsBinding
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.QuestionUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.QuizQuestionDetailsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizQuestionsFragment : BaseFragment<FragmentQuizQuestionsBinding>(
    FragmentQuizQuestionsBinding::inflate
), QuizQuestionsContract.View {
    @Inject
    lateinit var presenter: QuizQuestionsPresenter

    private lateinit var adapter: QuestionsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.questionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = QuestionsAdapter(
            onItemClicked = { question ->
                openQuestionDetailsSheet(question.id)
            },
            onAddClicked = { openNewQuestionSheet() }
        )
        binding.questionRecyclerView.adapter = adapter

        presenter.loadQuestions()
    }

    override fun displayQuestions(questions: List<QuestionUIModel>) {
        adapter.submitList(questions)
    }

    private fun openQuestionDetailsSheet(questionId: Int) {
        val bundle = Bundle().apply {
            putInt("questionId", questionId)
        }
        val bottomBarCategoryDetails = QuizQuestionDetailsFragment().apply { arguments = bundle }
        bottomBarCategoryDetails.setOnDismiss { presenter.loadQuestions() }

        bottomBarCategoryDetails.show(parentFragmentManager, "QuestionDetailsBS")
    }

    private fun openNewQuestionSheet() {
        val bottomBarCategoryDetails = QuizQuestionDetailsFragment()
        bottomBarCategoryDetails.setOnDismiss { presenter.loadQuestions() }
        bottomBarCategoryDetails.show(parentFragmentManager, "NewQuestionBS")
    }
}