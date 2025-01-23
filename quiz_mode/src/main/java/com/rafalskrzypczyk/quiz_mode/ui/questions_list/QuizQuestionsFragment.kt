package com.rafalskrzypczyk.quiz_mode.ui.questions_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizQuestionsBinding
import com.rafalskrzypczyk.quiz_mode.models.Question

class QuizQuestionsFragment : Fragment(), QuizQuestionsView {

    private var _binding: FragmentQuizQuestionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: QuestionsAdapter
    private lateinit var presenter: QuizQuestionsPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizQuestionsBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.questionRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        presenter = QuizQuestionsPresenter(this)
        presenter.loadAllQuestions()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun displayAllQuestions(questions: List<Question>) {
        adapter = QuestionsAdapter()
        binding.questionRecyclerView.adapter = adapter

        adapter.submitList(questions)
    }
}