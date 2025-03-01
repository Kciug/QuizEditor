package com.rafalskrzypczyk.myapplication

import android.view.Menu
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.rafalskrzypczyk.core.app_bar_handler.ActionBarBuilder
import com.rafalskrzypczyk.core.base.BaseCompatActivity
import com.rafalskrzypczyk.myapplication.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApplicationActivity : BaseCompatActivity<ActivityMainBinding>(ActivityMainBinding::inflate), ActionBarBuilder {

    lateinit var appBarConfiguration: AppBarConfiguration


    private var actionMenuRes: Int? = null
    private var actionMenuCallback: ((MenuItem) -> Boolean)? = null

    override fun onViewBound() {
        super.onViewBound()

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.drawerNavView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_editor) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_quiz_mode, R.id.nav_swipe_quiz_mode, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(actionMenuRes != null)  menuInflater.inflate(actionMenuRes!!, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        actionMenuCallback?.invoke(item)
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_editor) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun setupActionBarMenu(menuRes: Int, callback: (MenuItem) -> Boolean){
        actionMenuRes = menuRes
        actionMenuCallback = callback
        invalidateOptionsMenu()
    }
}