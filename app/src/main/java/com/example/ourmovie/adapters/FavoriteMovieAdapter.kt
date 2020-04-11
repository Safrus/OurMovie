package com.example.ourmovie.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.Responses.Movie
import com.example.ourmovie.R


class FavoriteMovieAdapter(
    var list: List<Movie>? = null,
    val itemClickListener: RecyclerViewItemClick? = null

) : RecyclerView.Adapter<FavoriteMovieAdapter.FavoriteMovieViewHolder>() {

    private var mContext: Context? = null

    val baseImageUrl:String = "https://image.tmdb.org/t/p/w500"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteMovieViewHolder {
        this.mContext = parent.context

        return FavoriteMovieViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_favorite_movie,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoriteMovieViewHolder, position: Int) {
        val movie = list!!.get(position)

        if(movie.posterPath != null){
            Glide.with(mContext!!)
                .load(baseImageUrl + movie.posterPath)
                .into(holder.ivMoviePoster)
        }

        if(movie.title != null){
            holder.tvTitle.setText(movie.title)
        }

        if(movie.overview != null){
            holder.tvOverview.setText(movie.overview)
        }

        holder.itemView.setOnClickListener{
            itemClickListener?.itemClick(movie.movieId!!, movie!!)
        }

    }

    fun clearAll() {
        (list as? ArrayList<Movie>)?.clear()
        notifyDataSetChanged()
    }


    inner class FavoriteMovieViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val ivMoviePoster: ImageView = itemView.findViewById(R.id.movie_poster)
        val tvTitle: TextView = itemView.findViewById(R.id.movie_title)
        val tvOverview: TextView = itemView.findViewById(R.id.movie_overview)
    }

    interface RecyclerViewItemClick {

        fun itemClick(position: Int, item: Movie)
    }

    override fun getItemCount(): Int = list?.size ?: 0
}
