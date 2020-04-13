package com.example.ourmovie.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.example.ourmovie.Movie
import com.example.ourmovie.R
import com.example.ourmovie.RetrofitService
import com.example.ourmovie.responses.FavoriteResponse
import com.example.ourmovie.user.CurrentUser
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class MovieDetailActivity: AppCompatActivity(), CoroutineScope {

    private lateinit var progressBar: ProgressBar
    private lateinit var tvTitle: TextView
    private lateinit var ivDivider: ImageView
    private lateinit var ivMoviePoster: ImageView
    private lateinit var tvRating: TextView
    private lateinit var tvRatingText: TextView
    private lateinit var tvRuntime: TextView
    private lateinit var tvRuntimeText: TextView
    private lateinit var tvOverviewText: TextView
    private lateinit var ivLike: ImageView
    private var isFavorite = false

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val baseImageUrl: String = "https://image.tmdb.org/t/p/w500"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        progressBar = findViewById(R.id.progressBar)
        tvTitle = findViewById(R.id.movie_title)
        ivDivider = findViewById(R.id.divider)
        ivMoviePoster = findViewById(R.id.movie_poster)
        tvRating = findViewById(R.id.tvRating)
        tvRatingText = findViewById(R.id.movie_rating)
        tvRuntime = findViewById(R.id.tvRuntime)
        tvRuntimeText = findViewById(R.id.movie_runtime)
        tvOverviewText = findViewById(R.id.movie_overview)
        ivLike = findViewById(R.id.like)

        val movieId = intent.getIntExtra("movie_id", 1)

        //getMovie(id = movieId)

        getMovieCoroutine(id = movieId)

        val goBack: ImageButton = findViewById(R.id.back)

        goBack.setOnClickListener {finish()}
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    /*
    private fun getMovie(id: Int) {
        RetrofitService.getMovieApi()
            .getMovieById(id, RetrofitService.getApiKey()).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressBar.visibility = View.GONE
                }
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson = Gson()
                    Log.d("My_movie", response.body().toString())
                    if (response.isSuccessful) {
                        progressBar.visibility = View.GONE
                        val movie: Movie = gson.fromJson(response.body(), Movie::class.java)
                        if (movie != null) {
                            tvTitle.text = movie.title
                            tvRating.text = "Rating"
                            tvRatingText.text = movie.rating.toString() + " / 10"
                            tvRuntime.text = "Runtime"
                            tvRuntimeText.text = movie.runtime.toString() + " minutes"
                            tvOverviewText.text = movie.overview

                            for (fm in CurrentUser.favoritList!!) {
                                if (movie.title.equals(fm.title)) {
                                    isFavorite = true
                                }
                            }

                            if (isFavorite) {
                                Glide.with(this@MovieDetailActivity)
                                    .load(R.drawable.redlike)
                                    .into(ivLike)
                            } else {
                                Glide.with(this@MovieDetailActivity)
                                    .load(R.drawable.like)
                                    .into(ivLike)
                            }

                            if (movie.posterPath != null) {
                                Glide.with(this@MovieDetailActivity)
                                    .load(baseImageUrl + movie.posterPath)
                                    .into(ivMoviePoster)
                            }

                            ivLike.setOnClickListener{
                                if (isFavorite) {
                                    val body = JsonObject().apply {
                                        addProperty("media_type", "movie")
                                        addProperty("media_id", movie.movieId)
                                        addProperty("favorite", false)
                                    }

                                    Glide.with(this@MovieDetailActivity)
                                        .load(R.drawable.like)
                                        .into(ivLike)

                                    markFavorite(body)
                                } else {
                                    val body = JsonObject().apply {
                                        addProperty("media_type", "movie")
                                        addProperty("media_id", movie.movieId)
                                        addProperty("favorite", true)
                                    }

                                    Glide.with(this@MovieDetailActivity)
                                        .load(R.drawable.redlike)
                                        .into(ivLike)

                                    markFavorite(body)
                                }
                            }
                        }
                    }
                }
            })
    }
     */

    private fun getMovieCoroutine(id: Int) {
        launch {
            val response = RetrofitService.getMovieApi().getMovieByIdCoroutine(id, RetrofitService.getApiKey())
            if (response.isSuccessful) {
                progressBar.visibility = View.GONE
                var gson = Gson()
                Log.d("My_movie", response.body().toString())
                val movie: Movie = gson.fromJson(response.body(), Movie::class.java)
                if (movie != null) {
                    tvTitle.text = movie.title
                    tvRating.text = "Rating"
                    tvRatingText.text = movie.rating.toString() + " / 10"
                    tvRuntime.text = "Runtime"
                    tvRuntimeText.text = movie.runtime.toString() + " minutes"
                    tvOverviewText.text = movie.overview

                    for (fm in CurrentUser.favoritList!!) {
                        if (movie.title.equals(fm.title)) {
                            isFavorite = true
                        }
                    }

                    if (isFavorite) {
                        Glide.with(this@MovieDetailActivity)
                            .load(R.drawable.redlike)
                            .into(ivLike)
                    } else {
                        Glide.with(this@MovieDetailActivity)
                            .load(R.drawable.like)
                            .into(ivLike)
                    }

                    if (movie.posterPath != null) {
                        Glide.with(this@MovieDetailActivity)
                            .load(baseImageUrl + movie.posterPath)
                            .into(ivMoviePoster)
                    }

                    ivLike.setOnClickListener {
                        if (isFavorite) {
                            val body = JsonObject().apply {
                                addProperty("media_type", "movie")
                                addProperty("media_id", movie.movieId)
                                addProperty("favorite", false)
                            }

                            Glide.with(this@MovieDetailActivity)
                                .load(R.drawable.like)
                                .into(ivLike)

                            markFavoriteCoroutine(body)
                        } else {
                            val body = JsonObject().apply {
                                addProperty("media_type", "movie")
                                addProperty("media_id", movie.movieId)
                                addProperty("favorite", true)
                            }

                            Glide.with(this@MovieDetailActivity)
                                .load(R.drawable.redlike)
                                .into(ivLike)

                            markFavoriteCoroutine(body)
                        }

                    }
                }
            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(this@MovieDetailActivity, "Error", Toast.LENGTH_SHORT)
            }
        }
    }

    /*
   private fun markFavorite(body: JsonObject) {
        var favResponse: FavoriteResponse?
        RetrofitService.getMovieApi().markAsFavorite(
            CurrentUser.user!!.accountId,
            RetrofitService.getApiKey(), CurrentUser.user!!.sessionId.toString(), body).enqueue(object :
            Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var gson = Gson()
                favResponse = gson.fromJson(response.body(), FavoriteResponse::class.java)
                if (favResponse != null) {
                    notify(favResponse!!)
                }
            }
        })
    }
     */

    private fun markFavoriteCoroutine(body: JsonObject) {
        launch {
            var favResponse: FavoriteResponse?
            val response = RetrofitService.getMovieApi().markAsFavoriteCoroutine(CurrentUser.user!!.accountId, RetrofitService.getApiKey(), CurrentUser.user!!.sessionId.toString(), body)
            if (response.isSuccessful) {
                var gson = Gson()
                favResponse = gson.fromJson(response.body(), FavoriteResponse::class.java)
                if (favResponse != null) {
                    notify(favResponse!!)
                }
            } else {
                Toast.makeText(this@MovieDetailActivity, "Error", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun notify(favResponse: FavoriteResponse) {
        val status_code = favResponse!!.statusCode
        if (status_code == 0) {
            Toast.makeText(this, favResponse.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}