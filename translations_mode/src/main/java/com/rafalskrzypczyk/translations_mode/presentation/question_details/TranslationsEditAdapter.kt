package com.rafalskrzypczyk.translations_mode.presentation.question_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.delete_bubble_manager.DeleteBubbleManager
import com.rafalskrzypczyk.translations_mode.databinding.CardTranslationEditBinding
import com.rafalskrzypczyk.translations_mode.presentation.question_details.ui_models.TranslationEditUIModel

class TranslationsEditAdapter(
    private val onTranslationChanged: (Int, String) -> Unit,
    private val onTranslationRemoved: (Int) -> Unit
) : ListAdapter<TranslationEditUIModel, TranslationsEditAdapter.EditViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditViewHolder {
        val binding = CardTranslationEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EditViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EditViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EditViewHolder(private val binding: CardTranslationEditBinding) : RecyclerView.ViewHolder(binding.root) {
        private val deleteBubbleManager = DeleteBubbleManager(binding.root.context)

        fun bind(item: TranslationEditUIModel) {
            with(binding) {
                inputTranslation.setText(item.text)
                
                inputTranslation.addTextChangedListener(afterTextChanged = {
                    if (inputTranslation.hasFocus()) {
                        onTranslationChanged(item.index, it.toString())
                    }
                })

                inputTranslation.setOnLongClickListener {
                    deleteBubbleManager.showDeleteBubble(it) {
                        onTranslationRemoved(item.index)
                    }
                    true
                }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<TranslationEditUIModel>() {
        override fun areItemsTheSame(oldItem: TranslationEditUIModel, newItem: TranslationEditUIModel) = oldItem.index == newItem.index
        override fun areContentsTheSame(oldItem: TranslationEditUIModel, newItem: TranslationEditUIModel) = oldItem.text == newItem.text
    }
}