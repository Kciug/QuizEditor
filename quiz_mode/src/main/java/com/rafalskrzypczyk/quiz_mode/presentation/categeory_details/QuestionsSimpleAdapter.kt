package com.rafalskrzypczyk.quiz_mode.presentation.categeory_details

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.quiz_mode.domain.models.Question

class QuestionsSimpleAdapter : ListAdapter<Question, QuestionsSimpleAdapter.QuestionSimpleViewHolder>(
    GenericDiffCallback(
        itemsTheSame = { old, new -> old.id == new.id },
        contentsTheSame = { old, new -> old == new }
    )
) {
    inner class QuestionSimpleViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionSimpleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_list_item_1, parent, false)
        return QuestionSimpleViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionSimpleViewHolder, position: Int) {
        holder.textView.text = getItem(position).text
    }
}