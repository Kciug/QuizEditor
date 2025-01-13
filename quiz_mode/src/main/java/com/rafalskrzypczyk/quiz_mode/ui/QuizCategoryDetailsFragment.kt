package com.rafalskrzypczyk.quiz_mode.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizCategoryDetailsBinding
import com.rafalskrzypczyk.quiz_mode.models.Category
import com.rafalskrzypczyk.quiz_mode.presenters.QuizCategoryDetailsPresenter

class QuizCategoryDetailsFragment : Fragment(), QuizCategoryDetailsView {

    private var _binding: FragmentQuizCategoryDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: QuizCategoryDetailsPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizCategoryDetailsBinding.inflate(inflater, container, false)
        val root = binding.root

        presenter = QuizCategoryDetailsPresenter(this)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryId = arguments?.getInt("categoryId")
        if(categoryId == null) return

        presenter.loadCategoryById(categoryId)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun displayCategoryDetails(category: Category) {
        binding.categoryNameField.setText(category.title)
        binding.categoryDescriptionField.setText(category.description)
        binding.categoryQuestionsCount.text = String.format(category.questionAmount.toString())
    }
}