package ca.georgiancollege.assignment_1.model

import android.view.View
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
    }