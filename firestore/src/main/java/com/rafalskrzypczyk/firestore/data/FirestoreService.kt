package com.rafalskrzypczyk.firestore.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.firestore.data.models.CategoryDTO
import com.rafalskrzypczyk.firestore.data.models.QuestionDTO
import com.rafalskrzypczyk.firestore.data.models.UserDataDTO
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
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

        if (result != null)
            emit(Response.Success(result))
    }.catch {
        emit(
            Response.Error(
                it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown)
            )
        )
    }

    override fun getQuizCategories(): Flow<Response<List<CategoryDTO>>> = channelFlow {
        trySend(Response.Loading)
        val collection = firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_CATEGORIES)
        collection.get().await().toObjects(CategoryDTO::class.java).let {
                trySend(Response.Success(it))
            }
    }.catch {
        emit(
            Response.Error(
                it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown)
            )
        )
    }

    override fun getUpdatedQuizCategories(): Flow<List<CategoryDTO>> = callbackFlow {
        val collection = firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_CATEGORIES)
        collection.snapshots().collectLatest {
            if (it.isEmpty.not()) {
                val categories = it.toObjects(CategoryDTO::class.java)
                trySend(categories)
            }
        }
    }

    override suspend fun addQuizCategory(category: CategoryDTO): Response<Unit> {
        return try {
            firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_CATEGORIES)
                .document(category.id.toString())
                .set(category)
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))
        }
    }

    override suspend fun updateQuizCategory(category: CategoryDTO): Response<Unit> {
        return try {
            firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_CATEGORIES)
                .document(category.id.toString())
                .set(category, SetOptions.merge())
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))
        }
    }

    override suspend fun deleteQuizCategory(categoryId: Long): Response<Unit> {
        return try {
            firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_CATEGORIES)
                .document(categoryId.toString())
                .delete()
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))
        }
    }

    override fun getQuizQuestions(): Flow<Response<List<QuestionDTO>>> = channelFlow {
        trySend(Response.Loading)
        val collection = firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_QUESTIONS)
        collection.get().await().toObjects(QuestionDTO::class.java).let {
            trySend(Response.Success(it))
        }
    }.catch {
        emit(
            Response.Error(
                it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown)
            )
        )
    }

    override fun getUpdatedQuizQuestions(): Flow<List<QuestionDTO>> = callbackFlow {
        val collection = firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_QUESTIONS)
        collection.snapshots().collectLatest {
            if (it.isEmpty.not()) {
                val questions = it.toObjects(QuestionDTO::class.java)
                trySend(questions)
            }
        }
    }

    override suspend fun addQuizQuestion(question: QuestionDTO): Response<Unit> {
        return try {
            firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_QUESTIONS)
                .document(question.id.toString())
                .set(question)
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))
        }
    }

    override suspend fun updateQuizQuestion(question: QuestionDTO): Response<Unit> {
        return try {
            firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_QUESTIONS)
                .document(question.id.toString())
                .set(question, SetOptions.merge())
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))
        }
    }

    override suspend fun deleteQuizQuestion(questionId: Long): Response<Unit> {
        return try {
            firestore.collection(FirestoreCollections.TEST_QUIZ_MODE_QUESTIONS)
                .document(questionId.toString())
                .delete()
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))
        }
    }
}