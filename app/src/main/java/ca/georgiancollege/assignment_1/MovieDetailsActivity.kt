package ca.georgiancollege.assignment_1

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.georgiancollege.assignment_1.databinding.ActivityMovieDetailsBinding
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val apiKey = "96ce8e15"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imdbID = intent.getStringExtra("imdbID")
        if (imdbID != null) {
            fetchMovieDetails(imdbID)
        } else {
            Toast.makeText(this, "Movie ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchMovieDetails(imdbID: String) {
        thread {
            try {
                val url = URL("https://www.omdbapi.com/?apikey=$apiKey&i=$imdbID")
                val connection = url.openConnection() as HttpURLConnection
                val response = connection.inputStream.bufferedReader().use { it.readText() }

                val json = JSONObject(response)
                val title = json.optString("Title", "N/A")
                val year = json.optString("Year", "N/A")
                val plot = json.optString("Plot", "N/A")
                val posterUrl = json.optString("Poster", "")

                runOnUiThread {
                    binding.titleTextView.text = title
                    binding.yearTextView.text = year
                    binding.plotTextView.text = plot
                    // You can use libraries like Glide or Picasso to load the poster image
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("DETAILS_ERROR", "Error loading movie details", e)
                runOnUiThread {
                    Toast.makeText(this, "Error loading details", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
