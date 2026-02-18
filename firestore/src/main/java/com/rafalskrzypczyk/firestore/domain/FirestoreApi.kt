package com.rafalskrzypczyk.firestore.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.data_statistics.DataStatistics
import com.rafalskrzypczyk.firestore.data.models.CategoryDTO
import com.rafalskrzypczyk.firestore.data.models.CemCategoryDTO
import com.rafalskrzypczyk.firestore.data.models.CemQuestionDTO
import com.rafalskrzypczyk.firestore.data.models.MessageDTO
import com.rafalskrzypczyk.firestore.data.models.MigrationRecordDTO
import com.rafalskrzypczyk.firestore.data.models.QuestionDTO
import com.rafalskrzypczyk.firestore.data.models.SwipeQuestionDTO
import com.rafalskrzypczyk.firestore.data.models.TranslationQuestionDTO
import com.rafalskrzypczyk.firestore.data.models.UserDataDTO
import kotlinx.coroutines.flow.Flow

interface FirestoreApi {
    fun getUserData(userId: String): Flow<Response<UserDataDTO>>

    suspend fun getDatabaseStatistics() : Flow<Response<DataStatistics>>

    fun getMigrationHistory(mode: String): Flow<Response<List<MigrationRecordDTO>>>
    suspend fun addMigrationRecord(record: MigrationRecordDTO): Response<Unit>

    suspend fun getItemsFromCollection(collectionName: String): Response<List<Any>>
    suspend fun <T : Any> addItemToCollection(id: String, data: T, collectionName: String): Response<Unit>
    suspend fun updateItemFieldInCollection(id: String, fieldName: String, value: Any?, collectionName: String): Response<Unit>

    fun getCollectionNameForMode(mode: String, database: com.rafalskrzypczyk.core.database_management.Database, isQuestions: Boolean = false): String

    suspend fun getQuizCategoriesFrom(collectionName: String): Response<List<CategoryDTO>>
    suspend fun getQuizQuestionsFrom(collectionName: String): Response<List<QuestionDTO>>
    suspend fun getSwipeQuestionsFrom(collectionName: String): Response<List<SwipeQuestionDTO>>
    suspend fun getTranslationQuestionsFrom(collectionName: String): Response<List<TranslationQuestionDTO>>
    suspend fun getCemCategoriesFrom(collectionName: String): Response<List<CemCategoryDTO>>
    suspend fun getCemQuestionsFrom(collectionName: String): Response<List<CemQuestionDTO>>

    suspend fun getQuizCategoryFrom(id: String, collectionName: String): Response<CategoryDTO>
    suspend fun getCemCategoryFrom(id: String, collectionName: String): Response<CemCategoryDTO>

    fun getQuizCategories(): Flow<Response<List<CategoryDTO>>>
    fun getUpdatedQuizCategories(): Flow<List<CategoryDTO>>
    suspend fun addQuizCategory(category: CategoryDTO): Response<Unit>
    suspend fun updateQuizCategory(category: CategoryDTO): Response<Unit>
    suspend fun deleteQuizCategory(categoryId: Long): Response<Unit>

    fun getQuizQuestions(): Flow<Response<List<QuestionDTO>>>
    fun getUpdatedQuizQuestions(): Flow<List<QuestionDTO>>
    suspend fun addQuizQuestion(question: QuestionDTO): Response<Unit>
    suspend fun updateQuizQuestion(question: QuestionDTO): Response<Unit>
    suspend fun deleteQuizQuestion(questionId: Long): Response<Unit>

    fun getSwipeQuestions(): Flow<Response<List<SwipeQuestionDTO>>>
    fun getUpdatedSwipeQuestions(): Flow<List<SwipeQuestionDTO>>
    suspend fun addSwipeQuestion(question: SwipeQuestionDTO): Response<Unit>
    suspend fun updateSwipeQuestion(question: SwipeQuestionDTO): Response<Unit>
    suspend fun deleteSwipeQuestion(questionId: Long): Response<Unit>

    fun getLatestMessages(): Flow<Response<List<MessageDTO>>>
    fun getOlderMessages(): Flow<Response<List<MessageDTO>>>
    fun getUpdatedMessages(): Flow<List<MessageDTO>>
    suspend fun sendMessage(message: MessageDTO): Response<Unit>

    fun getTranslationQuestions(): Flow<Response<List<TranslationQuestionDTO>>>
    fun getUpdatedTranslationQuestions(): Flow<List<TranslationQuestionDTO>>
    suspend fun addTranslationQuestion(question: TranslationQuestionDTO): Response<Unit>
    suspend fun updateTranslationQuestion(question: TranslationQuestionDTO): Response<Unit>
    suspend fun deleteTranslationQuestion(questionId: Long): Response<Unit>

    fun getCemCategories(): Flow<Response<List<CemCategoryDTO>>>
    fun getUpdatedCemCategories(): Flow<List<CemCategoryDTO>>
    fun getCemCategoryById(categoryId: Long): Flow<Response<CemCategoryDTO>>
    fun getUpdatedCemCategoryById(categoryId: Long): Flow<CemCategoryDTO?>
    suspend fun addCemCategory(category: CemCategoryDTO): Response<Unit>
    suspend fun updateCemCategory(category: CemCategoryDTO): Response<Unit>
    suspend fun deleteCemCategory(categoryId: Long): Response<Unit>

    fun getCemQuestions(): Flow<Response<List<CemQuestionDTO>>>
    fun getUpdatedCemQuestions(): Flow<List<CemQuestionDTO>>
    fun getCemQuestionById(questionId: Long): Flow<Response<CemQuestionDTO>>
    fun getUpdatedCemQuestionById(questionId: Long): Flow<CemQuestionDTO?>
    suspend fun addCemQuestion(question: CemQuestionDTO): Response<Unit>
    suspend fun updateCemQuestion(question: CemQuestionDTO): Response<Unit>
    suspend fun deleteCemQuestion(questionId: Long): Response<Unit>
}
