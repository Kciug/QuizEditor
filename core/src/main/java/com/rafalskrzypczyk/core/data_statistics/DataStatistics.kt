package com.rafalskrzypczyk.core.data_statistics

data class DataStatistics (
    val dataBaseName: String,
    val quizModeStatistics: StatisticsQuizMode,
    val swipeQuizModeStatistics: Long,
    val calculationsModeStatistics: Long,
    val scenariosModeStatistics: Long
)

data class StatisticsQuizMode(
    val numberOfCategories: Long,
    val numberOfQuestions: Long,
)