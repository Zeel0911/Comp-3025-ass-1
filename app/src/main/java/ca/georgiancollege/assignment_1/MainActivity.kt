package ca.georgiancollege.assignment_1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ca.georgiancollege.assignment_1.databinding.ActivityMainBinding
import ca.georgiancollege.assignment_1.model.Movie
import ca.georgiancollege.assignment_1.model.MovieAdapter
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var movieAdapter: MovieAdapter
    private val movieList = mutableListOf<Movie>()

    private val apiKey = "96ce8e15"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        movieAdapter = MovieAdapter(movieList) { selectedMovie ->
            // Go to detail screen
            val intent = Intent(this, MovieDetailsActivity::class.java)
            intent.putExtra("imdbID", selectedMovie.imdbID)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = movieAdapter

        // Search button click
        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                fetchMovies(query)
            } else {
                Toast.makeText(this, "Enter a movie title", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchMovies(query: String) {
        thread {
            try {
                val url =
                    URL("https://www.omdbapi.com/?apikey=$apiKey&s=${query.replace(" ", "+")}")
                val connection = url.openConnection() as HttpURLConnection
                val response = connection.inputStream.bufferedReader().use { it.readText() }

                val json = JSONObject(response)

                if (json.has("Search")) {
                    val searchArray = json.getJSONArray("Search")
                    val movies = mutableListOf<Movie>()
                    for (i in 0 until searchArray.length()) {
                        val obj = searchArray.getJSONObject(i)
                        val movie = Movie(
                            Title = obj.getString("Title"),
                            Year = obj.getString("Year"),
                            imdbID = obj.getString("imdbID"),
                            Type = obj.getString("Type"),
                            Poster = obj.getString("Poster")
                        )
                        movies.add(movie)
                    }

                    runOnUiThread {
                        movieList.clear()
                        movieList.addAll(movies)
                        movieAdapter.notifyDataSetChanged()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("OMDB_ERROR", "Failed to fetch movies", e)
                runOnUiThread {
                    Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
