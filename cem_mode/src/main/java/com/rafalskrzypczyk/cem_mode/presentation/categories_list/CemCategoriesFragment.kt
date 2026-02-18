package com.rafalskrzypczyk.cem_mode.presentation.categories_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.cem_mode.R
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.cem_mode.presentation.CemModeFragment
import com.rafalskrzypczyk.cem_mode.presentation.category_details.CemCategoryDetailsFragment
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.databinding.FragmentListBinding
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeInvisible
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.sort_filter.SortAndFilterMenuBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CemCategoriesFragment :
    BaseFragment<FragmentListBinding, CemCategoriesContract.View, CemCategoriesContract.Presenter>(
        FragmentListBinding::inflate
    ), CemCategoriesContract.View {

    private lateinit var adapter: CemCategoriesAdapter
    private lateinit var actionBarMenuBuilder: SortAndFilterMenuBuilder
    private var noElementsView: View? = null
    private var recyclerViewManager: LinearLayoutManager? = null
    
    private var breadcrumbsContainer: LinearLayout? = null

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            presenter.onBackAction()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parentId = arguments?.getLong("parentCategoryID") ?: CemCategory.ROOT_ID
        presenter.getData(parentId)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
    }

    override fun onPause() {
        super.onPause()
        activityActionBarBuilder?.showBackArrow(false)
    }

    override fun onResume() {
        super.onResume()
        presenter.refreshUI()
    }

    override fun onViewBound() {
        super.onViewBound()

        actionMenuRes = com.rafalskrzypczyk.core.R.menu.action_bar_edit_mode
        actionMenuCallback = { actionMenuCallback(it) }

        adapter = CemCategoriesAdapter(
            onCategoryClicked = { openCategoryDetails(it.id, null) },
            onCategoryRemoved = { presenter.removeCategory(it) }
        )

        with(binding) {
            recyclerView.adapter = adapter
            recyclerViewManager = recyclerView.layoutManager as LinearLayoutManager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val firstVisibleItemPosition = recyclerViewManager?.findFirstVisibleItemPosition()
                    if (firstVisibleItemPosition == 0) hideScrollToBottomPopover()
                    else showScrollToBottomPopover()
                }
            })

            popoverScrollUp.root.setOnClickListener {
                recyclerView.smoothScrollToPosition(0)
            }

            searchBar.setOnTextChanged { presenter.searchBy(it) }
            searchBar.setOnClearClick { presenter.searchBy("") }
            
            val breadcrumbsView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_breadcrumbs, headerAppendixRoot, false)
            breadcrumbsContainer = breadcrumbsView.findViewById(R.id.breadcrumbs_container)
            headerAppendixRoot.addView(breadcrumbsView)
        }

        actionBarMenuBuilder = SortAndFilterMenuBuilder(requireContext())
        actionBarMenuBuilder.setupOnSelectListeners(
            onSortOptionSelected = { presenter.sortByOption(it.itemHashCode) },
            onSortTypeSelected = { presenter.sortByType(it.itemHashCode) },
            onFilterSelected = { presenter.filterBy(it.itemHashCode) }
        )

        parentFragmentManager.setFragmentResultListener("open_subcategory", this) { _, bundle ->
            val parentId = bundle.getLong("parentId")
            presenter.onBreadcrumbClicked(parentId)
        }
        
        parentFragmentManager.setFragmentResultListener("navigate_to_questions", this) { _, bundle ->
            findNavController().navigate(R.id.navigation_cem_questions, bundle)
        }
    }

    private fun actionMenuCallback(item: MenuItem): Boolean {
        return when (item.itemId) {
            com.rafalskrzypczyk.core.R.id.action_sort -> {
                presenter.onSortMenuOpened()
                true
            }
            com.rafalskrzypczyk.core.R.id.action_filter -> {
                presenter.onFilterMenuOpened()
                true
            }
            com.rafalskrzypczyk.core.R.id.action_add_new -> {
                presenter.onAddNewCategory()
                true
            }
            else -> false
        }
    }

    override fun displayCategories(categories: List<CemCategory>) {
        adapter.submitList(categories)
        
        if (binding.loading.root.isVisible) {
            QuizEditorAnimations.animateReplaceScaleOutExpandFromTop(binding.loading.root, binding.recyclerView)
            noElementsView?.makeGone()
        } else if (noElementsView?.isVisible == true) {
            QuizEditorAnimations.animateReplaceScaleOutIn(noElementsView!!, binding.recyclerView)
        } else {
            binding.recyclerView.makeVisible()
            noElementsView?.makeGone()
        }
    }

    override fun displayNoElementsView() {
        if (noElementsView == null) {
            noElementsView = binding.stubEmptyList.inflate().apply { makeInvisible() }
            noElementsView?.findViewById<View>(com.rafalskrzypczyk.core.R.id.button_add_new)
                ?.setOnClickListener { presenter.onAddNewCategory() }
        }
        
        if (noElementsView?.isVisible == true) return

        when {
            binding.loading.root.isVisible -> {
                QuizEditorAnimations.animateReplaceScaleOutIn(binding.loading.root, noElementsView!!)
                binding.recyclerView.makeGone()
            }
            binding.recyclerView.isVisible -> {
                QuizEditorAnimations.animateReplaceScaleOutIn(binding.recyclerView, noElementsView!!)
            }
            else -> {
                noElementsView?.makeVisible()
                binding.recyclerView.makeGone()
            }
        }
    }

    override fun displayElementsCount(count: Int) {
        binding.searchBar.setElementsCount(count)
    }

    override fun displayLoading() {
        binding.recyclerView.animate().cancel()
        noElementsView?.animate()?.cancel()
        
        binding.recyclerView.makeGone()
        noElementsView?.makeGone()
        binding.loading.root.makeVisible()
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }

    override fun displaySortMenu(sortOptions: List<SelectableMenuItem>, sortTypes: List<SelectableMenuItem>) {
        actionBarMenuBuilder.showSortMenu(
            anchorView = requireActivity().findViewById(com.rafalskrzypczyk.core.R.id.action_sort),
            sortOptionsList = sortOptions,
            sortTypesList = sortTypes
        )
    }

    override fun displayFilterMenu(filterOptions: List<SelectableMenuItem>) {
        actionBarMenuBuilder.showFilterMenu(
            ahchorView = requireActivity().findViewById(com.rafalskrzypczyk.core.R.id.action_filter),
            filterOptions = filterOptions
        )
    }

    override fun updateBreadcrumbs(path: List<CemCategory>) {
        breadcrumbsContainer?.removeAllViews()
        
        addBreadcrumbItem("Root", CemCategory.ROOT_ID)
        
        path.forEach { category ->
            addBreadcrumbItem(" > ", -2L)
            addBreadcrumbItem(category.title, category.id)
        }
        
        val isDeep = path.isNotEmpty()
        backPressedCallback.isEnabled = isDeep
        activityActionBarBuilder?.showBackArrow(isDeep) { presenter.onBackAction() }
    }
    
    private fun addBreadcrumbItem(title: String, id: Long) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_breadcrumb, breadcrumbsContainer, false) as TextView
        view.text = title
        if (id != -2L) {
            view.setOnClickListener { presenter.onBreadcrumbClicked(id) }
        } else {
            view.setTextColor(requireContext().getColor(com.rafalskrzypczyk.core.R.color.text_secondary))
        }
        breadcrumbsContainer?.addView(view)
    }

    override fun openCategoryDetails(categoryId: Long?, parentId: Long?) {
        val bundle = Bundle().apply {
            categoryId?.let { putLong("categoryId", it) }
            parentId?.let { putLong("parentCategoryID", it) }
        }
        CemCategoryDetailsFragment().apply { arguments = bundle }.show(parentFragmentManager, "CemCategoryDetailsBS")
    }

    private fun showScrollToBottomPopover() {
        if (binding.popoverScrollUp.root.isVisible) return
        QuizEditorAnimations.animateScaleIn(binding.popoverScrollUp.root)
    }

    private fun hideScrollToBottomPopover() {
        if (binding.popoverScrollUp.root.isGone) return
        QuizEditorAnimations.animateScaleOut(binding.popoverScrollUp.root)
    }
}
