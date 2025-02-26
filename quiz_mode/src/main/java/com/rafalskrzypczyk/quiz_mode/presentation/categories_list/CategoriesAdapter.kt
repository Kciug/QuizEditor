package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.presentation.custom_views.StatusIndicatorView
import com.rafalskrzypczyk.quiz_mode.utils.ListItemType
import com.rafalskrzypczyk.quiz_mode.utils.getColor
import com.rafalskrzypczyk.quiz_mode.utils.getTitle

class CategoriesAdapter(
    private val onCategoryClicked: (Category, Int) -> Unit,
    private val onAddClicked: () -> Unit
) : ListAdapter<Category, RecyclerView.ViewHolder>(GenericDiffCallback<Category>(
    itemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
    contentsTheSame = { oldItem, newItem ->
        oldItem == newItem
    }
)) {
    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.categoryName)
        val categoryDescription: TextView = view.findViewById(R.id.categoryDescription)
        val questionCount: TextView = view.findViewById(R.id.questionCount)
        val statusIndicator: StatusIndicatorView = view.findViewById(R.id.statusIndicator)
        val iconCategoryColor: ImageView = view.findViewById(R.id.icon_category_color)

        fun bind(category: Category, position: Int) {
            categoryName.text = category.title
            categoryDescription.text = category.description
            questionCount.text = String.format(category.linkedQuestions.count().toString())
            statusIndicator.setColorAndText(category.status.getColor(itemView.context), category.status.getTitle(itemView.context))
            iconCategoryColor.setColorFilter(category.color.toInt())

            itemView.setOnClickListener {
                onCategoryClicked(category, position)
            }
        }
    }

    // ViewHolder dla przycisku dodawania nowej kategorii
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
            holder.bind(category, position)
        } else if (holder is AddButtonViewHolder) {
            holder.bind()
        }
    }

    override fun getItemCount(): Int = currentList.size + 1

    override fun submitList(list: List<Category>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}
