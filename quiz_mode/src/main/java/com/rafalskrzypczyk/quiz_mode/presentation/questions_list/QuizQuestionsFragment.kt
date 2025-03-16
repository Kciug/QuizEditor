package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.sort_filter.SortAndFilterMenuBuilder
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizQuestionsBinding
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.QuizQuestionDetailsFragment
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionUIModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizQuestionsFragment : BaseFragment<FragmentQuizQuestionsBinding>(
    FragmentQuizQuestionsBinding::inflate
), QuizQuestionsContract.View {
    @Inject
    lateinit var presenter: QuizQuestionsContract.Presenter

    private lateinit var adapter: QuestionsAdapter
    private lateinit var actionBarMenuBuilder: SortAndFilterMenuBuilder

    private var noElementsView: View? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.onAttach(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewCreated()
    }

    override fun onViewBound() {
        super.onViewBound()

        actionMenuRes = R.menu.action_bar_quiz_mode
        actionMenuCallback = { actionMenuCallback(it) }

        adapter = QuestionsAdapter(
            onItemClicked = { openQuestionDetailsSheet(it.id) },
            onItemDeleted = { presenter.removeQuestion(it) }
        )
        binding.recyclerViewQuestions.adapter = adapter

        binding.searchBar.setOnTextChanged { presenter.searchBy(it) }
        binding.searchBar.setOnClearClick { presenter.searchBy("") }

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
                openNewQuestionSheet()
                true
            }

            else -> false
        }
    }

    override fun displayQuestions(questions: List<QuestionUIModel>) {
        adapter.submitList(questions)
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
        if(noElementsView == null) {
            val stub = binding.stubEmptyList
            noElementsView = stub.inflate()
        }

        val buttonAddNew = noElementsView?.findViewById<View>(com.rafalskrzypczyk.core.R.id.button_add_new)
        buttonAddNew?.setOnClickListener { openNewQuestionSheet() }
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
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}