package com.rafalskrzypczyk.quiz_mode.ui

import CategoriesAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizCategoriesBinding
import com.rafalskrzypczyk.quiz_mode.models.Category
import com.rafalskrzypczyk.quiz_mode.presenters.QuizCategoriesPresenter

class QuizCategoriesFragment : Fragment(), QuizCategoriesView {
    private var _binding: FragmentQuizCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: QuizCategoriesPresenter
    private lateinit var adapter: CategoriesAdapter

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizCategoriesBinding.inflate(inflater, container, false)
        val root = binding.root

        //val navHostFragment = parentFragmentManager.findFragmentById(R.id.nav_host_fragment_quiz_mode) as NavHostFragment
        navController = findNavController()

        presenter = QuizCategoriesPresenter(this)

        binding.categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        presenter.loadCategories()

        return root
    }

    override fun displayCategories(categories: List<Category>) {
        adapter = CategoriesAdapter(categories) { category ->
            val bundle = Bundle().apply {
                putInt("categoryId", category.id)
            }
            navController.navigate(R.id.navigation_category_details, bundle)
        }
        binding.categoryRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}