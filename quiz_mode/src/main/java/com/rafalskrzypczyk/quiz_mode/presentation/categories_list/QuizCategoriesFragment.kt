package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.databinding.FragmentListBinding
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeInvisible
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

    private var noElementsView : View? = null

    override fun onViewBound() {
        super.onViewBound()

        actionMenuRes = R.menu.action_bar_quiz_mode
        actionMenuCallback = { actionMenuCallback(it) }

        adapter = CategoriesAdapter(
            onCategoryClicked = { openCategoryDetailsSheet(it.id) },
            onCategoryRemoved = { presenter.removeCategory(it) }
        )

        with(binding) {
            recyclerView.adapter = adapter
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val firstVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (firstVisibleItemPosition == 0) hideScrollToBottomPopover()
                    else showScrollToBottomPopover()
                }
            })

            popoverScrollUp.root.setOnClickListener {
                recyclerView.smoothScrollToPosition(0)
            }

            searchBar.setOnTextChanged { presenter.searchBy(it) }
            searchBar.setOnClearClick { presenter.searchBy("") }
        }


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

        if(binding.loading.root.isVisible) {
            QuizEditorAnimations.animateReplaceScaleOutExpandFromTop(binding.loading.root, binding.recyclerView)
        }
        if (noElementsView?.isVisible == true) {
            QuizEditorAnimations.animateReplaceScaleOutIn(noElementsView!!, binding.recyclerView)
        }
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
        if (noElementsView == null) {
            noElementsView = binding.stubEmptyList.inflate().apply { makeInvisible() }

            noElementsView?.findViewById<View>(com.rafalskrzypczyk.core.R.id.button_add_new)
                ?.setOnClickListener { openNewCategorySheet() }
        }

        when {
            binding.loading.root.isVisible -> {
                QuizEditorAnimations.animateReplaceScaleOutIn(binding.loading.root, noElementsView!!)
            }
            binding.recyclerView.isVisible -> {
                QuizEditorAnimations.animateReplaceScaleOutIn(binding.recyclerView, noElementsView!!)
            }
            else -> {
                noElementsView?.makeVisible()
            }
        }
    }

    override fun displayElementsCount(count: Int) {
        binding.searchBar.setElementsCount(count)
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
        binding.recyclerView.makeGone()
        binding.loading.root.makeVisible()
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }

    private fun showScrollToBottomPopover() {
        with(binding.popoverScrollUp.root) {
            if (isVisible) return
            QuizEditorAnimations.animateScaleIn(this)
        }
    }

    private fun hideScrollToBottomPopover() {
        with(binding.popoverScrollUp) {
            if (root.isGone) return
            QuizEditorAnimations.animateScaleOut(root)
        }
    }
}