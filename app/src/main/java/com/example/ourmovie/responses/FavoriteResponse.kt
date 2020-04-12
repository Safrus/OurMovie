package com.example.ourmovie.responses

import com.google.gson.annotations.SerializedName

data class FavoriteResponse(
    @SerializedName("status_code") val statusCode: Int? = null,
    @SerializedName("status_message") val statusMessage: String? = null
)
