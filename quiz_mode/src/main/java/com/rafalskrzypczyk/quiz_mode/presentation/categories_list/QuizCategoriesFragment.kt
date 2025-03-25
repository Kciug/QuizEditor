package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.databinding.FragmentListBinding
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.sort_filter.SortAndFilterMenuBuilder
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.presentation.category_details.QuizCategoryDetailsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizCategoriesFragment :
    BaseFragment<FragmentListBinding, QuizCategoriesContract.View, QuizCategoriesContract.Presenter>(
        FragmentListBinding::inflate
    ), QuizCategoriesContract.View {

    private lateinit var adapter: CategoriesAdapter
    private lateinit var actionBarMenuBuilder: SortAndFilterMenuBuilder

    private var noElementsView: View? = null

    override fun onViewBound() {
        super.onViewBound()

        actionMenuRes = R.menu.action_bar_quiz_mode
        actionMenuCallback = { actionMenuCallback(it) }

        adapter = CategoriesAdapter(
            onCategoryClicked = { openCategoryDetailsSheet(it.id) },
            onCategoryRemoved = { presenter.removeCategory(it) }
        )
        binding.recyclerViewCategories.adapter = adapter

        binding.searchBar.setOnTextChanged { presenter.searchBy(it) }
        binding.searchBar.setOnClearClick { presenter.searchBy("") }

        actionBarMenuBuilder = SortAndFilterMenuBuilder(requireContext())
        actionBarMenuBuilder.setupOnSelectListeners(
            onSortOptionSelected = { presenter.sortByOption(it) },
            onSortTypeSelected = { presenter.sortByType(it) },
            onFilterSelected = { presenter.filterBy(it) }
        )
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
        if(binding.loading.root.isVisible)
            QuizEditorAnimations.animateReplaceScaleOutExpandFromTop(binding.loading.root, binding.recyclerViewCategories)
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

    override fun displayNoElementsView() {
        if(noElementsView == null) {
            val stub = binding.stubEmptyList
            noElementsView = stub.inflate().apply {
                scaleX = 0f
                scaleY = 0f
            }

            QuizEditorAnimations.animateReplaceScaleOutIn(binding.loading.root, noElementsView!!)
        }

        val buttonAddNew = noElementsView?.findViewById<View>(com.rafalskrzypczyk.core.R.id.button_add_new)
        buttonAddNew?.setOnClickListener { openNewCategorySheet() }
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
        binding.recyclerViewCategories.makeGone()
        binding.loading.root.makeVisible()
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}