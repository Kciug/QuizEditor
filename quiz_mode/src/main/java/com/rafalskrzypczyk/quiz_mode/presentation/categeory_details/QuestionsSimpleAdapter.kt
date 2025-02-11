package com.rafalskrzypczyk.quiz_mode.presentation.categeory_details

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.quiz_mode.domain.models.Question

class QuestionsSimpleAdapter(
    private val questions: MutableList<Question>
) : RecyclerView.Adapter<QuestionsSimpleAdapter.QuestionSimpleViewHolder>() {

    inner class QuestionSimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textView: TextView = itemView.findViewById(R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionSimpleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_list_item_1, parent, false)
        return QuestionSimpleViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionSimpleViewHolder, position: Int) {
        holder.textView.text = questions[position].text
    }

    override fun getItemCount() = questions.size

    fun removeItem(position: Int){
        questions.removeAt(position)
        notifyItemRemoved(position)
    }
}