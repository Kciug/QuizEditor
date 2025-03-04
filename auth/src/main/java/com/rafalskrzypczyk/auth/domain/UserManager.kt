package com.rafalskrzypczyk.auth.domain

import com.rafalskrzypczyk.core.user.UserData

interface UserManager {
    fun isUserLogged(): Boolean
    fun getCurrentLoggedUser(): UserData?
    fun saveUserDataInLocal(user: UserData)
    fun clearUserDataLocal()
}