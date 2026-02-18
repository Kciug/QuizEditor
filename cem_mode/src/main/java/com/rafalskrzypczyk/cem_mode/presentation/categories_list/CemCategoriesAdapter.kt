package com.rafalskrzypczyk.cem_mode.presentation.categories_list

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.core.custom_views.ColorOutlinedLabelView
import com.rafalskrzypczyk.core.delete_bubble_manager.DeleteBubbleManager
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.core.utils.UITextHelpers
import com.rafalskrzypczyk.core.R as coreR

class CemCategoriesAdapter(
    private val onCategoryClicked: (CemCategory) -> Unit,
    private val onCategoryRemoved: (CemCategory) -> Unit
) : ListAdapter<CemCategory, CemCategoriesAdapter.CemCategoryViewHolder>(GenericDiffCallback<CemCategory>(
    itemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
    contentsTheSame = { oldItem, newItem -> 
        oldItem == newItem && 
        oldItem.linkedQuestions.size == newItem.linkedQuestions.size &&
        oldItem.linkedSubcategories.size == newItem.linkedSubcategories.size
    }
)) {
    inner class CemCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryTitle: TextView = view.findViewById(coreR.id.label_category_title)
        val categoryDescription: TextView = view.findViewById(coreR.id.label_category_description)
        val questionCount: TextView = view.findViewById(coreR.id.category_questions_amount)
        val questionCountLabel: TextView = view.findViewById(coreR.id.label_category_questions_amount)
        
        val subcategoriesCount: TextView = view.findViewById(coreR.id.category_subcategories_amount)
        val subcategoriesCountLabel: TextView = view.findViewById(coreR.id.label_category_subcategories_amount)
        val statsDivider: TextView = view.findViewById(coreR.id.label_stats_divider)
        
        val statusIndicator: ColorOutlinedLabelView = view.findViewById(coreR.id.category_status)
        val colorPreview: View = view.findViewById(coreR.id.color_preview)
        val ivSyncStatus: View = view.findViewById(coreR.id.iv_sync_status)

        val deleteBubbleManager = DeleteBubbleManager(itemView.context)

        fun bind(category: CemCategory) {
            categoryTitle.text = category.title
            categoryDescription.text = category.description
            categoryDescription.visibility = if (category.description.isEmpty()) View.GONE else View.VISIBLE
            ivSyncStatus.visibility = if (category.productionTransferDate != null && !category.isUpToDate) View.VISIBLE else View.GONE
            
            val totalQuestions = category.linkedQuestions.count()
            questionCount.text = totalQuestions.toString()
            questionCountLabel.text = UITextHelpers.provideDeclinedNumberText(
                totalQuestions,
                itemView.context.getString(coreR.string.label_category_questions_amount_singular),
                itemView.context.getString(coreR.string.label_category_questions_amount_few),
                itemView.context.getString(coreR.string.label_category_questions_amount_many)
            )
            
            val totalSubcategories = category.linkedSubcategories.count()
            val hasSubcategories = totalSubcategories > 0
            subcategoriesCount.isVisible = hasSubcategories
            subcategoriesCountLabel.isVisible = hasSubcategories
            statsDivider.isVisible = hasSubcategories
            
            if (hasSubcategories) {
                subcategoriesCount.text = totalSubcategories.toString()
                subcategoriesCountLabel.text = UITextHelpers.provideDeclinedNumberText(
                    totalSubcategories,
                    itemView.context.getString(coreR.string.label_category_subcategories_amount_singular),
                    itemView.context.getString(coreR.string.label_category_subcategories_amount_few),
                    itemView.context.getString(coreR.string.label_category_subcategories_amount_many)
                )
            }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CemCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(coreR.layout.card_category, parent, false)
        return CemCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CemCategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }
}
