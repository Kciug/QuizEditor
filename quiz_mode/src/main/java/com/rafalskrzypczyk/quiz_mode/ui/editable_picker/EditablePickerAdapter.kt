package com.rafalskrzypczyk.quiz_mode.ui.editable_picker

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class EditablePickerAdapter(
    private val items: MutableList<Checkable> = mutableListOf(),
    private val onItemSelected: (Checkable) -> Unit,
    private val onItemDeselected: (Checkable) -> Unit
) : RecyclerView.Adapter<EditablePickerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView as CheckBox

        fun bind(item: Checkable) {
            view.text = item.title

            view.isChecked = item.isChecked
            view.isEnabled = !item.isLocked

            view.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) onItemSelected(item) else onItemDeselected(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = CheckBox(parent.context)
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
    fun updateData(newItems: List<Checkable>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}