package com.rafalskrzypczyk.cem_mode.presentation.questions_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.cem_mode.presentation.questions_list.ui_models.CemQuestionUIModel
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.delete_bubble_manager.DeleteBubbleManager
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.core.utils.UITextHelpers

class CemQuestionsAdapter(
    private val onItemClicked: (CemQuestionUIModel) -> Unit,
    private val onItemDeleted: (CemQuestionUIModel) -> Unit
) : ListAdapter<CemQuestionUIModel, CemQuestionsAdapter.CemQuestionViewHolder>(
    GenericDiffCallback<CemQuestionUIModel>(
        itemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
        contentsTheSame = { oldItem, newItem -> oldItem == newItem }
    )
) {
    inner class CemQuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.question_text)
        val answersCount: TextView = view.findViewById(R.id.answers_count)
        val answersCountText: TextView = view.findViewById(R.id.answers_count_text)
        val validationIcon: ImageView = view.findViewById(R.id.validation_icon)
        val validationMessage: TextView = view.findViewById(R.id.validation_message)
        
        val deleteBubbleManager = DeleteBubbleManager(itemView.context)

        fun bind(question: CemQuestionUIModel) {
            questionText.text = question.text
            answersCount.text = question.answersCount.toString()
            answersCountText.text = UITextHelpers.provideDeclinedNumberText(
                question.answersCount,
                itemView.context.getString(R.string.label_answers_count_one),
                itemView.context.getString(R.string.label_answers_count_many),
                itemView.context.getString(R.string.label_answers_count_many)
            )

            validationIcon.setImageResource(question.validationMessage.icon)
            validationIcon.setColorFilter(itemView.context.getColor(question.validationMessage.color))
            validationMessage.text = itemView.context.getString(question.validationMessage.message)
            validationMessage.setTextColor(itemView.context.getColor(question.validationMessage.color))

            itemView.setOnClickListener { onItemClicked(question) }
            itemView.setOnLongClickListener { view ->
                deleteBubbleManager.showDeleteBubble(view) {
                    onItemDeleted(question)
                }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CemQuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_question, parent, false)
        return CemQuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CemQuestionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
