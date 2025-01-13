import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.models.Category

class CategoriesAdapter(
    private val categories: List<Category>,
    private val onCategoryClicked: (Category) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
//        holder.categoryName.text = category.title
//        holder.categoryDescription.text = category.description
//        holder.questionCount.text = "Ilość pytań: ${category.questionAmount}"
        //holder.categoryStatus.text = category.status
        holder.bind(category)
    }

    override fun getItemCount(): Int = categories.size
}
