package com.rafalskrzypczyk.firestore.data

import com.google.firebase.firestore.FirebaseFirestore
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.firestore.data.models.UserDataDTO
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreApi {
    override fun getUserData(userId: String): Flow<Response<UserDataDTO>> = flow {
        emit(Response.Loading)

        val result = firestore.collection(FirestoreCollections.USER_DATA_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject(UserDataDTO::class.java)

        if(result != null)
            emit(Response.Success(result))
    }.catch {
        emit(Response.Error(it.localizedMessage ?: "Unknown error"))
    }
}