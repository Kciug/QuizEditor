package com.rafalskrzypczyk.migration.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.database_management.Database
import com.rafalskrzypczyk.migration.domain.models.MigrationRecord
import kotlinx.coroutines.flow.Flow

interface MigrationRepository {
    fun getMigrationHistory(mode: String): Flow<Response<List<MigrationRecord>>>
    suspend fun addMigrationRecord(record: MigrationRecord): Response<Unit>

    suspend fun migrateMainModeCategory(
        categoryId: Long,
        sourceEnv: Database,
        targetEnv: Database,
        performedBy: String
    ): Response<Unit>

    suspend fun migrateSwipeMode(
        sourceEnv: Database,
        targetEnv: Database,
        performedBy: String
    ): Response<Unit>

    suspend fun migrateTranslationsMode(
        sourceEnv: Database,
        targetEnv: Database,
        performedBy: String
    ): Response<Unit>

    suspend fun migrateCemModeCategory(
        categoryId: Long,
        sourceEnv: Database,
        targetEnv: Database,
        performedBy: String
    ): Response<Unit>
    
    suspend fun getCemCategoryMigrationPreview(
        categoryId: Long,
        sourceEnv: Database
    ): Response<Pair<Int, Int>> // Subcategories, Questions
}
