package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import android.content.Context
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem

class CategoriesSortAndFilter(private val context: Context) {
    private var onSortOptionSelected: ((SelectableMenuItem) -> Unit)? = null
    private var onSortTypeSelected: ((SelectableMenuItem) -> Unit)? = null
    private var onFilterSelected: ((SelectableMenuItem) -> Unit)? = null

    fun setupOnSelectListeners(
        onSortOptionSelected: (SelectableMenuItem) -> Unit,
        onSortTypeSelected: (SelectableMenuItem) -> Unit,
        onFilterSelected: (SelectableMenuItem) -> Unit
    ) {
        this.onSortOptionSelected = onSortOptionSelected
        this.onSortTypeSelected = onSortTypeSelected
        this.onFilterSelected = onFilterSelected
    }

    fun showSortMenu(anchorView: View, sortOptionsList: List<SelectableMenuItem>, sortTypesList: List<SelectableMenuItem>) {
        val sortOptionsGroupId = 0
        val sortTypesGroupId = 1
        val popup = PopupMenu(context, anchorView)

        val currentSortOption = sortOptionsList.find { it.isSelected } ?: sortOptionsList.first()
        val currentSortType = sortTypesList.find { it.isSelected } ?: sortTypesList.first()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            popup.menu.setGroupDividerEnabled(true)
            popup.menu.setGroupCheckable(sortOptionsGroupId, true, true)
            sortOptionsList.forEach {
                val item =
                    popup.menu.add(
                        sortOptionsGroupId,
                        it.hashCode(),
                        Menu.NONE,
                        context.getString(it.title)
                    )
                item.isCheckable = true
                if (it.isSelected) item.isChecked = true
            }
            popup.menu.setGroupCheckable(sortTypesGroupId, true, true)
            sortTypesList.forEach {
                val item =
                    popup.menu.add(
                        sortTypesGroupId,
                        it.hashCode(),
                        Menu.NONE,
                        context.getString(it.title)
                    )
                item.isCheckable = true
                if (it.isSelected) item.isChecked = true
            }
        } else {
            popup.menu.setGroupCheckable(sortOptionsGroupId, true, true)
            popup.menu.setGroupCheckable(sortTypesGroupId, true, true)
            popup.menu.addSubMenu(context.getString(R.string.sort_by_label)).apply {
                sortOptionsList.forEach {
                    val item = add(
                        sortOptionsGroupId,
                        it.hashCode(),
                        Menu.NONE,
                        context.getString(it.title)
                    )
                    item.isCheckable = true
                    if (it.isSelected) item.isChecked = true
                }
            }
            popup.menu.addSubMenu(context.getString(R.string.sort_type_label)).apply {
                sortTypesList.forEach {
                    val item = add(
                        sortTypesGroupId,
                        it.hashCode(),
                        Menu.NONE,
                        context.getString(it.title)
                    )
                    item.isCheckable = true
                    if (it.isSelected) item.isChecked = true
                }
            }
        }
        popup.setOnMenuItemClickListener { menuItem ->
            val selectedSortOption = sortOptionsList.find { it.hashCode() == menuItem.itemId }
            val selectedSortType = sortTypesList.find { it.hashCode() == menuItem.itemId }
            when {
                selectedSortOption != null -> {
                    menuItem.isChecked = !menuItem.isChecked
                    popup.menu.findItem(currentSortOption.hashCode()).isChecked = false
                    onSortOptionSelected?.invoke(selectedSortOption)
                }
                selectedSortType != null -> {
                    menuItem.isChecked = !menuItem.isChecked
                    popup.menu.findItem(currentSortType.hashCode()).isChecked = false
                    onSortTypeSelected?.invoke(selectedSortType)
                }
            }

            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
            menuItem.actionView = View(context)
            menuItem.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
                override fun onMenuItemActionExpand(p0: MenuItem): Boolean = false
                override fun onMenuItemActionCollapse(p0: MenuItem): Boolean = false
            })

            false
        }

        popup.show()
    }

    fun showFilterMenu(ahchorView: View, filterOptions: List<SelectableMenuItem>) {
        val popup = PopupMenu(context, ahchorView)

        filterOptions.forEach {
            if(it.subMenu != null) {
                val currentSubMenuItem = it.subMenu!!.find { it.isSelected }
                val subMenuTitle = if(currentSubMenuItem == null) context.getString(it.title)
                else "${context.getString(it.title)}: ${currentSubMenuItem.title}"

                popup.menu.addSubMenu(subMenuTitle).apply {
                    it.subMenu!!.forEach {
                        val item = add(Menu.NONE, it.hashCode(), Menu.NONE, context.getString(it.title))
                        item.isCheckable = true
                        if (it.isSelected) item.isChecked = true
                    }
                }
            } else {
                val item = popup.menu.add(
                    Menu.NONE,
                    it.hashCode(),
                    Menu.NONE,
                    context.getString(it.title)
                )
                item.isCheckable = true
                if(it.isSelected) item.isChecked = true
            }
        }

        popup.setOnMenuItemClickListener { menuItem ->
            val selectedFilter = filterOptions.flatMap { listOf(it) + (it.subMenu ?: emptyList()) }
                .find { it.itemHashCode == menuItem.itemId }

            selectedFilter?.let { onFilterSelected?.invoke(it) }
            true
        }

        popup.show()
    }
}