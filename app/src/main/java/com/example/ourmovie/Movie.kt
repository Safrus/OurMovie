package com.example.ourmovie.Responses

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id")
    val movieId:Int?=null,
    @SerializedName("title")
    val title: String?=null,
    @SerializedName("overview")
    val overview : String?=null,
    @SerializedName("vote_average")
    val rating: Float?=null,
    @SerializedName("poster_path")
    val posterPath : String?=null,
    @SerializedName("runtime")
    val runtime : Float?=null,
    @SerializedName("favorite")
    val favorite : Boolean?=null
)
