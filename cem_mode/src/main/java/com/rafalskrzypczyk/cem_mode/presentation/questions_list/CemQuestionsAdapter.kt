package com.rafalskrzypczyk.cem_mode.presentation.questions_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.cem_mode.presentation.questions_list.ui_models.CemQuestionUIModel
import com.rafalskrzypczyk.core.custom_views.ColorOutlinedLabelView
import com.rafalskrzypczyk.core.R as coreR
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
        val questionText: TextView = view.findViewById(coreR.id.question_text)
        val answersCount: TextView = view.findViewById(coreR.id.answers_count)
        val answersCountText: TextView = view.findViewById(coreR.id.answers_count_text)
        val validationIcon: ImageView = view.findViewById(coreR.id.validation_icon)
        val validationMessage: TextView = view.findViewById(coreR.id.validation_message)
        val categoriesContainer: LinearLayout = view.findViewById(coreR.id.categories_container)
        
        val deleteBubbleManager = DeleteBubbleManager(view.context)

        fun bind(question: CemQuestionUIModel) {
            questionText.text = question.text
            answersCount.text = question.answersCount.toString()
            answersCountText.text = UITextHelpers.provideDeclinedNumberText(
                question.answersCount,
                itemView.context.getString(coreR.string.label_answers_count_one),
                itemView.context.getString(coreR.string.label_answers_count_many),
                itemView.context.getString(coreR.string.label_answers_count_many)
            )

            validationIcon.setImageResource(question.validationMessage.icon)
            validationIcon.setColorFilter(itemView.context.getColor(question.validationMessage.color))
            validationMessage.text = itemView.context.getString(question.validationMessage.message)
            validationMessage.setTextColor(itemView.context.getColor(question.validationMessage.color))

            categoriesContainer.removeAllViews()
            question.linkedCategories.forEach { category ->
                val label = ColorOutlinedLabelView(itemView.context).apply {
                    setColorAndText(category.color.toInt(), category.name)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 8, 0)
                    }
                }
                categoriesContainer.addView(label)
            }

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
        val view = LayoutInflater.from(parent.context).inflate(coreR.layout.card_question, parent, false)
        return CemQuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CemQuestionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
