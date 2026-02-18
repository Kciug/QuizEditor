package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.delete_bubble_manager.DeleteBubbleManager
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.core.R as coreR
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.CategoriesPreviewAdapter
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionUIModel

class QuestionsAdapter(
    private val onItemClicked: (QuestionUIModel) -> Unit,
    private val onItemDeleted: (QuestionUIModel) -> Unit,
) : ListAdapter<QuestionUIModel, QuestionsAdapter.QuestionViewHolder>(GenericDiffCallback<QuestionUIModel>(
    itemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
    contentsTheSame = { oldItem, newItem ->
        oldItem == newItem
    }
)) {
    inner class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(coreR.id.question_text)
        val questionAnswers: TextView = view.findViewById(coreR.id.answers_count)
        val questionAnswersText: TextView = view.findViewById(coreR.id.answers_count_text)
        val categoryLabelsRecyclerView: RecyclerView = view.findViewById(coreR.id.categories_recycler_view)
        val validationMessage: TextView = view.findViewById(coreR.id.validation_message)
        val validationIcon: ImageView = view.findViewById(coreR.id.validation_icon)

        val deleteBubbleManager = DeleteBubbleManager(view.context)

        @SuppressLint("ClickableViewAccessibility")
        fun bind(question: QuestionUIModel) {
            val categoryLabelsAdapter = CategoriesPreviewAdapter()
            questionText.text = question.text
            questionAnswers.text = String.format(question.answersCount.toString())
            categoryLabelsRecyclerView.adapter = categoryLabelsAdapter
            categoryLabelsAdapter.submitList(question.linkedCategories)

            questionAnswersText.text = if (question.answersCount == 1) itemView.context.getString(R.string.label_answers_count_one)
            else itemView.context.getString(R.string.label_answers_count_many)

            validationMessage.text = itemView.context.getString(question.validationMessage.message)
            validationIcon.setImageResource(question.validationMessage.icon)
            validationMessage.setTextColor(itemView.context.getColor(question.validationMessage.color))
            validationIcon.setColorFilter(itemView.context.getColor(question.validationMessage.color))

            val gestureDetector = GestureDetector(itemView.context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    itemView.performClick()
                    return super.onSingleTapUp(e)
                }
            })

            itemView.setOnLongClickListener { view ->
                deleteBubbleManager.showDeleteBubble(view) {
                    onItemDeleted(question)
                }
                true
            }

            categoryLabelsRecyclerView.setOnTouchListener { view , event ->
                if (gestureDetector.onTouchEvent(event)){
                    view.performClick()
                    true
                } else{
                    false
                }
            }

            itemView.setOnClickListener {
                onItemClicked(question)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(coreR.layout.card_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = getItem(position)
        holder.bind(question)
    }
}
