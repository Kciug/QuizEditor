package com.rafalskrzypczyk.quiz_mode.presentation.editable_picker

data class Checkable(
    val id: Int,
    val title: String,
    var isChecked: Boolean,
    val isLocked: Boolean
)
