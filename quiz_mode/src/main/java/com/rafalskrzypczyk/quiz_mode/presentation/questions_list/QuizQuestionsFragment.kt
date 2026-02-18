package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.QuizQuestionDetailsFragment
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionUIModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizQuestionsFragment :
    BaseFragment<FragmentListBinding, QuizQuestionsContract.View, QuizQuestionsContract.Presenter>(
        FragmentListBinding::inflate
    ), QuizQuestionsContract.View {

    private lateinit var adapter: QuestionsAdapter
    private lateinit var actionBarMenuBuilder: SortAndFilterMenuBuilder

    private var noElementsView : View? = null

    private var recyclerViewManager: LinearLayoutManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getData(arguments)
    }

    override fun onViewBound() {
        super.onViewBound()

        actionMenuRes = R.menu.action_bar_quiz_mode
        actionMenuCallback = { actionMenuCallback(it) }

        adapter = QuestionsAdapter(
            onItemClicked = { openQuestionDetailsSheet(it.id) },
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
                presenter.onNewElement()
                true
            }

            else -> false
        }
    }

    override fun displayQuestions(questions: List<QuestionUIModel>) {
        val isFirstItemVisible = recyclerViewManager?.findFirstVisibleItemPosition() == 0

        adapter.submitList(questions) {
            if(isFirstItemVisible)
                binding.recyclerView.scrollToPosition(0)
        }

        if (binding.loading.root.isVisible) {
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
            sortTypesList = sortTypes
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
                ?.setOnClickListener { presenter.onNewElement() }
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

    override fun displayNewElementSheet(categoryId: Long?) {
        val newQuestionSheetFragment = QuizQuestionDetailsFragment()
        categoryId?.let {
            val bundle = Bundle().apply { putLong("parentCategoryId", it) }
            newQuestionSheetFragment.apply { arguments = bundle }
        }
        newQuestionSheetFragment.show(parentFragmentManager, "NewQuestionBS")
    }

    override fun displayCategoryBadge(category: SimpleCategoryUIModel) {
        val categoryBadge = ColorOutlinedLabelView(requireContext()).apply {
            setColorAndText(category.color.toInt(), category.name)
            gravity = Gravity.CENTER
        }
        binding.headerAppendixRoot.apply {
            setPadding(10, 0, 10, 10)
            addView(categoryBadge)
        }
    }

    private fun openQuestionDetailsSheet(questionId: Long) {
        val bundle = Bundle().apply {
            putLong("questionId", questionId)
        }
        val bottomBarCategoryDetails = QuizQuestionDetailsFragment().apply { arguments = bundle }
        bottomBarCategoryDetails.show(parentFragmentManager, "QuestionDetailsBS")
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
