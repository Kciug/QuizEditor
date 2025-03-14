package com.rafalskrzypczyk.home.presentation

data class DataStatisticsUIModel (
    val dataBaseName: String,
    val quizModeStatistics: StatisticsQuizMode,
    val swipeQuizModeStatistics: Statistics,
    val calculationsModeStatistics: Statistics,
    val scenariosModeStatistics: Statistics
)

data class StatisticsQuizMode(
    val modeName: String,
    val numberOfCategories: Int,
    val numberOfQuestions: Int,
)

data class Statistics(
    val modeName: String,
    val numberOfQuestions: Int,
)