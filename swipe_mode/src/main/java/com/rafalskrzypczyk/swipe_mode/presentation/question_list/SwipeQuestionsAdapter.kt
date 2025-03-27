package com.rafalskrzypczyk.swipe_mode.presentation.question_list

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.delete_bubble_manager.DeleteBubbleManager
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.core.utils.UITextHelpers
import com.rafalskrzypczyk.swipe_mode.R
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models.SwipeQuestionSimpleUIModel

class SwipeQuestionsAdapter (
    private val onCategoryClicked: (Long) -> Unit,
    private val onCategoryRemoved: (Long) -> Unit
) : ListAdapter<SwipeQuestionSimpleUIModel, SwipeQuestionsAdapter.ViewHolder>(GenericDiffCallback<SwipeQuestionSimpleUIModel>(
    itemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
    contentsTheSame = { oldItem, newItem -> oldItem == newItem }
)) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_swipe_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionTitle: TextView = view.findViewById(R.id.tv_question_text)
        val labelCorrectness: TextView = view.findViewById(R.id.label_correctness)

        val deleteBubbleManager = DeleteBubbleManager(itemView.context)

        val colorCorrect = itemView.context.getColor(com.rafalskrzypczyk.core.R.color.green)
        val colorIncorrect = itemView.context.getColor(com.rafalskrzypczyk.core.R.color.red)

        fun bind(item: SwipeQuestionSimpleUIModel){
            questionTitle.text = item.text
            labelCorrectness.text = if(item.isCorrect == true) itemView.context.getString(R.string.label_question_correct)
                else itemView.context.getString(R.string.label_question_incorrect)

            (labelCorrectness.background as GradientDrawable).setColor(
                if(item.isCorrect == true) colorCorrect else colorIncorrect
            )

            labelCorrectness.setTextColor(UITextHelpers.Companion.getContrastingTextColor(
                if(item.isCorrect == true) colorCorrect else colorIncorrect)
            )

            itemView.setOnClickListener {
                onCategoryClicked(item.id)
            }

            itemView.setOnLongClickListener { view ->
                deleteBubbleManager.showDeleteBubble(view) {
                    onCategoryRemoved(item.id)
                }
                true
            }
        }
    }
}