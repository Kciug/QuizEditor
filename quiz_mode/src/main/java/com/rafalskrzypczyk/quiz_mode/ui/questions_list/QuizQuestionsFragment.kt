package com.rafalskrzypczyk.quiz_mode.ui.questions_list

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizQuestionsBinding
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.ui.question_details.QuizQuestionDetailsFragment

class QuizQuestionsFragment : BaseFragment<FragmentQuizQuestionsBinding>(FragmentQuizQuestionsBinding::inflate),
    QuizQuestionsView
{
    private lateinit var adapter: QuestionsAdapter
    private lateinit var presenter: QuizQuestionsPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.questionRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        presenter = QuizQuestionsPresenter(this)
        presenter.loadAllQuestions()
    }

    override fun displayAllQuestions(questions: List<Question>) {
        adapter = QuestionsAdapter(
            onItemClicked = { question, position ->
                openQuestionDetailsSheet(question.id, position)
            },
            onAddClicked = { openNewQuestionSheet() }
        )
        binding.questionRecyclerView.adapter = adapter

        adapter.submitList(questions)
    }

    private fun openQuestionDetailsSheet(questionId: Int, listPosition: Int){
        val bundle = Bundle().apply {
            putInt("questionId", questionId)
        }
        val bottomBarCategoryDetails =
            QuizQuestionDetailsFragment(bundle) { adapter.notifyItemChanged(listPosition) }

        bottomBarCategoryDetails.show(parentFragmentManager, "QuestionDetailsBS")
    }

    private fun openNewQuestionSheet(){
        val bottomBarCategoryDetails =
            QuizQuestionDetailsFragment { adapter.notifyItemInserted(adapter.itemCount) }
        bottomBarCategoryDetails.show(parentFragmentManager, "NewQuestionBS")
    }
}