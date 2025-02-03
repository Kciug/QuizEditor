package com.rafalskrzypczyk.core.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class KeyboardController(context: Context) {
    private val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    fun showKeyboardWithDelay(view: View) {
        view.postDelayed({
            view.requestFocus()
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }, 200)
    }

    fun hideKeyboard(view: View) {
        if(inputMethodManager.isActive) inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}