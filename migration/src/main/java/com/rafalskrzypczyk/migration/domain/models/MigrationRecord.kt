package com.rafalskrzypczyk.migration.domain.models

import com.rafalskrzypczyk.firestore.data.models.MigrationRecordDTO
import java.util.Date

data class MigrationRecord(
    val id: String,
    val mode: String,
    val sourceCollection: String,
    val targetCollection: String,
    val itemCount: Int,
    val itemDetails: List<String>,
    val performedBy: String,
    val date: Date
)

fun MigrationRecordDTO.toDomain() = MigrationRecord(
    id = id,
    mode = mode,
    sourceCollection = sourceCollection,
    targetCollection = targetCollection,
    itemCount = itemCount,
    itemDetails = itemDetails,
    performedBy = performedBy,
    date = date
)

fun MigrationRecord.toDTO() = MigrationRecordDTO(
    id = id,
    mode = mode,
    sourceCollection = sourceCollection,
    targetCollection = targetCollection,
    itemCount = itemCount,
    itemDetails = itemDetails,
    performedBy = performedBy,
    date = date
)
