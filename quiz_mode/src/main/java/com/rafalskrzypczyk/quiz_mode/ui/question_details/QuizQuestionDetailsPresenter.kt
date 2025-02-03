package com.rafalskrzypczyk.quiz_mode.ui.question_details

import android.icu.util.Calendar
import android.os.Bundle
import com.rafalskrzypczyk.quiz_mode.TestQuestions
import com.rafalskrzypczyk.quiz_mode.models.Answer
import com.rafalskrzypczyk.quiz_mode.models.Question
import com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models.SimpleCategoryUIModel
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
        view.displayCreatedOn(question.creationDate.toString(), question.createdBy)
        view.displayLinkedCategories(listOf(
            SimpleCategoryUIModel(
                name = "Zbrodnie wojenne",
                color = 0xFFE53935
            ),
            SimpleCategoryUIModel(
                name = "Fekalia",
                color = 0xFFBB86FC
            ),
            SimpleCategoryUIModel(
                name = "Jebanie",
                color = 0xFFBB86FC
            ),
            SimpleCategoryUIModel(
                name = "Imperium Rzymskie",
                color = 0xFFBB86FC
            ),
            SimpleCategoryUIModel(
                name = "Twoja stara",
                color = 0xFFBB86FC
            ),
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
        view.displayAnswersDetails(question.answers.count(), question.answers.count { it.isCorrect })
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
        view.displayAnswersList(listOf(
            AnswerUIModel(
                id = 1,
                answerText = "Tak",
                isCorrect = false
            ),
            AnswerUIModel(
                id = 1,
                answerText = "Srak",
                isCorrect = false
            ),
            AnswerUIModel(
                id = 1,
                answerText = "Kurwa",
                isCorrect = false
            ),
            AnswerUIModel(
                id = 1,
                answerText = "Nie",
                isCorrect = false
            ),
            AnswerUIModel(
                id = 1,
                answerText = "Wytrzymam",
                isCorrect = false
            ),
            AnswerUIModel(
                id = 1,
                answerText = "Jebane",
                isCorrect = false
            ),
            AnswerUIModel(
                id = 1,
                answerText = "Chujstwo",
                isCorrect = false
            ),
        ))

    }

    override fun updateQuestionText(questionText: String) {
        question.text = questionText
    }

    override fun addAnswer(answerText: String) {
        if(answerText.isEmpty()) return
        val newAnswer = Answer(
            id = question.answers.size + 1,
            answerText = answerText,
            isCorrect = false
        )
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