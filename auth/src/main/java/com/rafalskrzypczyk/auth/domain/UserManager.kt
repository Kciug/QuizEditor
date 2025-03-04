package com.rafalskrzypczyk.auth.domain

interface UserManager {
    fun isUserLogged(): Boolean
    fun getCurrentLoggedUser(): UserData
    fun saveUserDataInLocal(user: UserData)
    fun clearUserDataLocal()
}