package com.rafalskrzypczyk.quiz_mode.ui.question_details

import android.icu.util.Calendar
import android.os.Bundle
import com.rafalskrzypczyk.quiz_mode.TestQuestions
import com.rafalskrzypczyk.quiz_mode.models.Question
import com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.quiz_mode.ui.view_models.AnswerUIModel
import com.rafalskrzypczyk.quiz_mode.ui.view_models.toDomain
import com.rafalskrzypczyk.quiz_mode.ui.view_models.toPresentation

class QuizQuestionDetailsPresenter(
    private val view: QuizQuestionDetailsContract.View
) : QuizQuestionDetailsContract.Presenter {
    private lateinit var question: Question

    override fun getData(bundle: Bundle?) {
        val questionId = bundle?.getInt("questionId")

        if (questionId == null) {
            view.setupNewElementView()
            return
        }

        question = TestQuestions.questions.find { it.id == questionId }!!

        view.setupView()
        view.displayQuestionText(question.text)
        view.displayAnswersList(
            question.answers.map { it.toPresentation() },
            question.answers.size,
            question.answers.count { it.isCorrect })
        view.displayCreatedOn(question.creationDate.toString(), question.createdBy)
        view.displayLinkedCategories(listOf(
            SimpleCategoryUIModel(
                name = "Zbrodnie wojenne",
                color = 0xFFE53935
            ),
            SimpleCategoryUIModel(
                name = "Fekalia",
                color = 0xFFBB86FC
            )
        ))
    }

    override fun saveNewQuestion(questionText: String) {
        if(questionText.isEmpty()) return

        question = Question(
            id = TestQuestions.questions.size + 1,
            text = questionText,
            answers = mutableListOf(),
            creationDate = Calendar.getInstance().time,
            createdBy = "Kurwa Chuj"
        )

        TestQuestions.questions.add(question)
        view.setupView()
    }

    override fun updateQuestionText(questionText: String) {
        question.text = questionText
    }

    override fun addAnswer(answer: AnswerUIModel) {
        question.answers.add(answer.toDomain())
    }

    override fun updateAnswer(answer: AnswerUIModel) {
        val answerIndex = question.answers.indexOfFirst { it.id == answer.id }
        if(answerIndex != -1) question.answers[answerIndex] = answer.toDomain()
    }

    override fun removeAnswer(answer: AnswerUIModel) {
        question.answers.remove(answer.toDomain())
    }
}