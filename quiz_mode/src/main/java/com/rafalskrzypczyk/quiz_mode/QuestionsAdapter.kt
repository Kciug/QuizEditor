package com.rafalskrzypczyk.quiz_mode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.quiz_mode.models.Question

class QuestionsAdapter(private val questions: List<Question>) :
    RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>()
{
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
//        val binding = ItemQuestionBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return QuestionViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
//        val question = questions[position]
//        holder.bind(question)
//    }
//
//    override fun getItemCount(): Int = questions.size
//
//    inner class QuestionViewHolder(private val binding: ItemQuestionBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(question: Question) {
//            binding.tvQuestion.text = question.text
//            binding.tvAnswersCount.text = "${question.answers.size} Answers"
//
//            // Kategorie (przykładowe)
//            val categories = listOf("General Knowledge", "Body") // Można dynamicznie dostosować
//            binding.llCategories.removeAllViews()
//            categories.forEach { category ->
//                val categoryView = LayoutInflater.from(binding.root.context)
//                    .inflate(R.layout.category_chip, binding.llCategories, false)
//                (categoryView as? TextView)?.text = category
//                binding.llCategories.addView(categoryView)
//            }
//
//            // Status walidacji (przykład: true -> zielony check, false -> error)
//            val allCorrect = question.answers.all { it.isCorrect }
//            binding.ivValidationStatus.setImageResource(
//                if (allCorrect) R.drawable.ic_check else R.drawable.ic_error
//            )
//
//            // Kliknięcie w element
//            binding.root.setOnClickListener { onItemClick(question) }
//        }
//    }

    // ViewHolder dla pojedynczego elementu
    class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.question_text)
        val questionAnswers: TextView = view.findViewById(R.id.answers_count_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]
        holder.questionText.text = question.text
        holder.questionAnswers.text = "${question.answers.size} Answers"
    }

    override fun getItemCount(): Int = questions.size
}