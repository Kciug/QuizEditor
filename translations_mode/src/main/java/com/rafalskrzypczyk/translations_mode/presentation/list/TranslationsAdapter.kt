package com.rafalskrzypczyk.translations_mode.presentation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.delete_bubble_manager.DeleteBubbleManager
import com.rafalskrzypczyk.translations_mode.databinding.CardTranslationQuestionBinding
import com.rafalskrzypczyk.translations_mode.presentation.list.ui_models.TranslationQuestionUIModel

class TranslationsAdapter(
    private val onQuestionClicked: (Long) -> Unit,
    private val onQuestionRemoved: (Long) -> Unit
) : ListAdapter<TranslationQuestionUIModel, TranslationsAdapter.TranslationViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TranslationViewHolder {
        val binding = CardTranslationQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TranslationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TranslationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TranslationViewHolder(private val binding: CardTranslationQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        private val deleteBubbleManager = DeleteBubbleManager(binding.root.context)

        fun bind(item: TranslationQuestionUIModel) {
            with(binding) {
                tvPhrase.text = item.phrase
                tvTranslationsCount.text = item.translationsText
                root.setOnClickListener { onQuestionClicked(item.id) }
                root.setOnLongClickListener {
                    deleteBubbleManager.showDeleteBubble(it) {
                        onQuestionRemoved(item.id)
                    }
                    true
                }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<TranslationQuestionUIModel>() {
        override fun areItemsTheSame(oldItem: TranslationQuestionUIModel, newItem: TranslationQuestionUIModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TranslationQuestionUIModel, newItem: TranslationQuestionUIModel) = oldItem == newItem
    }
}
