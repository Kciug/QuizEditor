package com.rafalskrzypczyk.home

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.data_statistics.DataStatistics
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StatisticsRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi
) : StatisticsRepository {
    override suspend fun getStatistics(): Flow<Response<DataStatistics>> = firestoreApi.getDatabaseStatistics()
}