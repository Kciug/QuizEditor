package com.rafalskrzypczyk.quizeditor

import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.rafalskrzypczyk.core.app_bar_handler.ActionBarBuilder
import com.rafalskrzypczyk.core.base.BaseCompatActivity
import com.rafalskrzypczyk.core.database_management.DatabaseManager
import com.rafalskrzypczyk.core.nav_handling.DrawerNavigationHandler
import com.rafalskrzypczyk.quizeditor.databinding.ActivityMainBinding
import com.rafalskrzypczyk.quizeditor.drawerManager.DrawerManager
import com.rafalskrzypczyk.quizeditor.drawerManager.DrawerManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ApplicationActivity : BaseCompatActivity<ActivityMainBinding>(ActivityMainBinding::inflate),
    ActionBarBuilder, DrawerNavigationHandler {

    lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var drawerManagerFactory: DrawerManagerFactory
    private lateinit var drawerManager: DrawerManager

    @Inject
    lateinit var databaseManager: DatabaseManager

    private var actionMenuRes: Int? = null
    private var actionMenuCallback: ((MenuItem) -> Boolean)? = null

    private lateinit var navController: NavController

    override fun onViewBound() {
        super.onViewBound()

        databaseManager.configure()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_editor) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.appBarMain.toolbar)

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home,
            R.id.nav_chat,
            R.id.nav_quiz_mode,
            R.id.nav_swipe_quiz_mode,
        ), binding.drawerLayout)

        drawerManager = drawerManagerFactory.create(
            activity = this,
            drawerLayout = binding.drawerLayout,
            navViewBinding = binding.drawerNavView,
            navController = navController,
            selectorDatabaseBinding = binding.selectorDatabase,
        )
        drawerManager.setupDrawer(appBarConfiguration)
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
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun setupActionBarMenu(menuRes: Int?, callback: ((MenuItem) -> Boolean)?){
        actionMenuRes = menuRes
        actionMenuCallback = callback
        invalidateOptionsMenu()
    }

    override fun navigateToDestination(destination: Int) {
        binding.drawerNavView.setCheckedItem(destination)
        navController.navigate(destination)
    }

    override fun navigateToChat() {
        navigateToDestination(R.id.nav_chat)
    }
}