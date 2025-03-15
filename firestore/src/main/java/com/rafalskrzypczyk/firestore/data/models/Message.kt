package com.rafalskrzypczyk.firestore.data.models

data class MessageDTO(
    val id: Long = 0,
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val timestamp: Long = 0
)
