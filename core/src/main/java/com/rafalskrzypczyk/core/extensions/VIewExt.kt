package com.rafalskrzypczyk.core.extensions

import android.view.View

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.makeEnabled() {
    isEnabled = true
}

fun View.makeDisabled() {
    isEnabled = false
}

fun View.toggleVisibility() {
    visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
}