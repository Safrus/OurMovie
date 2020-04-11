package com.example.ourmovie.responses

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val isSuccess:Boolean?=null,
    @SerializedName("expires_at")
    val expiredAt:String?=null,
    @SerializedName("request_token")
    val request_token:String?=null
)