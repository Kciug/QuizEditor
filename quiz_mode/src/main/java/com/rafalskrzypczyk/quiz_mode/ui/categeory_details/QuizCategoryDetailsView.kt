package com.rafalskrzypczyk.quiz_mode.ui.categeory_details

import androidx.lifecycle.LiveData
import com.rafalskrzypczyk.quiz_mode.models.Category
import com.rafalskrzypczyk.quiz_mode.models.Question

interface QuizCategoryDetailsView {
    fun displayCategoryDetails(category: LiveData<Category>, questions: List<Question>)
}