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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    userManager: UserManager
) : ChatRepository {
    private var currentUser: UserData? = null

    private val messages = mutableListOf<Message>()

    init {
        currentUser = userManager.getCurrentLoggedUser()
    }

    override fun getCurrentUserId(): String = currentUser?.id ?: ""

    override fun getCurrentUserName(): String = currentUser?.name ?: ""

    override fun getLatestMessages(): Flow<Response<List<Message>>> =
        if(!(messages.isEmpty())) flowOf(Response.Success(messages))
        else firestoreApi.getLatestMessages().map {
            if(messages.isEmpty().not()) {
                Response.Success(messages)
            }
            when(it) {
                is Response.Success -> {
                    messages.clear()
                    messages.addAll(it.data.map { it.toDomain() })
                    Response.Success(messages)
                }
                is Response.Error -> Response.Error(it.error)
                is Response.Loading -> Response.Loading
            }
        }

    override fun getUpdatedMessages(): Flow<List<Message>> =
        firestoreApi.getUpdatedMessages().map {
            val received = it.map { it.toDomain() }
            messages.addAll(received)
            messages
        }

    override suspend fun sendMessage(message: Message): Response<Unit> =
        firestoreApi.sendMessage(message.toDTO())
}