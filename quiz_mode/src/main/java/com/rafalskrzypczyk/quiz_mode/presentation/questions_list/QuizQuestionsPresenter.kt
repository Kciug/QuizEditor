package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.util.Log
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizQuestionsPresenter @Inject constructor(
    private val view: QuizQuestionsContract.View,
    private val repository: QuizModeRepository
) : BasePresenter(), QuizQuestionsContract.Presenter {
    override fun loadAllQuestions(){
        presenterScope.launch{
            repository.getAllQuestions()
                .collect{
                    when(it){
                        is Response.Success -> {
                            view.displayAllQuestions(it.data)
                        }
                        is Response.Error -> {
                            Log.e("QuizQuestionsPresenter", "Error: ${it.error}")
                        }
                        is Response.Loading -> {
                            Log.d("QuizQuestionsPresenter", "Loading")
                        }
                    }
                }
        }
    }
}