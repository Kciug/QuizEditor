package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.delete_bubble_manager.DeleteBubbleManager
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.QuestionUIModel
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.CategoriesPreviewAdapter
import com.rafalskrzypczyk.quiz_mode.utils.ListItemType

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
        val categoryLabelsRecyclerView: RecyclerView = view.findViewById(R.id.categories_recycler_view)

        val deleteBubbleManager = DeleteBubbleManager(view.context)

        fun bind(question: QuestionUIModel) {
            val categoryLabelsAdapter = CategoriesPreviewAdapter()
            questionText.text = question.text
            questionAnswers.text = String.format(question.answersCount.toString())
            categoryLabelsRecyclerView.adapter = categoryLabelsAdapter
            categoryLabelsAdapter.submitList(question.linkedCategories)

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