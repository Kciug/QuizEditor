package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeInvisible
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.sort_filter.SortAndFilterMenuBuilder
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizQuestionsBinding
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.QuizQuestionDetailsFragment
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionUIModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizQuestionsFragment :
    BaseFragment<FragmentQuizQuestionsBinding, QuizQuestionsContract.View, QuizQuestionsContract.Presenter>(
        FragmentQuizQuestionsBinding::inflate
    ), QuizQuestionsContract.View {

    private lateinit var adapter: QuestionsAdapter
    private lateinit var actionBarMenuBuilder: SortAndFilterMenuBuilder

    private var noElementsView : View? = null

    override fun onViewBound() {
        super.onViewBound()

        actionMenuRes = R.menu.action_bar_quiz_mode
        actionMenuCallback = { actionMenuCallback(it) }

        adapter = QuestionsAdapter(
            onItemClicked = { openQuestionDetailsSheet(it.id) },
            onItemDeleted = { presenter.removeQuestion(it) }
        )
        binding.recyclerView.adapter = adapter

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
                openNewQuestionSheet()
                true
            }

            else -> false
        }
    }

    override fun displayQuestions(questions: List<QuestionUIModel>) {
        adapter.submitList(questions)

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
                ?.setOnClickListener { openNewQuestionSheet() }
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

    private fun openQuestionDetailsSheet(questionId: Long) {
        val bundle = Bundle().apply {
            putLong("questionId", questionId)
        }
        val bottomBarCategoryDetails = QuizQuestionDetailsFragment().apply { arguments = bundle }
        bottomBarCategoryDetails.show(parentFragmentManager, "QuestionDetailsBS")
    }

    private fun openNewQuestionSheet() {
        val bottomBarCategoryDetails = QuizQuestionDetailsFragment()
        bottomBarCategoryDetails.show(parentFragmentManager, "NewQuestionBS")
    }

    override fun displayLoading() {
        binding.recyclerView.makeGone()
        binding.loading.root.makeVisible()
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}