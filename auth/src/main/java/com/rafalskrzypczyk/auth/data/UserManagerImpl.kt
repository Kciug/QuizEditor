package com.rafalskrzypczyk.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.rafalskrzypczyk.auth.domain.UserData
import com.rafalskrzypczyk.auth.domain.UserManager
import com.rafalskrzypczyk.auth.domain.UserRole
import javax.inject.Inject

class UserManagerImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : UserManager {
    override fun isUserLogged(): Boolean = firebaseAuth.currentUser != null

    override fun getCurrentLoggedUser(): UserData {
        return UserData("","","", UserRole.ADMIN)
    }

    override fun saveUserDataInLocal(user: UserData) {

    }

    override fun clearUserDataLocal() {

    }

}