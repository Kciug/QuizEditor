package com.rafalskrzypczyk.core.database_management

import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesApi
import javax.inject.Inject

class DatabaseManager @Inject constructor(
    private val sharedPreferencesApi: SharedPreferencesApi
) {
    private var database: Database
    private var databaseChangedCallback: ((Database) -> Unit)? = null

    init {
        database = sharedPreferencesApi.getCurrentDatabase()
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
}