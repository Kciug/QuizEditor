package com.rafalskrzypczyk.quiz_mode.presentation.categeory_details

import android.os.Bundle
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.utils.CategoryStatus

interface QuizCategoryDetailsContract {
    interface View {
        fun setupView()
        fun setupNewElementView()
        fun displayCategoryDetails(categoryTitle: String, categoryDescription: String)
        fun displayCategoryColor(color: Int)
        fun displayCategoryStatus(status: CategoryStatus)
        fun displayQuestionCount(questionCount: Int)
        fun displayQuestionList(questions: List<Question>)
    }
    interface Presenter {
        fun getData(bundle: Bundle?)
        fun createNewCategory(categoryTitle: String)
        fun updateCategoryTitle(categoryTitle: String)
        fun updateCategoryDescription(categoryDescription: String)
        fun updateCategoryColor(color: Int)
        fun updateCategoryStatus(status: CategoryStatus)
        fun updateQuestionList()
        fun getCategoryId(): Int
        fun getCategoryColor(): Int
        fun onViewClosed()
    }
}