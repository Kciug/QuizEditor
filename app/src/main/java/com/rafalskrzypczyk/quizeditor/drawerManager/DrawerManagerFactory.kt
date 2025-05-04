package com.rafalskrzypczyk.quizeditor.drawerManager

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import com.google.android.material.navigation.NavigationView
import com.rafalskrzypczyk.core.database_management.DatabaseManager
import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesApi
import com.rafalskrzypczyk.core.user_management.UserManager
import javax.inject.Inject

interface DrawerManagerFactory {
    fun create(
        activity: AppCompatActivity,
        drawerLayout: DrawerLayout,
        navViewBinding: NavigationView,
        navController: NavController,
        selectorDatabaseBinding: TextView,
    ) : DrawerManager
}

class DrawerManagerFactoryImpl @Inject constructor(
    private val userManager: UserManager,
    private val sharedPreferences: SharedPreferencesApi,
    private val databaseManager: DatabaseManager
) : DrawerManagerFactory {
    override fun create(
        activity: AppCompatActivity,
        drawerLayout: DrawerLayout,
        navViewBinding: NavigationView,
        navController: NavController,
        selectorDatabaseBinding: TextView,
    ) : DrawerManager = DrawerManager(
        userManager = userManager,
        sharedPreferences = sharedPreferences,
        databaseManager = databaseManager,
        activity = activity,
        drawerLayout = drawerLayout,
        navViewBinding = navViewBinding,
        navController = navController,
        selectorDatabaseBinding = selectorDatabaseBinding,
    )
}