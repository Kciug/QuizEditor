package com.rafalskrzypczyk.core.user_management

import com.rafalskrzypczyk.core.user.UserData

interface UserManager {
    fun getCurrentLoggedUser(): UserData?
    fun saveUserDataInLocal(user: UserData)
    fun clearUserDataLocal()
}