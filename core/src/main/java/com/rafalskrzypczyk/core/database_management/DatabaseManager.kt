package com.rafalskrzypczyk.core.database_management

import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesApi
import com.rafalskrzypczyk.core.user.UserRole
import com.rafalskrzypczyk.core.user_management.UserManager
import javax.inject.Inject

class DatabaseManager @Inject constructor(
    private val sharedPreferencesApi: SharedPreferencesApi,
    private val userManager: UserManager
) {
    private var database: Database
    private var databaseChangedCallback: ((Database) -> Unit)? = null

    init {
        database = getDatabasePerUserRole()
        databaseChangedCallback?.invoke(database)
    }

    fun getDatabase(): Database = database

    suspend fun changeDatabase(database: Database) {
        this.database = database
        sharedPreferencesApi.setCurrentDatabase(database)

        databaseChangedCallback?.invoke(database)
        DatabaseEventBus.publish()
    }

    fun setDatabaseChangedCallback(callback: (Database) -> Unit) {
        databaseChangedCallback = callback
    }

    fun configure() {
        database = getDatabasePerUserRole()
        databaseChangedCallback?.invoke(database)
    }

    private fun getDatabasePerUserRole() : Database {
        return when(userManager.getCurrentLoggedUser()?.role) {
            UserRole.ADMIN -> sharedPreferencesApi.getCurrentDatabase() ?: Database.DEVELOPMENT
            UserRole.CREATOR -> Database.DEVELOPMENT
            else -> Database.TEST
        }
    }
}