package com.example.ourmovie.responses

import com.google.gson.annotations.SerializedName

data class FavoriteResponse(
    @SerializedName("status_code")
    val status_code:Int?=null,
    @SerializedName("status_message")
    val status_message:String?=null
)
