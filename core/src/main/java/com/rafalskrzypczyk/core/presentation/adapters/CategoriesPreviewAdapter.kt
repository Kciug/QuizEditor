package com.rafalskrzypczyk.core.presentation.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.core.custom_views.ColorOutlinedLabelView
import com.rafalskrzypczyk.core.presentation.ui_models.SimpleCategoryUIModel

class CategoriesPreviewAdapter : ListAdapter<SimpleCategoryUIModel, CategoriesPreviewAdapter.ViewHolder>(
    GenericDiffCallback(
        itemsTheSame = { oldItem, newItem ->
            oldItem.name == newItem.name
        },
        contentsTheSame = { oldItem, newItem ->
            oldItem == newItem
        }
    )
) {
    inner class ViewHolder(private val view: ColorOutlinedLabelView) : RecyclerView.ViewHolder(view) {
        fun bind(item: SimpleCategoryUIModel) {
            view.setColorAndText(item.color.toInt(), item.name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ColorOutlinedLabelView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
