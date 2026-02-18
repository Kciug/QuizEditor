package com.rafalskrzypczyk.quizeditor

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.collection.forEach
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.rafalskrzypczyk.chat.domain.ChatMessagesHandler
import com.rafalskrzypczyk.core.app_bar_handler.ActionBarBuilder
import com.rafalskrzypczyk.core.base.BaseCompatActivity
import com.rafalskrzypczyk.core.database_management.DatabaseManager
import com.rafalskrzypczyk.core.internal_notifications.InAppNotificationManager
import com.rafalskrzypczyk.core.nav_handling.DrawerNavigationHandler
import com.rafalskrzypczyk.quizeditor.databinding.ActivityMainBinding
import com.rafalskrzypczyk.quizeditor.drawerManager.DrawerManager
import com.rafalskrzypczyk.quizeditor.drawerManager.DrawerManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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

    @Inject
    lateinit var chatMessagesHandler: ChatMessagesHandler

    private var actionMenuRes: Int? = null
    private var actionMenuCallback: ((MenuItem) -> Boolean)? = null

    private lateinit var inAppNotificationManager: InAppNotificationManager

    private lateinit var navController: NavController
    private var navDestinations: List<NavDestination>? = null

    override fun onViewBound() {
        super.onViewBound()

        databaseManager.configure()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_editor) as NavHostFragment
        navController = navHostFragment.navController
        collectDestinations()

        setSupportActionBar(binding.appBarMain.toolbar)

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home,
            R.id.nav_chat,
            R.id.nav_quiz_mode,
            R.id.nav_swipe_quiz_mode,
            R.id.nav_translations_mode,
            R.id.nav_cem_mode,
        ), binding.drawerLayout)

        drawerManager = drawerManagerFactory.create(
            activity = this,
            drawerLayout = binding.drawerLayout,
            navViewBinding = binding.drawerNavView,
            navController = navController,
            selectorDatabaseBinding = binding.selectorDatabase,
        )
        drawerManager.setupDrawer(appBarConfiguration)

        inAppNotificationManager = InAppNotificationManager(this)
        observeNewChatMessages()
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

    override fun showBackArrow(show: Boolean, onBack: (() -> Unit)?) {
        if (show) {
            binding.appBarMain.toolbar.navigationIcon = androidx.appcompat.content.res.AppCompatResources.getDrawable(this, com.rafalskrzypczyk.core.R.drawable.ic_arrow_back_24)
            binding.appBarMain.toolbar.navigationIcon?.setTint(getColor(com.rafalskrzypczyk.core.R.color.primary))
            binding.appBarMain.toolbar.setNavigationOnClickListener {
                onBack?.invoke()
            }
        } else {
            binding.appBarMain.toolbar.navigationIcon = androidx.appcompat.content.res.AppCompatResources.getDrawable(this, com.rafalskrzypczyk.core.R.drawable.ic_menu_24)
            binding.appBarMain.toolbar.navigationIcon?.setTint(getColor(com.rafalskrzypczyk.core.R.color.primary))
            binding.appBarMain.toolbar.setNavigationOnClickListener {
                binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
            }
        }
    }

    override fun navigateToTopLevelDestination(destination: Int) {
        binding.drawerNavView.setCheckedItem(destination)
        navController.navigate(destination)
    }

    override fun navigateToChat() {
        navigateToTopLevelDestination(R.id.nav_chat)
    }

    override fun navigateToDestinationByTag(tag: String, args: Bundle?) {
        val destination = findDestinationIdByTag(tag)
        Log.d("KURWA", "destination - $destination")
        destination?.let {
            navController.navigate(it, args)
        }
    }

    private fun findDestinationIdByTag(tag: String): Int? {
        return navDestinations?.find { dest ->
                (dest.arguments["nav_tag"]?.defaultValue as? String) == tag
            }?.id
    }

    private fun collectDestinations() {
        val graphNodes = navController.graph.nodes
        var result = mutableListOf<NavDestination>()
        graphNodes.forEach { id, node ->
            result.add(node)
        }
        navDestinations = result
    }

    private fun observeNewChatMessages() {
        chatMessagesHandler.setInAppNotificationManager(notificationManager = inAppNotificationManager) { navigateToChat() }

        lifecycleScope.launch {
            launch { chatMessagesHandler.observeNewMessages() }

            chatMessagesHandler.hasNewMessages.collect {
                if (it) {
                    drawerManager.notifyUnreadChat()
                } else {
                    drawerManager.resetChatIcon()
                }
            }
        }
    }
}