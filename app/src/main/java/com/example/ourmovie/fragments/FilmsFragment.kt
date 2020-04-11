package com.example.ourmovie.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.movieapp.Responses.Movie
import com.example.movieapp.Responses.MovieResponse
import com.example.ourmovie.R
import com.example.ourmovie.RetrofitService
import com.example.ourmovie.activities.MovieDetailActivity
import com.example.ourmovie.adapters.MovieAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilmsFragment: Fragment() , MovieAdapter.RecyclerViewItemClick{

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var movieAdapter: MovieAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val logo = getView()!!.findViewById<ImageView>(R.id.logo)
        val option = getView()!!.findViewById<ImageView>(R.id.option)

        Glide.with(logo.context)
            .load(R.drawable.logokino)
            .into(logo)
        Glide.with(option.context)
            .load(R.drawable.option)
            .into(option)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view: View = inflater!!.inflate(R.layout.films_fragment,container,false)
        recyclerView = view.findViewById(R.id.favoriteRecyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            movieAdapter?.clearAll()
            getMovies()
        }

        movieAdapter =
            MovieAdapter(
                itemClickListener = this
            )
        recyclerView.adapter = movieAdapter

        getMovies()

        return view
    }

    private fun getMovies() {
        swipeRefreshLayout.isRefreshing = true
        RetrofitService.getMovieApi().getMovieList(
            RetrofitService.getApiKey()
        ).enqueue(object :
            Callback<MovieResponse> {

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>
            ) {
                Log.d("My_movie_list", response.body().toString())
                if (response.isSuccessful) {
                    val list: List<Movie>? = response.body()?.results
                    movieAdapter?.list = list
                    movieAdapter?.notifyDataSetChanged()
                }
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    override fun itemClick(position: Int, item: Movie) {
        val intent = Intent(this.activity, MovieDetailActivity::class.java)
        intent.putExtra("movie_id", item.movieId)
        startActivity(intent)
    }

}