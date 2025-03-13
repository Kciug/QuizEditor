package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.rafalskrzypczyk.core.app_bar_handler.ActionBarBuilder
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.sort_filter.SortAndFilterMenuBuilder
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizCategoriesBinding
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.presentation.category_details.QuizCategoryDetailsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizCategoriesFragment : BaseFragment<FragmentQuizCategoriesBinding>(
    FragmentQuizCategoriesBinding::inflate
), QuizCategoriesContract.View {
    @Inject
    lateinit var presenter: QuizCategoriesContract.Presenter

    private lateinit var adapter: CategoriesAdapter

    private lateinit var actionBarMenuBuilder: SortAndFilterMenuBuilder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttach(this)
        presenter.onViewCreated()
    }

    override fun onViewBound() {
        super.onViewBound()

        adapter = CategoriesAdapter(
            onCategoryClicked = { openCategoryDetailsSheet(it.id) },
            onCategoryRemoved = { presenter.removeCategory(it) },
            onAddClicked = { openNewCategorySheet() },
        )
        binding.recyclerViewCategories.adapter = adapter

        binding.searchBar.setOnTextChanged { presenter.searchBy(it) }
        binding.searchBar.setOnClearClick { presenter.searchBy("") }

        (requireActivity() as ActionBarBuilder).setupActionBarMenu(R.menu.action_bar_quiz_mode) { actionMenuCallback(it) }

        actionBarMenuBuilder = SortAndFilterMenuBuilder(requireContext())
        actionBarMenuBuilder.setupOnSelectListeners(
            onSortOptionSelected = { presenter.sortByOption(it) },
            onSortTypeSelected = { presenter.sortByType(it) },
            onFilterSelected = { presenter.filterBy(it) }
        )
    }

    override fun onDestroyView() {
        presenter.onDestroy()
        super.onDestroyView()
    }

    private fun actionMenuCallback(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                presenter.onSortMenuOpened()
                true
            }

            R.id.action_filter -> {
                presenter.onFilterMenuOpened()
                true
            }

            R.id.action_add_new -> {
                openNewCategorySheet()
                true
            }

            else -> false
        }
    }

    override fun displayCategories(categories: List<Category>) {
        adapter.submitList(categories)
    }

    override fun displaySortMenu(
        sortOptions: List<SelectableMenuItem>,
        sortTypes: List<SelectableMenuItem>,
    ) {
        actionBarMenuBuilder.showSortMenu(
            anchorView = requireActivity().findViewById(R.id.action_sort),
            sortOptionsList = sortOptions,
            sortTypesList = sortTypes,
        )
    }

    override fun displayFilterMenu(filterOptions: List<SelectableMenuItem>) {
        actionBarMenuBuilder.showFilterMenu(
            ahchorView = requireActivity().findViewById(R.id.action_filter),
            filterOptions = filterOptions
        )
    }

    private fun openCategoryDetailsSheet(categoryId: Long) {
        val bundle = Bundle().apply {
            putLong("categoryId", categoryId)
        }
        val bottomBarCategoryDetails = QuizCategoryDetailsFragment().apply { arguments = bundle }
        bottomBarCategoryDetails.show(parentFragmentManager, "CategoryDetailsBS")
    }

    private fun openNewCategorySheet() {
        val bottomBarCategoryDetails = QuizCategoryDetailsFragment()
        bottomBarCategoryDetails.show(parentFragmentManager, "NewCategoryBS")
    }

    override fun displayLoading() {
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}