package com.rafalskrzypczyk.quiz_mode.ui.editable_picker

data class Checkable(
    val id: Int,
    val title: String,
    var isChecked: Boolean,
    val isLocked: Boolean
)
