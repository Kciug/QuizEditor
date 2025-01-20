package com.rafalskrzypczyk.quiz_mode.ui

import com.rafalskrzypczyk.quiz_mode.CategoriesAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
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

    override fun displayCategories(categories: LiveData<List<Category>>) {
        adapter = CategoriesAdapter(
            onCategoryClicked = { category, position ->
                openCategoryDetailsSheet(category.id, position)
            },
            onAddClicked = { openNewCategorySheet() },
        )
        binding.categoryRecyclerView.adapter = adapter

        categories.observe(viewLifecycleOwner, Observer {updatedCategories ->
            adapter.submitList(updatedCategories)
        })
    }

    private fun openCategoryDetailsSheet(categoryId: Int, listPosition: Int){
        val bundle = Bundle().apply {
            putInt("categoryId", categoryId)
        }
        val bottomBarCategoryDetails = QuizCategoryDetailsFragment(bundle) { adapter.notifyItemChanged(listPosition) }

        bottomBarCategoryDetails.show(parentFragmentManager, "CategoryDetailsBS")
    }

    private fun openNewCategorySheet(){
        val bottomBarCategoryDetails = QuizCategoryDetailsFragment { adapter.notifyItemInserted(adapter.itemCount) }
        bottomBarCategoryDetails.show(parentFragmentManager, "CategoryDetailsBS")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}