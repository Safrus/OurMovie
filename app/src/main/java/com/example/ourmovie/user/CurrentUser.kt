package com.example.ourmovie.user

import com.example.ourmovie.Movie
import com.example.ourmovie.responses.AccountResponse

class CurrentUser {
    companion object {
        var user: AccountResponse? = null
        var favoritList: List<Movie>? = null
    }
}
