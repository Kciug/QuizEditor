package com.rafalskrzypczyk.quiz_mode.ui

import CategoriesAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

        navController = findNavController()

        presenter = QuizCategoriesPresenter(this)

        binding.categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        presenter.loadCategories()

        return root
    }

    override fun displayCategories(categories: List<Category>) {
        adapter = CategoriesAdapter(
            categories = categories,
            onCategoryClicked = { category ->
                openCategoryDetailsSheet(category.id)
            },
            onAddClicked = {
                openNewCategorySheet()
            },
        )
        binding.categoryRecyclerView.adapter = adapter
    }

    private fun openCategoryDetailsSheet(categoryId: Int){
        val bundle = Bundle().apply {
            putInt("categoryId", categoryId)
        }
        val bottomBarCategoryDetails = QuizCategoryDetailsFragment(bundle)
        bottomBarCategoryDetails.show(parentFragmentManager, "CategoryDetailsBS")
    }

    private fun openNewCategorySheet(){
        val bottomBarCategoryDetails = QuizCategoryDetailsFragment()
        bottomBarCategoryDetails.show(parentFragmentManager, "CategoryDetailsBS")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}