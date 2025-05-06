package com.rafalskrzypczyk.core.local_preferences

import android.content.SharedPreferences
import com.rafalskrzypczyk.core.database_management.Database
import com.rafalskrzypczyk.core.user.UserData
import kotlinx.serialization.json.Json
import java.util.Date
import javax.inject.Inject

class SharedPreferencesService @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : SharedPreferencesApi {
    companion object{
        const val KEY_CURRENT_USER = "current_user"
        const val KEY_LAST_EDITED_MODE = "last_edited_mode"
        const val KEY_CURRENT_DATABASE = "current_database"
        const val KEY_LAST_DISPLAYED_MESSAGE_TIMESTAMP = "last_displayed_message_timestamp"

        const val DEFAULT_STRING_VALUE = ""
        const val DEFAULT_NUMBER_VALUE = 0
    }


    override fun setCurrentUser(userData: UserData?) {
        sharedPreferences.edit()
            .putString(KEY_CURRENT_USER, Json.encodeToString(userData))
            .apply()
    }

    override fun getCurrentUser(): UserData? {
        val json = sharedPreferences.getString(KEY_CURRENT_USER, DEFAULT_STRING_VALUE)
        if (json.isNullOrEmpty()) return null
        return Json.decodeFromString<UserData>(json)
    }

    override fun clearUserData() {
        setCurrentUser(null)
        setLastEditedMode(DEFAULT_NUMBER_VALUE)
        sharedPreferences.edit().remove(KEY_CURRENT_DATABASE).apply()
    }

    override fun setLastEditedMode(mode: Int) {
        sharedPreferences.edit()
            .putInt(KEY_LAST_EDITED_MODE, mode)
            .apply()
    }

    override fun getLastEditedMode(): Int {
        return sharedPreferences.getInt(KEY_LAST_EDITED_MODE, DEFAULT_NUMBER_VALUE)
    }

    override fun setCurrentDatabase(database: Database) {
        sharedPreferences.edit()
            .putString(KEY_CURRENT_DATABASE, database.name)
            .apply()
    }

    override fun getCurrentDatabase(): Database? {
        val databaseName = sharedPreferences.getString(KEY_CURRENT_DATABASE, null)
        databaseName?.let { return Database.valueOf(it) } ?: return null
    }

    override fun setLastDisplayedMessageTimestamp(timestamp: Date) {
        sharedPreferences.edit()
            .putLong(KEY_LAST_DISPLAYED_MESSAGE_TIMESTAMP, timestamp.time)
            .apply()
    }

    override fun getLastDisplayedMessageTimestamp(): Date {
        val date = sharedPreferences.getLong(KEY_LAST_DISPLAYED_MESSAGE_TIMESTAMP, 0)
        return Date(date)
    }
}