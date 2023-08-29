import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var carItemClickListener: ((Repository) -> Unit)? = null
    var btnShareClickListener: ((Repository) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = repositories[position]
        holder.bind(repository)
    }

    override fun getItemCount(): Int {
        return repositories.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var repositoryNameCardView: TextView = view.findViewById(R.id.tv_preco)
        private val shareButton: ImageView = view.findViewById(R.id.iv_favorite)

        fun bind(repository: Repository) {
            // Set the text of the TextView to the repository name
            repositoryNameCardView.text = repository.name

            // Exemplo de click no item
            itemView.setOnClickListener {
                carItemClickListener?.invoke(repository)
            }

            // Exemplo de click no botão de compartilhamento
            shareButton.setOnClickListener {
                btnShareClickListener?.invoke(repository)

            }
        }

    }
}
