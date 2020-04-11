package com.example.ourmovie.responses

import com.example.ourmovie.Movie
import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("page")
    val page:Int?=null,
    @SerializedName("results")
    val results: List<Movie>?=null,
    @SerializedName("session_id")
    val session_id: String?=null,
    @SerializedName("total_results")
    val totalResults : Int?=null,
    @SerializedName("total_pages")
    val totalPages: Int?=null
)