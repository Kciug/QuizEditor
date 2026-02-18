package com.rafalskrzypczyk.cem_mode.presentation.questions_list

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.cem_mode.R
import com.rafalskrzypczyk.cem_mode.presentation.questions_list.ui_models.CemQuestionUIModel
import com.rafalskrzypczyk.cem_mode.presentation.question_details.CemQuestionDetailsFragment
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.custom_views.ColorOutlinedLabelView
import com.rafalskrzypczyk.core.databinding.FragmentListBinding
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeInvisible
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.core.presentation.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.sort_filter.SortAndFilterMenuBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CemQuestionsFragment :
    BaseFragment<FragmentListBinding, CemQuestionsContract.View, CemQuestionsContract.Presenter>(
        FragmentListBinding::inflate
    ), CemQuestionsContract.View {

    private lateinit var adapter: CemQuestionsAdapter
    private lateinit var actionBarMenuBuilder: SortAndFilterMenuBuilder
    private var noElementsView: View? = null
    private var recyclerViewManager: LinearLayoutManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getData()
    }

    override fun onViewBound() {
        super.onViewBound()

        actionMenuRes = com.rafalskrzypczyk.core.R.menu.action_bar_edit_mode
        actionMenuCallback = { actionMenuCallback(it) }

        adapter = CemQuestionsAdapter(
            onItemClicked = { presenter.onQuestionClicked(it) },
            onItemDeleted = { presenter.removeQuestion(it) }
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
        }

        actionBarMenuBuilder = SortAndFilterMenuBuilder(requireContext())
        actionBarMenuBuilder.setupOnSelectListeners(
            onSortOptionSelected = { presenter.sortByOption(it.itemHashCode) },
            onSortTypeSelected = { presenter.sortByType(it.itemHashCode) },
            onFilterSelected = { presenter.filterBy(it.itemHashCode) }
        )

        parentFragmentManager.setFragmentResultListener("open_questions", this) { _, bundle ->
            val categoryId = bundle.getLong("categoryId")
            val title = bundle.getString("categoryTitle") ?: ""
            val color = bundle.getLong("categoryColor")
            presenter.filterByCategory(categoryId)
            displayCategoryBadge(SimpleCategoryUIModel(title, color))
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
                presenter.onAddNewQuestion()
                true
            }
            else -> false
        }
    }

    override fun displayQuestions(questions: List<CemQuestionUIModel>) {
        adapter.submitList(questions)
        if (binding.loading.root.isVisible) {
            QuizEditorAnimations.animateReplaceScaleOutExpandFromTop(binding.loading.root, binding.recyclerView)
        }
        if (noElementsView?.isVisible == true) {
            QuizEditorAnimations.animateReplaceScaleOutIn(noElementsView!!, binding.recyclerView)
        }
    }

    override fun displayNoElementsView() {
        if (noElementsView == null) {
            noElementsView = binding.stubEmptyList.inflate().apply { makeInvisible() }
            noElementsView?.findViewById<View>(com.rafalskrzypczyk.core.R.id.button_add_new)
                ?.setOnClickListener { presenter.onAddNewQuestion() }
        }
        when {
            binding.loading.root.isVisible -> QuizEditorAnimations.animateReplaceScaleOutIn(binding.loading.root, noElementsView!!)
            binding.recyclerView.isVisible -> QuizEditorAnimations.animateReplaceScaleOutIn(binding.recyclerView, noElementsView!!)
            else -> noElementsView?.makeVisible()
        }
    }

    override fun displayElementsCount(count: Int) {
        binding.searchBar.setElementsCount(count)
    }

    override fun displayLoading() {
        binding.recyclerView.makeGone()
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

    override fun displayCategoryBadge(category: SimpleCategoryUIModel) {
        binding.headerAppendixRoot.removeAllViews()
        val categoryBadge = ColorOutlinedLabelView(requireContext()).apply {
            setColorAndText(category.color.toInt(), category.name)
            gravity = Gravity.CENTER
            setOnClickListener {
                presenter.filterByCategory(null)
                binding.headerAppendixRoot.removeAllViews()
            }
        }
        binding.headerAppendixRoot.apply {
            setPadding(10, 0, 10, 10)
            addView(categoryBadge)
        }
    }

    override fun openQuestionDetails(questionId: Long?) {
        val bundle = Bundle().apply {
            questionId?.let { putLong("questionId", it) }
        }
        CemQuestionDetailsFragment().apply { arguments = bundle }.show(parentFragmentManager, "CemQuestionDetailsBS")
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
