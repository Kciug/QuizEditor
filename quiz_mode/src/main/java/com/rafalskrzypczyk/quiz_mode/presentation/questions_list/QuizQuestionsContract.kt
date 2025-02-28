package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

interface QuizQuestionsContract{
    interface View {
        fun displayQuestions(questions: List<QuestionUIModel>)
    }
    interface Presenter {
        fun loadQuestions()
        fun removeQuestion(question: QuestionUIModel)
        fun onSearchQueryChanged(query: String)
    }
}