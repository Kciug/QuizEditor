package com.rafalskrzypczyk.chat.domain

import com.rafalskrzypczyk.firestore.data.models.MessageDTO

data class Message(
    val id: Long,
    val senderId: String,
    val senderName: String,
    val message: String,
    val timestamp: Long
)

fun MessageDTO.toDomain() = Message(
    id = id,
    message = text,
    senderId = senderId,
    senderName = senderName,
    timestamp = timestamp
)

fun Message.toDTO() = MessageDTO(
    id = id,
    text = message,
    senderId = senderId,
    senderName = senderName,
    timestamp = timestamp
)
