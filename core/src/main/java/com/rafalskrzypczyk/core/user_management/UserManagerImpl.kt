package com.rafalskrzypczyk.core.user_management

import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesApi
import com.rafalskrzypczyk.core.user.UserData
import javax.inject.Inject

class UserManagerImpl @Inject constructor(
    private val sharedPreferences: SharedPreferencesApi
) : UserManager {
    override fun getCurrentLoggedUser(): UserData? {
        return sharedPreferences.getCurrentUser()
    }

    override fun saveUserDataInLocal(user: UserData) {
        sharedPreferences.setCurrentUser(user)
    }

    override fun clearUserDataLocal() {
        sharedPreferences.clearUserData()
    }

}