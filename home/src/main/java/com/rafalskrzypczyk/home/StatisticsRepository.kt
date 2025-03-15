package com.rafalskrzypczyk.home

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.data_statistics.DataStatistics
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    suspend fun getStatistics(): Flow<Response<DataStatistics>>
}