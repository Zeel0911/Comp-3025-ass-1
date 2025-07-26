package ca.georgiancollege.assignment_1.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.georgiancollege.assignment_1.R

class MovieAdapter(
    private val movies: List<Movie>,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val studioTextView: TextView = itemView.findViewById(R.id.studioTextView)
        val ratingTextView: TextView = itemView.findViewById(R.id.ratingTextView)
        val yearTextView: TextView = itemView.findViewById(R.id.yearTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(movies[position])
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount() = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.titleTextView.text = movie.title
        holder.studioTextView.text = "Studio: N/A"
        holder.ratingTextView.text = "Rating: N/A"
        holder.yearTextView.text = "Year: ${movie.year}"
    }
}