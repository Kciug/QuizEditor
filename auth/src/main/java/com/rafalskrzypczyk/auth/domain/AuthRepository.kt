package com.rafalskrzypczyk.auth.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.user.UserData
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun loginWithEmailAndPassword(email: String, password: String) : Flow<Response<UserData>>
    fun signOut()
    fun sendPasswordResetToEmail(email: String) : Flow<Response<Unit>>
}