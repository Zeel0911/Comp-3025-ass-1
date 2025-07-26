package ca.georgiancollege.assignment_1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.georgiancollege.assignment_1.databinding.ActivityMovieDetailsBinding
import com.bumptech.glide.Glide
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

        val imdbId = intent.getStringExtra("imdbID")

        if (imdbId != null && imdbId.isNotEmpty()) {
            getMovieDetails(imdbId)
        } else {
            Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getMovieDetails(imdbId: String) {
        thread {
            try {
                val apiUrl = "https://www.omdbapi.com/?apikey=$apiKey&i=$imdbId"
                val connection = URL(apiUrl).openConnection() as HttpURLConnection
                val result = connection.inputStream.bufferedReader().readText()
                connection.disconnect()

                val jsonObject = JSONObject(result)
                val success = jsonObject.optString("Response") == "True"

                if (success) {
                    val title = jsonObject.optString("Title", "N/A")
                    val year = jsonObject.optString("Year", "N/A")
                    val plot = jsonObject.optString("Plot", "N/A")
                    val poster = jsonObject.optString("Poster", "")

                    runOnUiThread {
                        binding.titleTextView.text = title
                        binding.yearTextView.text = year
                        binding.plotTextView.text = plot

                        Glide.with(this)
                            .load(poster)
                            .into(binding.posterImageView)
                    }
                } else {
                    val error = jsonObject.optString("Error", "Movie not found")
                    runOnUiThread {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
