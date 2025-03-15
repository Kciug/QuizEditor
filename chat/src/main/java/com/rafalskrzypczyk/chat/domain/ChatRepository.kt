package com.rafalskrzypczyk.chat.domain

import com.rafalskrzypczyk.core.api_result.Response
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getCurrentUserId() : String
    fun getCurrentUserName() : String
    fun getLatestMessages() : Flow<Response<List<Message>>>
    fun getUpdatedMessages() : Flow<List<Message>>
    suspend fun sendMessage(message: Message) : Response<Unit>
}