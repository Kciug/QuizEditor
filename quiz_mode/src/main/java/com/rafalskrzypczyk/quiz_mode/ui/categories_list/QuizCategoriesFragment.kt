package com.rafalskrzypczyk.quiz_mode.ui.categories_list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizCategoriesBinding
import com.rafalskrzypczyk.quiz_mode.models.Category
import com.rafalskrzypczyk.quiz_mode.ui.categeory_details.QuizCategoryDetailsFragment

class QuizCategoriesFragment : BaseFragment<FragmentQuizCategoriesBinding>(
    FragmentQuizCategoriesBinding::inflate), QuizCategoriesView
{
    private lateinit var presenter: QuizCategoriesPresenter
    private lateinit var adapter: CategoriesAdapter

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        presenter = QuizCategoriesPresenter(this)

        binding.categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        presenter.loadCategories()
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
        val bottomBarCategoryDetails =
            QuizCategoryDetailsFragment(bundle) { adapter.notifyItemChanged(listPosition) }

        bottomBarCategoryDetails.show(parentFragmentManager, "CategoryDetailsBS")
    }

    private fun openNewCategorySheet(){
        val bottomBarCategoryDetails =
            QuizCategoryDetailsFragment { adapter.notifyItemInserted(adapter.itemCount) }
        bottomBarCategoryDetails.show(parentFragmentManager, "CategoryDetailsBS")
    }
}