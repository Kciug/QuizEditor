package com.rafalskrzypczyk.quizeditor.drawerManager

import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.rafalskrzypczyk.core.database_management.Database
import com.rafalskrzypczyk.core.database_management.DatabaseManager
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesApi
import com.rafalskrzypczyk.core.user.UserData
import com.rafalskrzypczyk.core.user.UserRole
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.quizeditor.R
import com.rafalskrzypczyk.quizeditor.user_panel.UserPanelFragment
import kotlinx.coroutines.launch


class DrawerManager (
    private val userManager: UserManager,
    private val sharedPreferences: SharedPreferencesApi,
    private val databaseManager: DatabaseManager,
    private val activity: AppCompatActivity,
    private val drawerLayout: DrawerLayout,
    private val navViewBinding: NavigationView,
    private val navController: NavController,
    private val selectorDatabaseBinding: TextView,
) {
    private val user: UserData? by lazy {
        userManager.getCurrentLoggedUser()
    }

    fun setupDrawer(appBarConfiguration: AppBarConfiguration) {
        applyWindowInsetsToDrawer()

        val userRole = user?.role ?: UserRole.USER

        setupDrawerHeader()
        setupDatabaseSelector(userRole)

        if(userRole != UserRole.ADMIN && userRole != UserRole.CREATOR) {
            navViewBinding.menu.findItem(R.id.nav_chat)?.isVisible = false
        }

        activity.setupActionBarWithNavController(navController, appBarConfiguration)
        navViewBinding.setupWithNavController(navController)

        navViewBinding.setNavigationItemSelectedListener { menuItem ->
            val currentDestination = navController.currentDestination?.id

            if (menuItem.itemId == currentDestination) {
                drawerLayout.closeDrawers()
                return@setNavigationItemSelectedListener true
            }

            if (menuItem.itemId != R.id.nav_home && menuItem.itemId != R.id.nav_chat) {
                sharedPreferences.setLastEditedMode(menuItem.itemId)
            }

            navController.navigate(menuItem.itemId)
            drawerLayout.closeDrawers()
            true
        }

        navController.addOnDestinationChangedListener {_, destination, _ ->
            val drawerNavView = navViewBinding
            val matchingItem = drawerNavView.menu.findItem(destination.id)

            matchingItem?.let {
                drawerNavView.setCheckedItem(it.itemId)
            }
        }
    }

    private fun setupDrawerHeader() {
        val drawerHeader = navViewBinding.getHeaderView(0)
        with(drawerHeader) {
            findViewById<TextView>(R.id.tv_user_name).text = user?.name
            findViewById<TextView>(R.id.tv_user_email).text = user?.email
            findViewById<TextView>(R.id.tv_user_role).text = user?.role?.value
        }

        val buttonLogout = drawerHeader.findViewById<ImageButton>(R.id.btn_manage_account)
        buttonLogout.setOnClickListener{
            val userPanelDialog = UserPanelFragment()
            userPanelDialog.show(activity.supportFragmentManager, "UserPanelDialog")
        }
    }

    private fun setupDatabaseSelector(userRole: UserRole) {
        if(userRole != UserRole.ADMIN) return

        selectorDatabaseBinding.makeVisible()
        selectorDatabaseBinding.text = databaseManager.getDatabase().name
        selectorDatabaseBinding.setOnClickListener {
            val popupMenu = PopupMenu(activity, it)
            Database.entries.forEach {
                popupMenu.menu.add(it.name)
            }
            popupMenu.setOnMenuItemClickListener { menuItem ->
                changeDatabase(enumValueOf<Database>(menuItem.title.toString()))
                selectorDatabaseBinding.text = menuItem.title.toString()
                drawerLayout.closeDrawers()
                true
            }
            popupMenu.show()
        }
    }

    private fun changeDatabase(database: Database) {
        activity.lifecycleScope.launch {
            databaseManager.changeDatabase(database)
        }
    }

    fun notifyUnreadChat() {
        val chatItem = navViewBinding.menu.findItem(R.id.nav_chat)
        chatItem.icon = AppCompatResources.getDrawable(activity, com.rafalskrzypczyk.core.R.drawable.ic_chat_unread_24)
        chatItem.title = activity.getString(R.string.drawer_menu_title_chat_unread)
    }

    fun resetChatIcon() {
        val chatItem = navViewBinding.menu.findItem(R.id.nav_chat)
        chatItem.icon = AppCompatResources.getDrawable(activity, com.rafalskrzypczyk.core.R.drawable.ic_chat_24)
        chatItem.title = activity.getString(R.string.drawer_menu_title_chat)
    }

    private fun applyWindowInsetsToDrawer() {
        ViewCompat.setOnApplyWindowInsetsListener(navViewBinding) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val header = navViewBinding.getHeaderView(0)
            header?.setPadding(
                header.paddingLeft,
                systemInsets.top,
                header.paddingRight,
                header.paddingBottom
            )

            insets
        }
    }
}