package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.custom_views.ColorOutlinedLabelView
import com.rafalskrzypczyk.core.delete_bubble_manager.DeleteBubbleManager
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.core.utils.UITextHelpers
import com.rafalskrzypczyk.core.R as coreR
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.domain.models.Category

class CategoriesAdapter(
    private val onCategoryClicked: (Category) -> Unit,
    private val onCategoryRemoved: (Category) -> Unit
) : ListAdapter<Category, CategoriesAdapter.CategoryViewHolder>(GenericDiffCallback<Category>(
    itemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
    contentsTheSame = { oldItem, newItem ->
        oldItem == newItem &&
        oldItem.linkedQuestions.count() == newItem.linkedQuestions.count()
    }
)) {
    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryTitle: TextView = view.findViewById(coreR.id.label_category_title)
        val categoryDescription: TextView = view.findViewById(coreR.id.label_category_description)
        val questionCount: TextView = view.findViewById(coreR.id.category_questions_amount)
        val questionCountLabel: TextView = view.findViewById(coreR.id.label_category_questions_amount)
        val statusIndicator: ColorOutlinedLabelView = view.findViewById(coreR.id.category_status)
        val colorPreview: View = view.findViewById(coreR.id.color_preview)
        val ivSyncStatus: View = view.findViewById(coreR.id.iv_sync_status)

        val deleteBubbleManager = DeleteBubbleManager(itemView.context)

        fun bind(category: Category) {
            categoryTitle.text = category.title
            categoryDescription.text = category.description
            categoryDescription.visibility = if (category.description.isEmpty()) View.GONE else View.VISIBLE
            ivSyncStatus.visibility = if (category.productionTransferDate != null && !category.isUpToDate) View.VISIBLE else View.GONE
            questionCount.text = String.format(category.linkedQuestions.count().toString())
            questionCountLabel.text = UITextHelpers.provideDeclinedNumberText(
                category.linkedQuestions.count(),
                itemView.context.getString(R.string.label_category_questions_amount_singular),
                itemView.context.getString(R.string.label_category_questions_amount_few),
                itemView.context.getString(R.string.label_category_questions_amount_many)
            )
            statusIndicator.setColorAndText(
                itemView.context.getColor(category.status.color),
                itemView.context.getString(category.status.title)
            )
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(coreR.layout.card_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }
}
