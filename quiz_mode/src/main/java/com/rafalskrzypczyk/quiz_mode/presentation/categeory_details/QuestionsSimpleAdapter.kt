package com.rafalskrzypczyk.quiz_mode.presentation.categeory_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.domain.models.Question

class QuestionsSimpleAdapter : ListAdapter<Question, QuestionsSimpleAdapter.QuestionSimpleViewHolder>(
    GenericDiffCallback(
        itemsTheSame = { old, new -> old.id == new.id },
        contentsTheSame = { old, new -> old == new }
    )
) {
    inner class QuestionSimpleViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val numberView: TextView = itemView.findViewById(R.id.question_number)
        val textView: TextView = itemView.findViewById(R.id.question_text)

        fun bind(position: Int) {
            numberView.text = String.format((position + 1).toString())
            textView.text = getItem(position).text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionSimpleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_simple_question_numered, parent, false)
        return QuestionSimpleViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionSimpleViewHolder, position: Int) {
        holder.bind(position)
    }
}