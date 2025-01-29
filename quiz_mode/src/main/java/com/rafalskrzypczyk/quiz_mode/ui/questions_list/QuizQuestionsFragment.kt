package com.rafalskrzypczyk.quiz_mode.ui.questions_list

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizQuestionsBinding
import com.rafalskrzypczyk.quiz_mode.models.Question

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
        adapter = QuestionsAdapter()
        binding.questionRecyclerView.adapter = adapter

        adapter.submitList(questions)
    }
}