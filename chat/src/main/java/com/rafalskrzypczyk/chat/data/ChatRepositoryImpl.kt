package com.rafalskrzypczyk.chat.data

import com.rafalskrzypczyk.auth.domain.UserManager
import com.rafalskrzypczyk.chat.domain.ChatRepository
import com.rafalskrzypczyk.chat.domain.Message
import com.rafalskrzypczyk.chat.domain.toDTO
import com.rafalskrzypczyk.chat.domain.toDomain
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.user.UserData
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val userManager: UserManager
) : ChatRepository {
    private var currentUser: UserData? = null

    init {
        currentUser = userManager.getCurrentLoggedUser()
    }

    override fun getCurrentUserId(): String = currentUser?.id ?: ""

    override fun getCurrentUserName(): String = currentUser?.name ?: ""

    override fun getLatestMessages(): Flow<Response<List<Message>>> =
        firestoreApi.getLatestMessages().map {
            when(it) {
                is Response.Success -> Response.Success(it.data.map { it.toDomain() })
                is Response.Error -> Response.Error(it.error)
                is Response.Loading -> Response.Loading
            }
        }

    override fun getUpdatedMessages(): Flow<List<Message>> =
        firestoreApi.getUpdatedMessages().map { it.map { it.toDomain() } }

    override suspend fun sendMessage(message: Message): Response<Unit> =
        firestoreApi.sendMessage(message.toDTO())
}