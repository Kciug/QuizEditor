package com.rafalskrzypczyk.core.local_preferences

import com.rafalskrzypczyk.core.database_management.Database
import com.rafalskrzypczyk.core.user.UserData
import java.util.Date

interface SharedPreferencesApi {
    fun setCurrentUser(userData: UserData?)
    fun getCurrentUser(): UserData?
    fun clearUserData()

    fun setLastEditedMode(mode: Int)
    fun getLastEditedMode(): Int

    fun setCurrentDatabase(database: Database)
    fun getCurrentDatabase(): Database?

    fun setLastDisplayedMessageTimestamp(timestamp: Date)
    fun getLastDisplayedMessageTimestamp() : Date?
}