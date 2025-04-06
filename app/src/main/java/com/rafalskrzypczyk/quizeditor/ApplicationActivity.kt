package com.rafalskrzypczyk.quizeditor

import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.rafalskrzypczyk.auth.domain.UserManager
import com.rafalskrzypczyk.core.app_bar_handler.ActionBarBuilder
import com.rafalskrzypczyk.core.base.BaseCompatActivity
import com.rafalskrzypczyk.core.database_management.Database
import com.rafalskrzypczyk.core.database_management.DatabaseManager
import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesApi
import com.rafalskrzypczyk.core.nav_handling.DrawerNavigationHandler
import com.rafalskrzypczyk.quizeditor.databinding.ActivityMainBinding
import com.rafalskrzypczyk.quizeditor.user_panel.UserPanelFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ApplicationActivity : BaseCompatActivity<ActivityMainBinding>(ActivityMainBinding::inflate),
    ActionBarBuilder, DrawerNavigationHandler {

    lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var sharedPreferences: SharedPreferencesApi

    @Inject
    lateinit var databaseManager: DatabaseManager


    private var actionMenuRes: Int? = null
    private var actionMenuCallback: ((MenuItem) -> Boolean)? = null

    private lateinit var navController: NavController

    override fun onViewBound() {
        super.onViewBound()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_editor) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.appBarMain.toolbar)
        setupDrawer()
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

    private fun setupDrawer() {
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home,
            R.id.nav_chat,
            R.id.nav_quiz_mode,
            R.id.nav_swipe_quiz_mode,
        ), binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.drawerNavView.setupWithNavController(navController)

        binding.drawerNavView.setNavigationItemSelectedListener{ menuItem ->
            val currentDestination = navController.currentDestination?.id

            if (menuItem.itemId == currentDestination) {
                binding.drawerLayout.closeDrawers()
                return@setNavigationItemSelectedListener true
            }

            if (menuItem.itemId != R.id.nav_home && menuItem.itemId != R.id.nav_chat) {
                sharedPreferences.setLastEditedMode(menuItem.itemId)
            }

            navController.navigate(menuItem.itemId)
            binding.drawerLayout.closeDrawers()
            true
        }

        binding.selectorDatabase.text = databaseManager.getDatabase().name
        binding.selectorDatabase.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            Database.entries.forEach {
                popupMenu.menu.add(it.name)
            }
            popupMenu.setOnMenuItemClickListener { menuItem ->
                changeDatabase(enumValueOf<Database>(menuItem.title.toString()))
                binding.selectorDatabase.text = menuItem.title.toString()
                true
            }
            popupMenu.show()
        }

        setupDrawerHeader()
    }

    fun changeDatabase(database: Database) {
        lifecycleScope.launch{
            databaseManager.changeDatabase(database)
        }
    }

    private fun setupDrawerHeader() {
        val currentUser = userManager.getCurrentLoggedUser()

        val drawerHeader = binding.drawerNavView.getHeaderView(0)
        with(drawerHeader) {
            findViewById<TextView>(R.id.tv_user_name).text = currentUser?.name
            findViewById<TextView>(R.id.tv_user_email).text = currentUser?.email
            findViewById<TextView>(R.id.tv_user_role).text = currentUser?.role?.value
        }

        val buttonLogout = drawerHeader.findViewById<ImageButton>(R.id.btn_manage_account)
        buttonLogout.setOnClickListener{
            val userPanelDialog = UserPanelFragment()
            userPanelDialog.show(supportFragmentManager, "UserPanelDialog")
        }
    }

    override fun navigateToDestination(destination: Int) {
        binding.drawerNavView.setCheckedItem(destination)
        navController.navigate(destination)
    }
}