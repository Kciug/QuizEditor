package com.rafalskrzypczyk.migration.data

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.database_management.Database
import com.rafalskrzypczyk.firestore.data.models.CemCategoryDTO
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.migration.domain.MigrationRepository
import com.rafalskrzypczyk.migration.domain.models.MigrationRecord
import com.rafalskrzypczyk.migration.domain.models.toDTO
import com.rafalskrzypczyk.migration.domain.models.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class MigrationRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi
) : MigrationRepository {

    override fun getMigrationHistory(mode: String): Flow<Response<List<MigrationRecord>>> =
        firestoreApi.getMigrationHistory(mode).map { response ->
            when (response) {
                is Response.Success -> Response.Success(response.data.map { it.toDomain() })
                is Response.Error -> response
                is Response.Loading -> response
            }
        }

    override suspend fun addMigrationRecord(record: MigrationRecord): Response<Unit> =
        firestoreApi.addMigrationRecord(record.toDTO())

    override suspend fun migrateMainModeCategory(
        categoryId: Long,
        sourceEnv: Database,
        targetEnv: Database,
        performedBy: String
    ): Response<Unit> {
        val sourceCategoryCollection = firestoreApi.getCollectionNameForMode("main", sourceEnv)
        val sourceQuestionCollection = firestoreApi.getCollectionNameForMode("main", sourceEnv, true)
        val targetCategoryCollection = firestoreApi.getCollectionNameForMode("main", targetEnv)
        val targetQuestionCollection = firestoreApi.getCollectionNameForMode("main", targetEnv, true)

        val categories = firestoreApi.getQuizCategoriesFrom(sourceCategoryCollection)
        if (categories !is Response.Success) return Response.Error("Failed to fetch source categories")
        val category = categories.data.find { it.id == categoryId } ?: return Response.Error("Category not found")

        val questions = firestoreApi.getQuizQuestionsFrom(sourceQuestionCollection)
        if (questions !is Response.Success) return Response.Error("Failed to fetch source questions")
        val linkedQuestions = questions.data.filter { category.questionIDs.contains(it.id) }

        // Write to target
        val transferDate = Date()
        val targetResp = firestoreApi.addItemToCollection(category.id.toString(), category, targetCategoryCollection)
        if (targetResp !is Response.Success) return Response.Error("Failed to write to target environment")

        linkedQuestions.forEach { 
            firestoreApi.addItemToCollection(it.id.toString(), it, targetQuestionCollection) 
        }

        if (targetEnv == Database.PRODUCTION) {
            val sourceUpdateResp = firestoreApi.updateItemFieldInCollection(
                category.id.toString(),
                "productionTransferDate",
                transferDate,
                sourceCategoryCollection
            )
            if (sourceUpdateResp !is Response.Success) return Response.Error("Failed to update source item migration date")
        }

        val record = MigrationRecord(
            id = UUID.randomUUID().toString(),
            mode = "main",
            sourceCollection = sourceEnv.name,
            targetCollection = targetEnv.name,
            itemCount = 1 + linkedQuestions.size,
            itemDetails = listOf("${category.title} (${linkedQuestions.size} questions)"),
            performedBy = performedBy,
            date = transferDate
        )
        return addMigrationRecord(record)
    }

    override suspend fun migrateSwipeMode(
        sourceEnv: Database,
        targetEnv: Database,
        performedBy: String
    ): Response<Unit> {
        val sourceColl = firestoreApi.getCollectionNameForMode("swipe", sourceEnv)
        val targetColl = firestoreApi.getCollectionNameForMode("swipe", targetEnv)

        val questions = firestoreApi.getSwipeQuestionsFrom(sourceColl)
        if (questions !is Response.Success) return Response.Error("Failed to fetch source questions")

        val transferDate = Date()
        questions.data.forEach { q ->
            val writeResp = firestoreApi.addItemToCollection(q.id.toString(), q, targetColl)
            if (writeResp is Response.Success && targetEnv == Database.PRODUCTION) {
                firestoreApi.updateItemFieldInCollection(
                    q.id.toString(),
                    "productionTransferDate",
                    transferDate,
                    sourceColl
                )
            }
        }

        val record = MigrationRecord(
            id = UUID.randomUUID().toString(),
            mode = "swipe",
            sourceCollection = sourceEnv.name,
            targetCollection = targetEnv.name,
            itemCount = questions.data.size,
            itemDetails = questions.data.map { it.text },
            performedBy = performedBy,
            date = transferDate
        )
        return addMigrationRecord(record)
    }

    override suspend fun migrateTranslationsMode(
        sourceEnv: Database,
        targetEnv: Database,
        performedBy: String
    ): Response<Unit> {
        val sourceColl = firestoreApi.getCollectionNameForMode("translations", sourceEnv)
        val targetColl = firestoreApi.getCollectionNameForMode("translations", targetEnv)

        val translations = firestoreApi.getTranslationQuestionsFrom(sourceColl)
        if (translations !is Response.Success) return Response.Error("Failed to fetch source translations")

        val transferDate = Date()
        translations.data.forEach { t ->
            val writeResp = firestoreApi.addItemToCollection(t.id.toString(), t, targetColl)
            if (writeResp is Response.Success && targetEnv == Database.PRODUCTION) {
                firestoreApi.updateItemFieldInCollection(
                    t.id.toString(),
                    "productionTransferDate",
                    transferDate,
                    sourceColl
                )
            }
        }

        val record = MigrationRecord(
            id = UUID.randomUUID().toString(),
            mode = "translations",
            sourceCollection = sourceEnv.name,
            targetCollection = targetEnv.name,
            itemCount = translations.data.size,
            itemDetails = translations.data.map { it.phrase },
            performedBy = performedBy,
            date = transferDate
        )
        return addMigrationRecord(record)
    }

    override suspend fun migrateCemModeCategory(
        categoryId: Long,
        sourceEnv: Database,
        targetEnv: Database,
        performedBy: String
    ): Response<Unit> {
        val sourceCatColl = firestoreApi.getCollectionNameForMode("cem", sourceEnv)
        val sourceQueColl = firestoreApi.getCollectionNameForMode("cem", sourceEnv, true)
        val targetCatColl = firestoreApi.getCollectionNameForMode("cem", targetEnv)
        val targetQueColl = firestoreApi.getCollectionNameForMode("cem", targetEnv, true)

        val allCategoriesResp = firestoreApi.getCemCategoriesFrom(sourceCatColl)
        if (allCategoriesResp !is Response.Success) return Response.Error("Failed to fetch source categories")
        
        val allQuestionsResp = firestoreApi.getCemQuestionsFrom(sourceQueColl)
        if (allQuestionsResp !is Response.Success) return Response.Error("Failed to fetch source questions")

        val treeCategories = mutableListOf<CemCategoryDTO>()
        collectCemTree(categoryId, allCategoriesResp.data, treeCategories)
        
        val uniqueQuestionIds = treeCategories.flatMap { it.questionIDs }.distinct()
        val treeQuestions = allQuestionsResp.data.filter { uniqueQuestionIds.contains(it.id) }

        // Write to target
        val transferDate = Date()
        treeCategories.forEach { firestoreApi.addItemToCollection(it.id.toString(), it, targetCatColl) }
        treeQuestions.forEach { firestoreApi.addItemToCollection(it.id.toString(), it, targetQueColl) }

        if (targetEnv == Database.PRODUCTION) {
            treeCategories.forEach { cat ->
                firestoreApi.updateItemFieldInCollection(
                    cat.id.toString(),
                    "productionTransferDate",
                    transferDate,
                    sourceCatColl
                )
            }
        }

        val itemDetails = treeCategories.map { cat ->
            "${cat.title} (${cat.subcategoryIDs.size} sub, ${cat.questionIDs.size} questions)"
        }.toMutableList()
        itemDetails.add("Total: ${treeQuestions.size} questions")

        val record = MigrationRecord(
            id = UUID.randomUUID().toString(),
            mode = "cem",
            sourceCollection = sourceEnv.name,
            targetCollection = targetEnv.name,
            itemCount = treeCategories.size + treeQuestions.size,
            itemDetails = itemDetails,
            performedBy = performedBy,
            date = transferDate
        )
        return addMigrationRecord(record)
    }

    override suspend fun getCemCategoryMigrationPreview(
        categoryId: Long,
        sourceEnv: Database
    ): Response<Pair<Int, Int>> {
        val sourceCatColl = firestoreApi.getCollectionNameForMode("cem", sourceEnv)
        val allCategoriesResp = firestoreApi.getCemCategoriesFrom(sourceCatColl)
        if (allCategoriesResp !is Response.Success) return Response.Error("Failed to fetch source categories")

        val treeCategories = mutableListOf<CemCategoryDTO>()
        collectCemTree(categoryId, allCategoriesResp.data, treeCategories)
        
        val uniqueQuestionIds = treeCategories.flatMap { it.questionIDs }.distinct()
        
        return Response.Success(Pair(treeCategories.size - 1, uniqueQuestionIds.size))
    }

    private fun collectCemTree(
        currentId: Long,
        allCategories: List<CemCategoryDTO>,
        result: MutableList<CemCategoryDTO>
    ) {
        val current = allCategories.find { it.id == currentId } ?: return
        result.add(current)
        current.subcategoryIDs.forEach { subId ->
            collectCemTree(subId, allCategories, result)
        }
    }
}
