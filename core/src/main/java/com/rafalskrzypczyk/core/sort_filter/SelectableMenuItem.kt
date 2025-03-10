package com.rafalskrzypczyk.core.sort_filter

data class SelectableMenuItem(
    val itemHashCode: Int,
    val title: Int,
    val isSelected: Boolean,
    val subMenu: List<SelectableMenuItem>? = null
)
