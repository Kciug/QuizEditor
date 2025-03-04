package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafalskrzypczyk.core.app_bar_handler.ActionBarBuilder
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizQuestionsBinding
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.QuizQuestionDetailsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizQuestionsFragment : BaseFragment<FragmentQuizQuestionsBinding>(
    FragmentQuizQuestionsBinding::inflate
), QuizQuestionsContract.View {
    @Inject
    lateinit var presenter: QuizQuestionsPresenter

    private lateinit var activity: ActionBarBuilder

    private lateinit var adapter: QuestionsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewQuestions.layoutManager = LinearLayoutManager(requireContext())
        adapter = QuestionsAdapter(
            onItemClicked = { question ->
                openQuestionDetailsSheet(question.id)
            },
            onItemDeleted = { question ->
                presenter.removeQuestion(question)
            },
            onAddClicked = { openNewQuestionSheet() }
        )
        binding.recyclerViewQuestions.adapter = adapter

        binding.searchBar.setOnTextChanged { presenter.searchBy(it) }
        binding.searchBar.setOnClearClick { presenter.searchBy("") }

        presenter.loadQuestions()

        activity = requireActivity() as ActionBarBuilder
        activity.setupActionBarMenu(R.menu.action_bar_quiz_mode) { actionMenuCallback(it) }
    }

    private fun actionMenuCallback(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                showSortMenu()
                true
            }

            R.id.action_filter -> {
                showFilterMenu()
                true
            }

            R.id.action_add_new -> {
                openNewQuestionSheet()
                true
            }

            else -> false
        }
    }

    private fun showSortMenu() {
        val sortOptionsGroupId = 0
        val sortTypesGroupId = 1
        val popup = PopupMenu(requireContext(), requireActivity().findViewById(R.id.action_sort))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            popup.menu.setGroupDividerEnabled(true)
            popup.menu.setGroupCheckable(sortOptionsGroupId, true, true)
            QuestionSort.getSortOptions().forEach {
                val item =
                    popup.menu.add(
                        sortOptionsGroupId,
                        it.hashCode(),
                        Menu.NONE,
                        requireContext().getString(it.title)
                    )
                item.isCheckable = true
                if (presenter.getCurrentSortOption() == it) item.isChecked = true
            }
            popup.menu.setGroupCheckable(sortTypesGroupId, true, true)
            QuestionSort.getSortTypes().forEach {
                val item =
                    popup.menu.add(
                        sortTypesGroupId,
                        it.hashCode(),
                        Menu.NONE,
                        requireContext().getString(it.title)
                    )
                item.isCheckable = true
                if (presenter.getCurrentSortType() == it) item.isChecked = true
            }
        } else {
            popup.menu.setGroupCheckable(sortOptionsGroupId, true, true)
            popup.menu.setGroupCheckable(sortTypesGroupId, true, true)
            popup.menu.addSubMenu("Sortuj po").apply {
                QuestionSort.getSortOptions().forEach {
                    val item = add(
                        sortOptionsGroupId,
                        it.hashCode(),
                        Menu.NONE,
                        requireContext().getString(it.title)
                    )
                    if (presenter.getCurrentSortOption() == it) item.isChecked = true
                }
            }
            popup.menu.addSubMenu("Kolejność").apply {
                QuestionSort.getSortTypes().forEach {
                    val item = add(
                        sortTypesGroupId,
                        it.hashCode(),
                        Menu.NONE,
                        requireContext().getString(it.title)
                    )
                    if (presenter.getCurrentSortType() == it) item.isChecked = true
                }
            }
        }

        popup.setForceShowIcon(true)

        popup.setOnMenuItemClickListener { menuItem ->
            val selectedSortOption =
                QuestionSort.getSortOptions().find { it.hashCode() == menuItem.itemId }
            val selectedSortType =
                QuestionSort.getSortTypes().find { it.hashCode() == menuItem.itemId }
            when {
                selectedSortOption != null -> {
                    menuItem.isChecked = !menuItem.isChecked
                    popup.menu.findItem(presenter.getCurrentSortOption().hashCode()).isChecked = false
                    presenter.sortByOption(selectedSortOption)
                }
                selectedSortType != null -> {
                    menuItem.isChecked = !menuItem.isChecked
                    popup.menu.findItem(presenter.getCurrentSortType().hashCode()).isChecked = false
                    presenter.sortByType(selectedSortType)
                }
            }

            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
            menuItem.actionView = View(requireContext())
            menuItem.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
                override fun onMenuItemActionExpand(p0: MenuItem): Boolean = false
                override fun onMenuItemActionCollapse(p0: MenuItem): Boolean = false
            })

            false
        }

        popup.show()
    }

    private fun showFilterMenu() {
        val popup = PopupMenu(requireContext(), requireActivity().findViewById(R.id.action_filter))

        QuestionFilter.getFilters().forEach {
            val item = popup.menu.add(
                Menu.NONE,
                it.hashCode(),
                Menu.NONE,
                requireContext().getString(it.title)
            )
            item.isCheckable = true
            if (presenter.getCurrentFilter() == it) item.isChecked = true
        }

        popup.setOnMenuItemClickListener { menuItem ->
            val selectedFilter =
                QuestionFilter.getFilters().find { it.hashCode() == menuItem.itemId }
            if (selectedFilter != null) presenter.filterBy(selectedFilter)
            false
        }

        popup.show()
    }

    override fun displayQuestions(questions: List<QuestionUIModel>) {
        adapter.submitList(questions)
    }

    private fun openQuestionDetailsSheet(questionId: Int) {
        val bundle = Bundle().apply {
            putInt("questionId", questionId)
        }
        val bottomBarCategoryDetails = QuizQuestionDetailsFragment().apply { arguments = bundle }
        bottomBarCategoryDetails.setOnDismiss { presenter.loadQuestions() }

        bottomBarCategoryDetails.show(parentFragmentManager, "QuestionDetailsBS")
    }

    private fun openNewQuestionSheet() {
        val bottomBarCategoryDetails = QuizQuestionDetailsFragment()
        bottomBarCategoryDetails.setOnDismiss { presenter.loadQuestions() }
        bottomBarCategoryDetails.show(parentFragmentManager, "NewQuestionBS")
    }
}