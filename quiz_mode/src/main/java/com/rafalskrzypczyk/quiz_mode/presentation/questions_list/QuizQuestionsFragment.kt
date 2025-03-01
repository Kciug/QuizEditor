package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafalskrzypczyk.core.app_bar_handler.ActionBarBuilder
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizQuestionsBinding
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.QuizQuestionDetailsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizQuestionsFragment : BaseFragment<FragmentQuizQuestionsBinding>(
    FragmentQuizQuestionsBinding::inflate
), QuizQuestionsContract.View {
    @Inject
    lateinit var presenter: QuizQuestionsPresenter

    private lateinit var activity: ActionBarBuilder

    private lateinit var adapter: QuestionsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewQuestions.layoutManager = LinearLayoutManager(requireContext())
        adapter = QuestionsAdapter(
            onItemClicked = { question ->
                openQuestionDetailsSheet(question.id)
            },
            onItemDeleted = { question ->
                presenter.removeQuestion(question)
            },
            onAddClicked = { openNewQuestionSheet() }
        )
        binding.recyclerViewQuestions.adapter = adapter

        binding.searchBar.setOnTextChanged { presenter.onSearchQueryChanged(it) }
        binding.searchBar.setOnClearClick { presenter.onSearchQueryChanged("") }

        presenter.loadQuestions()

        activity = requireActivity() as ActionBarBuilder
        activity.setupActionBarMenu(R.menu.action_bar_quiz_mode) { actionMenuCallback(it) }
    }

    private fun actionMenuCallback(item: MenuItem): Boolean{
        return when (item.itemId) {
            R.id.action_filter_and_sort -> {
                true
            }
            R.id.action_add_new -> {
                openNewQuestionSheet()
                true
            }
            else -> false
        }
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