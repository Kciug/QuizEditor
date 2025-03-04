package com.rafalskrzypczyk.core.local_preferences

import android.content.SharedPreferences
import android.util.Log
import com.rafalskrzypczyk.core.user.UserData
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SharedPreferencesService @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : SharedPreferencesApi {
    companion object{
        const val KEY_CURRENT_USER = "current_user"

        const val DEFAULT_VALUE = ""
    }


    override fun setCurrentUser(userData: UserData?) {
        Log.d("SharedPreferencesService", "setCurrentUser: $userData")
        sharedPreferences.edit()
            .putString(KEY_CURRENT_USER, Json.encodeToString(userData))
            .apply()
    }

    override fun getCurrentUser(): UserData? {
        val json = sharedPreferences.getString(KEY_CURRENT_USER, DEFAULT_VALUE)
        Log.d("SharedPreferencesService", "json: $json")
        if (json.isNullOrEmpty()) return null
        return Json.decodeFromString<UserData>(json)
    }

    override fun clearUserData() {
        setCurrentUser(null)
    }
}