package com.rafalskrzypczyk.firestore.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.firestore.data.models.UserDataDTO
import kotlinx.coroutines.flow.Flow

interface FirestoreApi {
    fun getUserData(userId: String) : Flow<Response<UserDataDTO>>
}