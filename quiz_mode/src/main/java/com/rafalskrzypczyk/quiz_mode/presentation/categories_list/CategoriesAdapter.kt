package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

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
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.presentation.custom_views.ColorOutlinedLabelView
import com.rafalskrzypczyk.quiz_mode.presentation.ListItemType
import com.rafalskrzypczyk.quiz_mode.domain.getColor
import com.rafalskrzypczyk.quiz_mode.domain.getTitle

class CategoriesAdapter(
    private val onCategoryClicked: (Category) -> Unit,
    private val onCategoryRemoved: (Category) -> Unit,
    private val onAddClicked: () -> Unit
) : ListAdapter<Category, RecyclerView.ViewHolder>(GenericDiffCallback<Category>(
    itemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
    contentsTheSame = { oldItem, newItem ->
        oldItem == newItem &&
        oldItem.linkedQuestions.count() == newItem.linkedQuestions.count()
    }
)) {
    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryTitle: TextView = view.findViewById(R.id.label_category_title)
        val categoryDescription: TextView = view.findViewById(R.id.label_category_description)
        val questionCount: TextView = view.findViewById(R.id.category_questions_amount)
        val questionCountLabel: TextView = view.findViewById(R.id.label_category_questions_amount)
        val statusIndicator: ColorOutlinedLabelView = view.findViewById(R.id.category_status)
        val colorPreview: View = view.findViewById(R.id.color_preview)

        val deleteBubbleManager = DeleteBubbleManager(itemView.context)

        fun bind(category: Category) {
            categoryTitle.text = category.title
            categoryDescription.text = category.description
            questionCount.text = String.format(category.linkedQuestions.count().toString())
            questionCountLabel.text = UITextHelpers.provideDeclinedNumberText(
                category.linkedQuestions.count(),
                itemView.context.getString(R.string.label_category_questions_amount_singular),
                itemView.context.getString(R.string.label_category_questions_amount_few),
                itemView.context.getString(R.string.label_category_questions_amount_many)
            )
            statusIndicator.setColorAndText(category.status.getColor(itemView.context), category.status.getTitle(itemView.context))
            (colorPreview.background as GradientDrawable).setColor(category.color)

            itemView.setOnLongClickListener { view ->
                deleteBubbleManager.showDeleteBubble(view) {
                    onCategoryRemoved(category)
                }
                true
            }

            itemView.setOnClickListener {
                onCategoryClicked(category)
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
                .inflate(R.layout.card_category, parent, false)
            CategoryViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(com.rafalskrzypczyk.core.R.layout.card_add_new, parent, false)
            AddButtonViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CategoryViewHolder) {
            val category = getItem(position)
            holder.bind(category)
        } else if (holder is AddButtonViewHolder) {
            holder.bind()
        }
    }

    override fun getItemCount(): Int = currentList.size + 1

    override fun submitList(list: List<Category>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}
