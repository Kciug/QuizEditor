package com.rafalskrzypczyk.quiz_mode.ui.question_details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.quiz_mode.ui.custom_views.CategoryLabelView
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models.SimpleCategoryUIModel

class CategoriesPreviewAdapter(
    private val items: MutableList<SimpleCategoryUIModel> = mutableListOf()
) : RecyclerView.Adapter<CategoriesPreviewAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView as CategoryLabelView

        fun bind(item: SimpleCategoryUIModel) {
            view.setColorAndText(item.color.toInt(), item.name)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = CategoryLabelView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<SimpleCategoryUIModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}