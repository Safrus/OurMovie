package com.example.ourmovie.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmovie.Movie
import com.example.ourmovie.R

class MovieAdapter(
    var list: List<Movie>? = null,
    val itemClickListener: RecyclerViewItemClick? = null
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var context: Context? = null

    val baseImageUrl: String = "https://image.tmdb.org/t/p/w500"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        this.context = parent.context

        return MovieViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_movie,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = list!!.get(position)

        if (movie.posterPath != null) {
            Glide.with(context!!)
                .load(baseImageUrl + movie.posterPath)
                .into(holder.ivMoviePoster)
        }

        if (movie.title != null) {
            holder.tvTitle.text = movie.title
        }

        if (movie.overview != null) {
            holder.tvOverview.text = movie.overview
        }

        holder.itemView.setOnClickListener {
            itemClickListener?.itemClick(movie.movieId!!, movie!!)
        }

    }

    fun clearAll() {
        (list as? ArrayList<Movie>)?.clear()
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivMoviePoster: ImageView = itemView.findViewById(R.id.movie_poster)
        val tvTitle: TextView = itemView.findViewById(R.id.movie_title)
        val tvOverview: TextView = itemView.findViewById(R.id.movie_overview)
    }

    interface RecyclerViewItemClick {
        fun itemClick(position: Int, item: Movie)
    }

    override fun getItemCount(): Int = list?.size ?: 0
}
