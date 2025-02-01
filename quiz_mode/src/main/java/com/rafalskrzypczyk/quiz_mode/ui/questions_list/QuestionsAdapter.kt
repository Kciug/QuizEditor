package com.rafalskrzypczyk.quiz_mode.ui.questions_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.quiz_mode.utils.ListItemType
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.models.Category
import com.rafalskrzypczyk.quiz_mode.models.Question

//class QuestionsAdapter(private val questions: List<Question>) :
//    RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>()
//{
//    // ViewHolder dla pojedynczego elementu
//    class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val questionText: TextView = view.findViewById(R.id.question_text)
//        val questionAnswers: TextView = view.findViewById(R.id.answers_count_text)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.card_question, parent, false)
//        return QuestionViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
//        val question = questions[position]
//        holder.questionText.text = question.text
//        holder.questionAnswers.text = "${question.answers.size} Answers"
//    }
//
//    override fun getItemCount(): Int = questions.size
//}

class QuestionsAdapter(
    private val onItemClicked: (Question, Int) -> Unit,
    private val onAddClicked: () -> Unit
) : ListAdapter<Question, RecyclerView.ViewHolder>(GenericDiffCallback<Question>(
    areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
    areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
)) {
    inner class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.question_text)
        val questionAnswers: TextView = view.findViewById(R.id.answers_count)

        fun bind(question: Question, position: Int) {
            questionText.text = question.text
            questionAnswers.text = String.format(question.answers.count().toString())

            itemView.setOnClickListener {
                onItemClicked(question, position)
            }
        }
    }

    inner class AddButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val button: View = view.findViewById(com.rafalskrzypczyk.core.R.id.button_add_new)

        fun bind() {
            button.setOnClickListener { onAddClicked() }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < currentList.size) {
            ListItemType.TYPE_ELEMENT.value
        } else {
            ListItemType.TYPE_ADD_BUTTON.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ListItemType.TYPE_ELEMENT.value) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_question, parent, false)
            QuestionViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(com.rafalskrzypczyk.core.R.layout.card_add_new, parent, false)
            AddButtonViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is QuestionViewHolder) {
            val question = getItem(position)
            holder.bind(question, position)
        } else if (holder is AddButtonViewHolder) {
            holder.bind()
        }
    }

    override fun getItemCount(): Int = currentList.size + 1
}