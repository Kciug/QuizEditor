package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizCategoriesBinding
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.presentation.categeory_details.QuizCategoryDetailsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizCategoriesFragment : BaseFragment<FragmentQuizCategoriesBinding>(
    FragmentQuizCategoriesBinding::inflate
), QuizCategoriesContract.View {
    @Inject
    lateinit var presenter: QuizCategoriesPresenter

    private lateinit var adapter: CategoriesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())
        adapter = CategoriesAdapter(
            onCategoryClicked = { openCategoryDetailsSheet(it.id) },
            onCategoryRemoved = { presenter.removeCategory(it) },
            onAddClicked = { openNewCategorySheet() },
        )
        binding.recyclerViewCategories.adapter = adapter

        binding.searchBar.setOnTextChanged { presenter.onSearchQueryChanged(it) }
        binding.searchBar.setOnClearClick { presenter.onSearchQueryChanged("") }

        presenter.loadCategories()
    }

    override fun displayCategories(categories: List<Category>) {
        adapter.submitList(categories)
    }

    private fun openCategoryDetailsSheet(categoryId: Int) {
        val bundle = Bundle().apply {
            putInt("categoryId", categoryId)
        }
        val bottomBarCategoryDetails =
            QuizCategoryDetailsFragment().apply { arguments = bundle }
        bottomBarCategoryDetails.setOnDismiss { presenter.loadCategories() }

        bottomBarCategoryDetails.show(parentFragmentManager, "CategoryDetailsBS")
    }

    private fun openNewCategorySheet() {
        val bottomBarCategoryDetails = QuizCategoryDetailsFragment()
        bottomBarCategoryDetails.setOnDismiss { presenter.loadCategories() }
        bottomBarCategoryDetails.show(parentFragmentManager, "CategoryDetailsBS")
    }
}