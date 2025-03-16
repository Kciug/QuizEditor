package com.rafalskrzypczyk.firestore.data

import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.data_statistics.DataStatistics
import com.rafalskrzypczyk.core.data_statistics.StatisticsQuizMode
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.firestore.data.models.CategoryDTO
import com.rafalskrzypczyk.firestore.data.models.MessageDTO
import com.rafalskrzypczyk.firestore.data.models.QuestionDTO
import com.rafalskrzypczyk.firestore.data.models.UserDataDTO
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val resourceProvider: ResourceProvider,
) : FirestoreApi {


    override fun getUserData(userId: String): Flow<Response<UserDataDTO>> = flow {
        emit(Response.Loading)
        val result = firestore.collection(FirestoreCollections.USER_DATA_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject(UserDataDTO::class.java)

        emit(result?.let { Response.Success(it) } ?: Response.Error(resourceProvider.getString(R.string.error_no_data)))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override suspend fun getDatabaseStatistics(): Flow<Response<DataStatistics>> = flow {
        emit(Response.Loading)

        val collectionCategories = firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_CATEGORIES)
        val collectionQuestions = firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_QUESTIONS)

        val categoriesCount = collectionCategories.count().get(AggregateSource.SERVER).await().count
        val questionsCount = collectionQuestions.count().get(AggregateSource.SERVER).await().count

        emit(Response.Success(DataStatistics(
            dataBaseName = "Test DB",
            quizModeStatistics = StatisticsQuizMode(
                numberOfCategories = categoriesCount,
                numberOfQuestions = questionsCount
            ),
            swipeQuizModeStatistics = 0,
            calculationsModeStatistics = 0,
            scenariosModeStatistics = 0
        )))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getQuizCategories(): Flow<Response<List<CategoryDTO>>> = flow {
        emit(Response.Loading)
        val categories = firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_CATEGORIES)
            .get().await()
            .toObjects(CategoryDTO::class.java)
        emit(Response.Success(categories))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getUpdatedQuizCategories(): Flow<List<CategoryDTO>> = callbackFlow {
        val listener = firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_CATEGORIES)
            .addSnapshotListener { value, error ->
                if(error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                value?.let { trySend(it.toObjects(CategoryDTO::class.java)) }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addQuizCategory(category: CategoryDTO): Response<Unit> =
        addFirestoreDocument(category.id.toString(), category, FirestoreCollections.TEST_QUIZ_MODE_CATEGORIES)

    override suspend fun updateQuizCategory(category: CategoryDTO): Response<Unit> =
        modifyFirestoreDocument(category.id.toString(), category, FirestoreCollections.TEST_QUIZ_MODE_CATEGORIES)

    override suspend fun deleteQuizCategory(categoryId: Long): Response<Unit> =
        deleteFirestoreDocument(categoryId.toString(), FirestoreCollections.TEST_QUIZ_MODE_CATEGORIES)

    override fun getQuizQuestions(): Flow<Response<List<QuestionDTO>>> = flow {
        emit(Response.Loading)
        val questions = firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_QUESTIONS)
            .get().await()
            .toObjects(QuestionDTO::class.java)
        emit(Response.Success(questions))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getUpdatedQuizQuestions(): Flow<List<QuestionDTO>> = callbackFlow {
        val listener = firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_QUESTIONS)
            .addSnapshotListener { value, error ->
                if(error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                value?.let { trySend(it.toObjects(QuestionDTO::class.java)) }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addQuizQuestion(question: QuestionDTO): Response<Unit> =
        addFirestoreDocument(question.id.toString(), question, FirestoreCollections.TEST_QUIZ_MODE_QUESTIONS)

    override suspend fun updateQuizQuestion(question: QuestionDTO): Response<Unit> =
        modifyFirestoreDocument(question.id.toString(), question, FirestoreCollections.TEST_QUIZ_MODE_QUESTIONS)

    override suspend fun deleteQuizQuestion(questionId: Long): Response<Unit> =
        deleteFirestoreDocument(questionId.toString(), FirestoreCollections.TEST_QUIZ_MODE_QUESTIONS)

    override fun getLatestMessages(): Flow<Response<List<MessageDTO>>> = flow {
        emit(Response.Loading)
        val messages = firestore.collection(FirestoreCollections.MESSAGES)
            .orderBy("timestamp", Query.Direction.DESCENDING).limit(15)
            .get().await()
            .toObjects(MessageDTO::class.java)
        emit(Response.Success(messages))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getUpdatedMessages(): Flow<List<MessageDTO>> = callbackFlow {
        val listener = firestore.collection(FirestoreCollections.MESSAGES)
            .orderBy("timestamp", Query.Direction.DESCENDING).limit(15)
            .addSnapshotListener { value, error ->
                if(value?.metadata?.isFromCache == true) return@addSnapshotListener
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val toSend = value?.documentChanges?.filter { it.type == DocumentChange.Type.ADDED }?.map {
                    it.document.toObject(MessageDTO::class.java)
                }
                toSend?.let { trySend(it) }
            }
        awaitClose{ listener.remove() }
    }

    override suspend fun sendMessage(message: MessageDTO): Response<Unit> {
        return try {
            firestore.collection(FirestoreCollections.MESSAGES)
                .document()
                .set(message)
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))
        }
    }

    private suspend fun <T : Any> addFirestoreDocument(
        id: String,
        data: T,
        collection: String,
    ): Response<Unit> {
        return try {
            firestore.collection(collection)
                .document(id)
                .set(data)
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))
        }
    }

    private suspend fun <T : Any> modifyFirestoreDocument(
        id: String,
        data: T,
        collection: String,
    ): Response<Unit> {
        return try {
            firestore.collection(collection)
                .document(id)
                .set(data, SetOptions.merge())
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))
        }
    }

    private suspend fun deleteFirestoreDocument(
        id: String,
        collection: String
    ): Response<Unit> {
        return try {
            firestore.collection(collection)
                .document(id)
                .delete()
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))
        }
    }
}