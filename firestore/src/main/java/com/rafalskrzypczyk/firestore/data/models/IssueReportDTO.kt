package com.rafalskrzypczyk.firestore.data.models

import com.google.firebase.Timestamp
import java.util.Date

data class IssueReportDTO(
    val id: String = "",
    val questionId: Long = -1,
    val questionContent: String = "",
    val description: String = "",
    val gameMode: String = "",
    val timestamp: Timestamp = Timestamp(Date())
)
