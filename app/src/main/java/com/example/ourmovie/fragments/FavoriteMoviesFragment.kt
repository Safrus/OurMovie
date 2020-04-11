package com.example.ourmovie.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ourmovie.Movie
import com.example.ourmovie.responses.MovieResponse
import com.example.ourmovie.R
import com.example.ourmovie.RetrofitService
import com.example.ourmovie.activities.MovieDetailActivity
import com.example.ourmovie.adapters.FavoriteMovieAdapter
import com.example.ourmovie.user.CurrentUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteMoviesFragment: Fragment(),
    FavoriteMovieAdapter.RecyclerViewItemClick {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var favoriteMovieAdapter: FavoriteMovieAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater!!.inflate(R.layout.fav_movies_fragment, container, false)
        recyclerView = view.findViewById(R.id.favoriteRecyclerView)

        recyclerView.layoutManager =
            LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            favoriteMovieAdapter?.clearAll()
            getFavoriteMovies()
        }

        favoriteMovieAdapter =
            FavoriteMovieAdapter(
                itemClickListener = this
            )
        recyclerView.adapter = favoriteMovieAdapter

        getFavoriteMovies()

        return view
    }

    private fun getFavoriteMovies() {
        swipeRefreshLayout.isRefreshing = true
        RetrofitService.getMovieApi().getFavoriteMovieList(
            CurrentUser.user!!.account_id,
            RetrofitService.getApiKey(), CurrentUser.user!!.sessionId.toString()).enqueue(object :
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
                    if (list != null) {
                        CurrentUser.favoritList = list
                    }
                    favoriteMovieAdapter?.list = list
                    favoriteMovieAdapter?.notifyDataSetChanged()
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