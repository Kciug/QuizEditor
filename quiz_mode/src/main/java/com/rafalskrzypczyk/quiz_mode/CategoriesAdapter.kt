import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.quiz_mode.ListItemType
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.models.Category

class CategoriesAdapter(
    private val categories: List<Category>,
    private val onCategoryClicked: (Category) -> Unit,
    private val onAddClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    // ViewHolder dla pojedynczego elementu
    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.categoryName)
        val categoryDescription: TextView = view.findViewById(R.id.categoryDescription)
        val questionCount: TextView = view.findViewById(R.id.questionCount)
        val categoryStatus: TextView = view.findViewById(R.id.categoryStatus)

        fun bind(category: Category){
            categoryName.text = category.title
            categoryDescription.text = category.description
            questionCount.text = "Ilość pytań: ${category.questionAmount}"

            itemView.setOnClickListener{
                onCategoryClicked(category)
            }
        }
    }

    inner class AddButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val button: View = view.findViewById(R.id.button_add_new)

        fun bind(onClick: () -> Unit) {
            button.setOnClickListener { onClick() }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < categories.size) ListItemType.TYPE_ELEMENT.value else ListItemType.TYPE_ADD_BUTTON.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.card_category, parent, false)
//        return CategoryViewHolder(view)
        return if (viewType == ListItemType.TYPE_ELEMENT.value) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_category, parent, false)
            CategoryViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_add_new, parent, false)
            AddButtonViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CategoryViewHolder) {
            val category = categories[position]
            holder.bind(category)
        } else if (holder is AddButtonViewHolder){
            holder.bind(onAddClicked)
        }
    }

    override fun getItemCount(): Int = categories.size + 1
}
