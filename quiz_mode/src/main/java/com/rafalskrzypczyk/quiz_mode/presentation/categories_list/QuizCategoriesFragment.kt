package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

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
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizCategoriesBinding
import com.rafalskrzypczyk.quiz_mode.domain.CategoryStatus
import com.rafalskrzypczyk.quiz_mode.domain.getTitle
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

    private lateinit var activity: ActionBarBuilder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())
        adapter = CategoriesAdapter(
            onCategoryClicked = { openCategoryDetailsSheet(it.id) },
            onCategoryRemoved = { presenter.removeCategory(it) },
            onAddClicked = { openNewCategorySheet() },
        )
        binding.recyclerViewCategories.adapter = adapter

        binding.searchBar.setOnTextChanged { presenter.searchBy(it) }
        binding.searchBar.setOnClearClick { presenter.searchBy("") }

        presenter.loadCategories()

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
                openNewCategorySheet()
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
            CategorySort.getSortOptions().forEach {
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
            CategorySort.getSortTypes().forEach {
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
                CategorySort.getSortOptions().forEach {
                    val item = add(
                        sortOptionsGroupId,
                        it.hashCode(),
                        Menu.NONE,
                        requireContext().getString(it.title)
                    )
                    item.isCheckable = true
                    if (presenter.getCurrentSortOption() == it) item.isChecked = true
                }
            }
            popup.menu.addSubMenu("Kolejność").apply {
                CategorySort.getSortTypes().forEach {
                    val item = add(
                        sortTypesGroupId,
                        it.hashCode(),
                        Menu.NONE,
                        requireContext().getString(it.title)
                    )
                    item.isCheckable = true
                    if (presenter.getCurrentSortType() == it) item.isChecked = true
                }
            }
        }
        popup.setOnMenuItemClickListener { menuItem ->
            val selectedSortOption =
                CategorySort.getSortOptions().find { it.hashCode() == menuItem.itemId }
            val selectedSortType =
                CategorySort.getSortTypes().find { it.hashCode() == menuItem.itemId }
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
        val currentFilter = presenter.getCurrentFilter()

        CategoryFilters.getFilters().forEach {
            if (it is CategoryFilters.ByStatus) {
                val currentStatus = currentFilter as? CategoryFilters.ByStatus
                val title = if (currentStatus == null) requireContext().getString(it.title)
                else "${requireContext().getString(it.title)}: ${currentStatus.status!!.getTitle(requireContext())}"
                popup.menu.addSubMenu(title).apply {
                    CategoryStatus.entries.forEach {
                        val item =
                            add(Menu.NONE, it.hashCode(), Menu.NONE, it.getTitle(requireContext()))
                        item.isCheckable = true
                        if (currentFilter is CategoryFilters.ByStatus && currentFilter.status == it)
                            item.isChecked = true
                    }
                }
            } else {
                val item = popup.menu.add(
                    Menu.NONE,
                    it.hashCode(),
                    Menu.NONE,
                    requireContext().getString(it.title)
                )
                item.isCheckable = true
                if (presenter.getCurrentFilter() == it) item.isChecked = true
            }
        }

        popup.setOnMenuItemClickListener { menuItem ->
            val selectedFilter =
                CategoryFilters.getFilters().find { it.hashCode() == menuItem.itemId }
            val selectedFilterStatus = CategoryStatus.entries.find { it.hashCode() == menuItem.itemId }
            if (selectedFilter != null && selectedFilter !is CategoryFilters.ByStatus) presenter.filterBy(selectedFilter)
            if (selectedFilterStatus != null) presenter.filterBy(CategoryFilters.ByStatus(selectedFilterStatus))

            false
        }

        popup.show()
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