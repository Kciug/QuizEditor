package com.rafalskrzypczyk.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.rafalskrzypczyk.auth.R
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.auth.domain.UserData
import com.rafalskrzypczyk.auth.domain.UserManager
import com.rafalskrzypczyk.auth.domain.UserRole
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.utils.ResourceProvider
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userManager: UserManager,
    private val resourcesProvider: ResourceProvider,
) : AuthRepository {
    override fun loginWithEmailAndPassword(
        email: String,
        password: String,
    ) = flow {
        emit(Response.Loading)
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if(user == null){
                throw Exception(resourcesProvider.getString(R.string.unknow_error))
            }

            val tempUser = UserData("", user.displayName ?: "", user.email ?: "", UserRole.ADMIN)

            emit(Response.Success(tempUser))
        } catch (e: Exception) {
            emit(
                Response.Error(
                    e.localizedMessage ?: resourcesProvider.getString(R.string.unknow_error)
                )
            )
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
        userManager.clearUserDataLocal()
    }

    override fun sendPasswordResetToEmail(email: String) = callbackFlow {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                trySend(
                    Response.Error(
                        it.exception?.localizedMessage
                            ?: resourcesProvider.getString(R.string.unknow_error)
                    )
                )
            }.addOnSuccessListener {
                trySend(Response.Success(Unit))
            }
    }
}