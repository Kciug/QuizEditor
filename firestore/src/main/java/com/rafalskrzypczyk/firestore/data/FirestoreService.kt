package com.rafalskrzypczyk.firestore.data

import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.data_statistics.DataStatistics
import com.rafalskrzypczyk.core.data_statistics.StatisticsQuizMode
import com.rafalskrzypczyk.core.database_management.Database
import com.rafalskrzypczyk.core.database_management.DatabaseManager
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.firestore.data.models.CategoryDTO
import com.rafalskrzypczyk.firestore.data.models.MessageDTO
import com.rafalskrzypczyk.firestore.data.models.QuestionDTO
import com.rafalskrzypczyk.firestore.data.models.SwipeQuestionDTO
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
    databaseManager: DatabaseManager,
) : FirestoreApi {
    private var userDataCollection = FirestoreCollections.USER_DATA_COLLECTION
    private var messagesCollection = FirestoreCollections.MESSAGES
    private var quizCategoriesCollection = FirestoreCollections.TEST_QUIZ_MODE_CATEGORIES
    private var quizQuestionsCollection = FirestoreCollections.TEST_QUIZ_MODE_QUESTIONS
    private var swipeQuestionsCollection = FirestoreCollections.TEST_SWIPE_QUESTIONS

    init {
        setDatabaseCollections(databaseManager.getDatabase())
        databaseManager.setDatabaseChangedCallback { setDatabaseCollections(it) }
    }

    override fun getUserData(userId: String): Flow<Response<UserDataDTO>> = flow {
        emit(Response.Loading)
        val result = firestore.collection(userDataCollection)
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
        val categories = getFirestoreData(quizCategoriesCollection)?.toObjects(CategoryDTO::class.java) ?: emptyList()
        emit(Response.Success(categories))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getUpdatedQuizCategories(): Flow<List<CategoryDTO>> = callbackFlow {
        val listener = firestore.collection(quizCategoriesCollection)
            .addSnapshotListener { value, error ->
                if(value?.metadata?.isFromCache == true) return@addSnapshotListener
                if(error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                value?.let { trySend(it.toObjects(CategoryDTO::class.java)) }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addQuizCategory(category: CategoryDTO): Response<Unit> =
        addFirestoreDocument(category.id.toString(), category, quizCategoriesCollection)

    override suspend fun updateQuizCategory(category: CategoryDTO): Response<Unit> =
        modifyFirestoreDocument(category.id.toString(), category, quizCategoriesCollection)

    override suspend fun deleteQuizCategory(categoryId: Long): Response<Unit> =
        deleteFirestoreDocument(categoryId.toString(), quizCategoriesCollection)

    override fun getQuizQuestions(): Flow<Response<List<QuestionDTO>>> = flow {
        emit(Response.Loading)
        val questions = getFirestoreData(quizQuestionsCollection)?.toObjects(QuestionDTO::class.java) ?: emptyList()
        emit(Response.Success(questions))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getUpdatedQuizQuestions(): Flow<List<QuestionDTO>> = callbackFlow {
        val listener = firestore.collection(quizQuestionsCollection)
            .addSnapshotListener { value, error ->
                if(value?.metadata?.isFromCache == true) return@addSnapshotListener
                if(error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                value?.let { trySend(it.toObjects(QuestionDTO::class.java)) }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addQuizQuestion(question: QuestionDTO): Response<Unit> =
        addFirestoreDocument(question.id.toString(), question, quizQuestionsCollection)

    override suspend fun updateQuizQuestion(question: QuestionDTO): Response<Unit> =
        modifyFirestoreDocument(question.id.toString(), question, quizQuestionsCollection)

    override suspend fun deleteQuizQuestion(questionId: Long): Response<Unit> =
        deleteFirestoreDocument(questionId.toString(), quizQuestionsCollection)

    override fun getSwipeQuestions(): Flow<Response<List<SwipeQuestionDTO>>> = flow {
        emit(Response.Loading)
        val questions = getFirestoreData(swipeQuestionsCollection)?.toObjects(SwipeQuestionDTO::class.java) ?: emptyList()
        emit(Response.Success(questions))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getUpdatedSwipeQuestions(): Flow<List<SwipeQuestionDTO>> = callbackFlow {
        val listener = firestore.collection(swipeQuestionsCollection)
            .addSnapshotListener { value, error ->
                if(value?.metadata?.isFromCache == true) return@addSnapshotListener
                if(error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                value?.let { trySend(it.toObjects(SwipeQuestionDTO::class.java)) }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addSwipeQuestion(question: SwipeQuestionDTO): Response<Unit> =
        addFirestoreDocument(question.id.toString(), question, swipeQuestionsCollection)

    override suspend fun updateSwipeQuestion(question: SwipeQuestionDTO): Response<Unit> =
        modifyFirestoreDocument(question.id.toString(), question, swipeQuestionsCollection)

    override suspend fun deleteSwipeQuestion(questionId: Long): Response<Unit> =
        deleteFirestoreDocument(questionId.toString(), swipeQuestionsCollection)

    private val messagesLimit = 15L
    private var lastObservedMessage: MessageDTO? = null

    override fun getLatestMessages(): Flow<Response<List<MessageDTO>>> = flow {
        emit(Response.Loading)
        val messages = firestore.collection(messagesCollection)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(messagesLimit)
            .get().await()
        val mappedMessages = messages.toObjects(MessageDTO::class.java)
        lastObservedMessage = mappedMessages.lastOrNull()
        emit(Response.Success(mappedMessages))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getOlderMessages(): Flow<Response<List<MessageDTO>>> = flow {
        emit(Response.Loading)
        val olderMessages = firestore.collection(messagesCollection)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .startAfter(lastObservedMessage?.timestamp)
            .limit(messagesLimit)
            .get().await()

        val fetchedMessages = olderMessages.toObjects(MessageDTO::class.java)
        lastObservedMessage = fetchedMessages.lastOrNull()
        emit(Response.Success(fetchedMessages))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getUpdatedMessages(): Flow<List<MessageDTO>> = callbackFlow {
        val listener = firestore.collection(messagesCollection)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(messagesLimit)
            .addSnapshotListener { value, error ->
                if(value?.metadata?.isFromCache == true) return@addSnapshotListener
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val toSend = value?.documentChanges?.filter { it.type == DocumentChange.Type.ADDED }?.map {
                    it.document.toObject(MessageDTO::class.java)
                }
                toSend?.let { Log.d("KURWA", "listener: $it") }
                toSend?.let { trySend(it) }
            }
        awaitClose{ listener.remove() }
    }

    override suspend fun sendMessage(message: MessageDTO): Response<Unit> {
        return try {
            firestore.collection(messagesCollection)
                .document()
                .set(message)
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))
        }
    }

    private suspend fun getFirestoreData(collection: String): QuerySnapshot? {
        return firestore.collection(collection)
            .get(Source.CACHE)
            .await()
            .takeIf { it.isEmpty.not() }
            ?: firestore.collection(collection).get(Source.SERVER).await()
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
            Log.d("KURWA", "modifyFirestoreDocument: $data")
            Response.Success(Unit)
        } catch (e: Exception) {
            Log.d("KURWA", "modifyFirestoreDocument: $e")
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

    private fun setDatabaseCollections(database: Database) {
        when (database) {
            Database.TEST -> {
                quizCategoriesCollection = FirestoreCollections.TEST_QUIZ_MODE_CATEGORIES
                quizQuestionsCollection = FirestoreCollections.TEST_QUIZ_MODE_QUESTIONS
                swipeQuestionsCollection = FirestoreCollections.TEST_SWIPE_QUESTIONS
            }
            Database.DEVELOPMENT -> {
                quizCategoriesCollection = FirestoreCollections.DEVELOPMENT_QUIZ_MODE_CATEGORIES
                quizQuestionsCollection = FirestoreCollections.DEVELOPMENT_QUIZ_MODE_QUESTIONS
                swipeQuestionsCollection = FirestoreCollections.DEVELOPMENT_SWIPE_QUESTIONS
            }
            Database.PRODUCTION -> {
                quizCategoriesCollection = FirestoreCollections.PRODUCTION_QUIZ_MODE_CATEGORIES
                quizQuestionsCollection = FirestoreCollections.PRODUCTION_QUIZ_MODE_QUESTIONS
                swipeQuestionsCollection = FirestoreCollections.PRODUCTION_SWIPE_QUESTIONS
            }
        }
    }
}