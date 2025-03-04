package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

interface QuizQuestionsContract{
    interface View {
        fun displayQuestions(questions: List<QuestionUIModel>)
    }
    interface Presenter {
        fun loadQuestions()
        fun removeQuestion(question: QuestionUIModel)
        fun searchBy(query: String)
        fun sortByOption(sort: QuestionSort.SortOptions)
        fun sortByType(sort: QuestionSort.SortTypes)
        fun filterBy(filter: QuestionFilter)
        fun getCurrentSortOption(): QuestionSort.SortOptions
        fun getCurrentSortType(): QuestionSort.SortTypes
        fun getCurrentFilter(): QuestionFilter
    }
}