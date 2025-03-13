package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.delete_bubble_manager.DeleteBubbleManager
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.presentation.ListItemType
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.CategoriesPreviewAdapter
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models.QuestionUIModel

class QuestionsAdapter(
    private val onItemClicked: (QuestionUIModel) -> Unit,
    private val onItemDeleted: (QuestionUIModel) -> Unit,
    private val onAddClicked: () -> Unit,
) : ListAdapter<QuestionUIModel, RecyclerView.ViewHolder>(GenericDiffCallback<QuestionUIModel>(
    itemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
    contentsTheSame = { oldItem, newItem ->
        oldItem == newItem
    }
)) {
    inner class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.question_text)
        val questionAnswers: TextView = view.findViewById(R.id.answers_count)
        val questionAnswersText: TextView = view.findViewById(R.id.answers_count_text)
        val categoryLabelsRecyclerView: RecyclerView = view.findViewById(R.id.categories_recycler_view)
        val validationMessage: TextView = view.findViewById(R.id.validation_message)
        val validationIcon: ImageView = view.findViewById(R.id.validation_icon)

        val deleteBubbleManager = DeleteBubbleManager(view.context)

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

            itemView.setOnLongClickListener { view ->
                deleteBubbleManager.showDeleteBubble(view) {
                    onItemDeleted(question)
                }
                true
            }

            itemView.setOnClickListener {
                onItemClicked(question)
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
            holder.bind(question)
        } else if (holder is AddButtonViewHolder) {
            holder.bind()
        }
    }

    override fun getItemCount(): Int = currentList.size + 1
}