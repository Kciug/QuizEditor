package com.rafalskrzypczyk.core.app_bar_handler

import android.view.MenuItem

interface ActionBarBuilder {
    fun setupActionBarMenu(menuRes: Int?, callback: ((MenuItem) -> Boolean)?)
}