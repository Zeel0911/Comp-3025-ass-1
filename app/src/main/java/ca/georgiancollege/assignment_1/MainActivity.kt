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
            val intent = Intent(this, MovieDetailsActivity::class.java)
            intent.putExtra("imdbID", selectedMovie.imdbID)
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = movieAdapter
        }

        // Search button click listener
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
                val apiUrl = "https://www.omdbapi.com/?apikey=$apiKey&s=${query.replace(" ", "+")}"
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("HTTP error code: $responseCode")
                }

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)

                if (json.has("Search")) {
                    val searchArray = json.getJSONArray("Search")
                    val movies = mutableListOf<Movie>()

                    for (i in 0 until searchArray.length()) {
                        val obj = searchArray.getJSONObject(i)
                        val movie = Movie(
                            title = obj.optString("title", "N/A"),
                            year = obj.optString("year", "N/A"),
                            imdbID = obj.optString("imdbID", ""),
                            type = obj.optString("type", ""),
                            poster = obj.optString("poster", "")
                        )
                        movies.add(movie)
                    }

                    runOnUiThread {
                        movieList.clear()
                        movieList.addAll(movies)
                        movieAdapter.notifyDataSetChanged()
                    }
                } else {
                    val errorMsg = json.optString("Error", "No results found")
                    runOnUiThread {
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                        movieList.clear()
                        movieAdapter.notifyDataSetChanged()
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("OMDB_ERROR", "Failed to fetch movies", e)
                runOnUiThread {
                    Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
