package com.rafalskrzypczyk.quiz_mode.ui.categories_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.quiz_mode.utils.ListItemType
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.ui.custom_views.StatusIndicatorView
import com.rafalskrzypczyk.quiz_mode.utils.getColor
import com.rafalskrzypczyk.quiz_mode.utils.getTitle
import com.rafalskrzypczyk.quiz_mode.models.Category

//class CategoriesAdapter(
//    private var categories: List<Category>,
//    private val onCategoryClicked: (Category) -> Unit,
//    private val onAddClicked: () -> Unit
//) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    // ViewHolder dla pojedynczego elementu
//    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val categoryName: TextView = view.findViewById(R.id.categoryName)
//        val categoryDescription: TextView = view.findViewById(R.id.categoryDescription)
//        val questionCount: TextView = view.findViewById(R.id.questionCount)
//
//        fun bind(category: Category){
//            categoryName.text = category.title
//            categoryDescription.text = category.description
//            questionCount.text = "Ilość pytań: ${category.questionAmount}"
//
//            itemView.setOnClickListener{
//                onCategoryClicked(category)
//            }
//        }
//    }
//
//    inner class AddButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        private val button: View = view.findViewById(R.id.button_add_new)
//
//        fun bind(onClick: () -> Unit) {
//            button.setOnClickListener { onClick() }
//        }
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return if (position < categories.size) ListItemType.TYPE_ELEMENT.value else ListItemType.TYPE_ADD_BUTTON.value
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return if (viewType == ListItemType.TYPE_ELEMENT.value) {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.card_category, parent, false)
//            CategoryViewHolder(view)
//        } else {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.card_add_new, parent, false)
//            AddButtonViewHolder(view)
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (holder is CategoryViewHolder) {
//            val category = categories[position]
//            holder.bind(category)
//        } else if (holder is AddButtonViewHolder){
//            holder.bind(onAddClicked)
//        }
//    }
//
//    fun updateData(newCategories: List<Category>) {
//        this.categories = newCategories
//    }
//
//    override fun getItemCount(): Int = categories.size + 1
//}

class CategoriesAdapter(
    private val onCategoryClicked: (Category, Int) -> Unit,
    private val onAddClicked: () -> Unit
) : ListAdapter<Category, RecyclerView.ViewHolder>(GenericDiffCallback<Category>(
    areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
    areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
)) {
    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.categoryName)
        val categoryDescription: TextView = view.findViewById(R.id.categoryDescription)
        val questionCount: TextView = view.findViewById(R.id.questionCount)
        val statusIndicator: StatusIndicatorView = view.findViewById(R.id.statusIndicator)

        fun bind(category: Category, position: Int) {
            categoryName.text = category.title
            categoryDescription.text = category.description
            questionCount.text = String.format(category.questionAmount.toString())
            statusIndicator.setColorAndText(category.status.getColor(itemView.context), category.status.getTitle(itemView.context))

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
}
