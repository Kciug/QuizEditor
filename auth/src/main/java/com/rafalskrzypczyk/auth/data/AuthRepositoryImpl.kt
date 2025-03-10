package com.rafalskrzypczyk.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.auth.domain.UserManager
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.user.UserData
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreApi: FirestoreApi,
    private val userManager: UserManager,
    private val resourcesProvider: ResourceProvider,
) : AuthRepository {
    override fun loginWithEmailAndPassword(
        email: String,
        password: String,
    ) = channelFlow<Response<UserData>> {
        try {
            send(Response.Loading)

            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            firestoreApi.getUserData(result.user!!.uid).collectLatest {
                when (it) {
                    is Response.Error -> throw Exception(it.error)
                    Response.Loading -> send(Response.Loading)
                    is Response.Success -> {
                        val userData = it.data.toDomain(result.user!!.email ?: "")
                        userManager.saveUserDataInLocal(userData)
                        send(Response.Success(userData))
                    }
                }
            }
        } catch (e: Exception) {
            send(Response.Error(e.localizedMessage ?: resourcesProvider.getString(R.string.error_unknown)))
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
                            ?: resourcesProvider.getString(R.string.error_unknown)
                    )
                )
            }.addOnSuccessListener {
                trySend(Response.Success(Unit))
            }
    }
}