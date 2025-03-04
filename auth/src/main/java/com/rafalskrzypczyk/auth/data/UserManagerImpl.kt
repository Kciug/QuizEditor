package com.rafalskrzypczyk.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.rafalskrzypczyk.auth.domain.UserManager
import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesApi
import com.rafalskrzypczyk.core.user.UserData
import javax.inject.Inject

class UserManagerImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val sharedPreferences: SharedPreferencesApi
) : UserManager {
    override fun isUserLogged(): Boolean = firebaseAuth.currentUser != null

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