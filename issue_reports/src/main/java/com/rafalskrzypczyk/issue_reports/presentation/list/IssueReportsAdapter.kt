package com.rafalskrzypczyk.issue_reports.presentation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.domain.models.GameMode
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.issue_reports.databinding.ItemIssueReportBinding
import com.rafalskrzypczyk.issue_reports.presentation.list.ui_models.IssueReportUIModel

class IssueReportsAdapter(
    private val onItemClicked: (IssueReportUIModel) -> Unit
) : ListAdapter<IssueReportUIModel, IssueReportsAdapter.ViewHolder>(
    GenericDiffCallback(
        itemsTheSame = { oldItem: IssueReportUIModel, newItem: IssueReportUIModel -> oldItem.id == newItem.id },
        contentsTheSame = { oldItem: IssueReportUIModel, newItem: IssueReportUIModel -> oldItem == newItem }
    )
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIssueReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemIssueReportBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: IssueReportUIModel) {
            with(binding) {
                textDescription.text = item.description
                textQuestionContent.text = item.questionContent
                textDate.text = item.dateString
                
                val result: Pair<Int, String> = when (item.gameMode) {
                    GameMode.QUIZ -> com.rafalskrzypczyk.core.R.color.green to "QUIZ"
                    GameMode.SWIPE -> com.rafalskrzypczyk.core.R.color.primary to "SWIPE"
                    GameMode.TRANSLATION -> com.rafalskrzypczyk.core.R.color.orange_primary to "TRANSLATIONS"
                    GameMode.CEM -> com.rafalskrzypczyk.core.R.color.purple_500 to "CEM"
                }
                
                labelGameMode.setColorAndText(root.context.getColor(result.first), result.second)
                
                root.setOnClickListener { onItemClicked(item) }
            }
        }
    }
}
