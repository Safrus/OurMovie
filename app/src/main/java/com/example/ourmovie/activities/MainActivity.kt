package com.example.ourmovie.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.example.ourmovie.R
import com.example.ourmovie.RetrofitService
import com.example.ourmovie.responses.AccountResponse
import com.example.ourmovie.user.CurrentUser
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class MainActivity: AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref: SharedPreferences =  this.getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE)
        val gson = Gson()
        var json: String? = sharedPref.getString("currentUser",null)
        var type: Type = object : TypeToken<AccountResponse>() {}.type
        CurrentUser.user = gson.fromJson<AccountResponse>(json, type)


        if(CurrentUser.user !=null && CurrentUser.user!!.sessionId!=null){
            getAccount(CurrentUser.user!!.sessionId.toString())
        }
        else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        progressBar = findViewById(R.id.progressBar)
    }

    fun getAccount(session:String?){
        var accountResponse: AccountResponse?=null

        RetrofitService.getMovieApi()
            .getAccount(RetrofitService.getApiKey(),session!!).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.d("My_token_failure", t.toString())
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson = Gson()
                    if (response.isSuccessful) {
                        progressBar.visibility = View.GONE
                        accountResponse= gson.fromJson(response.body(), AccountResponse::class.java)
                        if(accountResponse!=null){
                            welcome(accountResponse!!,session)
                        }
                        else{
                            CurrentUser.user = null
                            login()
                        }
                    }
                }

            })
    }

    fun welcome(user: AccountResponse, session:String?){
        CurrentUser.user = user
        CurrentUser.user!!.sessionId  = session;
        val intent = Intent(this, MovieAppActivity::class.java)
        startActivity(intent)
    }

    fun login(){
        val intent = Intent(this, MovieAppActivity::class.java)
        startActivity(intent)
    }

}