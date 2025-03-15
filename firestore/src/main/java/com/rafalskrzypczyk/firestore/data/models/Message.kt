package com.rafalskrzypczyk.firestore.data.models

import java.util.Date

data class MessageDTO(
    val id: Long = 0,
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val timestamp: Date = Date()
)
