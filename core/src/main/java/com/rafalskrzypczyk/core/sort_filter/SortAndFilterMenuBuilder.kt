package com.rafalskrzypczyk.core.sort_filter

import android.content.Context
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.rafalskrzypczyk.core.R

class SortAndFilterMenuBuilder(private val context: Context) {
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

        var currentSortOption = sortOptionsList.find { it.isSelected } ?: sortOptionsList.first()
        var currentSortType = sortTypesList.find { it.isSelected } ?: sortTypesList.first()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            popup.menu.setGroupDividerEnabled(true)
            addMenuItems(popup.menu, sortOptionsList, sortOptionsGroupId)
            addMenuItems(popup.menu, sortTypesList, sortTypesGroupId)
        } else {
            popup.menu.addSubMenu(context.getString(R.string.sort_by_label)).apply {
                addMenuItems(this, sortOptionsList, sortOptionsGroupId)
            }
            popup.menu.addSubMenu(context.getString(R.string.sort_type_label)).apply {
                addMenuItems(this, sortTypesList, sortTypesGroupId)
            }
        }

        popup.setOnMenuItemClickListener { menuItem ->
            val selectedSortOption = sortOptionsList.find { it.hashCode() == menuItem.itemId }
            val selectedSortType = sortTypesList.find { it.hashCode() == menuItem.itemId }

            when {
                selectedSortOption != null -> {
                    if(selectedSortOption.hashCode() != currentSortOption.hashCode()){
                        menuItem.isChecked = !menuItem.isChecked
                        popup.menu.findItem(currentSortOption.hashCode()).isChecked = false
                        currentSortOption = selectedSortOption

                        onSortOptionSelected?.invoke(selectedSortOption)
                    }
                }
                selectedSortType != null -> {
                    if(selectedSortType.hashCode() != currentSortType.hashCode()){
                        menuItem.isChecked = !menuItem.isChecked
                        popup.menu.findItem(currentSortType.hashCode()).isChecked = false
                        currentSortType = selectedSortType

                        onSortTypeSelected?.invoke(selectedSortType)
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                menuItem.actionView = View(context)
                menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(p0: MenuItem): Boolean = false
                    override fun onMenuItemActionCollapse(p0: MenuItem): Boolean = false
                })
            }

            false
        }

        popup.show()
    }

    fun showFilterMenu(ahchorView: View, filterOptions: List<SelectableMenuItem>) {
        val popup = PopupMenu(context, ahchorView)

        filterOptions.forEach { option ->
            if(option.subMenu != null) {
                val currentSubMenuItem = option.subMenu.find { it.isSelected }
                val subMenuTitle = context.getString(option.title) + currentSubMenuItem?.let { ": ${context.getString(it.title)}" }.orEmpty()
                val subMenu = popup.menu.addSubMenu(subMenuTitle)
                option.subMenu.forEach {
                    addMenuItem(subMenu, it)
                }
            } else {
                addMenuItem(popup.menu, option)
            }
        }

        popup.setOnMenuItemClickListener { menuItem ->
            filterOptions.flatMap { listOf(it) + (it.subMenu ?: emptyList()) }
                .find { it.hashCode() == menuItem.itemId }
                ?.takeIf { it.subMenu == null }
                ?.let { onFilterSelected?.invoke(it) }
            true
        }
        popup.show()
    }

    private fun addMenuItems(menu: Menu, items: List<SelectableMenuItem>, groupId: Int? = null){
        groupId?.let { menu.setGroupCheckable(groupId, true, true) }
        items.forEach { addMenuItem(menu, it, groupId) }
    }

    private fun addMenuItem(menu: Menu, item: SelectableMenuItem, groupId: Int? = null) {
        val menuItem = menu.add(groupId ?: Menu.NONE, item.hashCode(), Menu.NONE, context.getString(item.title))
        menuItem.isCheckable = true
        menuItem.isChecked = item.isSelected
    }
}