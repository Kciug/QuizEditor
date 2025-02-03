package com.rafalskrzypczyk.quiz_mode.ui.categeory_details

import androidx.lifecycle.LiveData
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Question

interface QuizCategoryDetailsView {
    fun displayCategoryDetails(category: LiveData<Category>, questions: List<Question>)
}