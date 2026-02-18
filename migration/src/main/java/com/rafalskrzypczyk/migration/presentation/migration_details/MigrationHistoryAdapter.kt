package com.rafalskrzypczyk.migration.presentation.migration_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.migration.R
import com.rafalskrzypczyk.migration.domain.models.MigrationRecord
import java.text.SimpleDateFormat
import java.util.Locale

class MigrationHistoryAdapter : ListAdapter<MigrationRecord, MigrationHistoryAdapter.ViewHolder>(
    GenericDiffCallback<MigrationRecord>(
        itemsTheSame = { old, new -> old.id == new.id },
        contentsTheSame = { old, new -> old == new }
    )
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val direction: TextView = view.findViewById(R.id.tv_direction)
        val date: TextView = view.findViewById(R.id.tv_date)
        val itemCount: TextView = view.findViewById(R.id.tv_item_count)
        val performedBy: TextView = view.findViewById(R.id.tv_performed_by)
        val detailsList: TextView = view.findViewById(R.id.tv_details_list)
        val expandedDetails: View = view.findViewById(R.id.expanded_details)

        fun bind(record: MigrationRecord) {
            direction.text = "${record.sourceCollection} -> ${record.targetCollection}"
            date.text = dateFormat.format(record.date)
            itemCount.text = "${itemView.context.getString(com.rafalskrzypczyk.core.R.string.text_items_count)} ${record.itemCount}"
            performedBy.text = "${itemView.context.getString(com.rafalskrzypczyk.core.R.string.text_performed_by)} ${record.performedBy}"
            detailsList.text = record.itemDetails.joinToString("\n") { "- $it" }

            itemView.setOnClickListener {
                expandedDetails.visibility = if (expandedDetails.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_migration_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
