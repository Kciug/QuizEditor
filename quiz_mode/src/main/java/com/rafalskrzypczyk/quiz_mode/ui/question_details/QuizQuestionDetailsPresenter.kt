package com.rafalskrzypczyk.quiz_mode.ui.question_details

import android.os.Bundle
import com.rafalskrzypczyk.core.utils.formatDate
import com.rafalskrzypczyk.quiz_mode.TestQuestions
import com.rafalskrzypczyk.quiz_mode.domain.models.Answer
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models.AnswerUIModel
import com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models.toDomain
import com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models.toPresentation

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
        view.displayAnswersDetails(question.answers.count(), question.answers.count { it.isCorrect })
        view.displayAnswersList(question.answers.map { it.toPresentation() })
        view.displayCreatedOn(String.formatDate(question.creationDate), question.createdBy)
        view.displayLinkedCategories(emptyList())
    }

    override fun saveNewQuestion(questionText: String) {
        if(questionText.isEmpty()) return

        question = Question.new(questionText)

        TestQuestions.questions.add(question)
        view.setupView()
        view.displayAnswersDetails(question.answers.count(), question.answers.count { it.isCorrect })
        view.displayCreatedOn(question.creationDate.toString(), question.createdBy)
    }

    override fun updateQuestionText(questionText: String) {
        question.text = questionText
    }

    override fun addAnswer(answerText: String) {
        if(answerText.isEmpty()) return
        val newAnswer = Answer.new(answerText)
        question.answers.add(newAnswer)
        view.addNewAnswer(newAnswer.toPresentation())
        view.displayAnswersDetails(question.answers.count(), question.answers.count { it.isCorrect })
    }

    override fun updateAnswer(answer: AnswerUIModel) {
        val answerIndex = question.answers.indexOfFirst { it.id == answer.id }
        if(answerIndex != -1) question.answers[answerIndex] = answer.toDomain()
        view.displayAnswersDetails(question.answers.count(), question.answers.count { it.isCorrect })
    }

    override fun removeAnswer(answer: AnswerUIModel, answerPosition: Int) {
        question.answers.remove(answer.toDomain())
        view.displayAnswersDetails(question.answers.count(), question.answers.count { it.isCorrect })
        view.removeAnswer(answerPosition)
    }
}