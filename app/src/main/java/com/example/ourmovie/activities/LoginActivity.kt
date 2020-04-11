package com.example.ourmovie.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.ourmovie.R
import com.example.ourmovie.RetrofitService
import com.example.ourmovie.Token
import com.example.ourmovie.responses.AccountResponse
import com.example.ourmovie.responses.LoginResponse
import com.example.ourmovie.responses.SessionResponse
import com.example.ourmovie.user.CurrentUser
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class LoginActivity : AppCompatActivity() {

    lateinit var loginBtn: Button
    lateinit var login: EditText
    lateinit var password: EditText
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE
        login = findViewById(R.id.login)
        password = findViewById(R.id.password)
        loginBtn = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener(){

            val userLogin:String = login.text.toString().trim()
            val userPassword:String = password.text.toString().trim()
            login(userLogin, userPassword)

        }
    }

    fun login(username: String, password: String){
        var requestTokenResponse: Token?
        progressBar.visibility = View.VISIBLE
        RetrofitService.getMovieApi()
            .getNewToken(RetrofitService.getApiKey()).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("My_token_failure", t.toString())
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson = Gson()
                    Log.d("My_token_response", response.body().toString())
                    if (response.isSuccessful) {
                        val type: Type = object : TypeToken<Token>() {}.type
                        requestTokenResponse= gson.fromJson(response.body(), Token::class.java)

                        if(requestTokenResponse!=null){
                            val request_token: String? = requestTokenResponse!!.request_token
                            val body = JsonObject().apply{
                                addProperty("username",username)
                                addProperty("password",password)
                                addProperty("request_token",request_token)
                            }
                            getLoginResponse(body)

                        }
                    }
                }



            })

    }

    fun getLoginResponse(body: JsonObject){
        var logRseponse: LoginResponse?
        RetrofitService.getMovieApi()
            .login(RetrofitService.getApiKey(),body).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson = Gson()
                    if(response.isSuccessful){
                        logRseponse= gson.fromJson(response.body(), LoginResponse::class.java)

                        if (logRseponse!=null){
                            val body = JsonObject().apply{
                                addProperty("request_token",logRseponse!!.request_token.toString())
                            }
                            getSession(body)
                        }
                    }
                    else{
                        val error = "Incorrect login or password!"
                        showError(error)
                    }

                }
            })
    }

    fun showError(error:String){
        progressBar.visibility = View.INVISIBLE
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    fun getSession(body: JsonObject){
        var session: SessionResponse?
        RetrofitService.getMovieApi()
            .getSession(RetrofitService.getApiKey(),body).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson = Gson()
                    if(response.isSuccessful) {
                        val type: Type = object : TypeToken<SessionResponse>() {}.type
                        session = gson.fromJson(response.body(), SessionResponse::class.java)
                        if (session != null) {
                            val session_id = session!!.session_id
                            getAccount(session_id)
                        }
                    }
                }
            })
    }

    fun getAccount(session:String?){
        Toast.makeText(this, "Loading......", Toast.LENGTH_SHORT).show()

        var accountResponse: AccountResponse?

        RetrofitService.getMovieApi()
            .getAccount(RetrofitService.getApiKey(),session!!).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("My_token_failure", t.toString())
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson = Gson()
                    if (response.isSuccessful) {
                        accountResponse= gson.fromJson(response.body(), AccountResponse::class.java)
                        if(accountResponse!=null){
                            showWelcome(accountResponse!!,session)
                        }
                    }
                }

            })

    }

    fun showWelcome(user: AccountResponse, session:String?){
        progressBar.visibility = View.GONE
        CurrentUser.user = user
        CurrentUser.user!!.sessionId  = session;
        saveSessionOftheCurrentUser()
        val intent = Intent(this, MovieAppActivity::class.java)
        Toast.makeText(this, "Glad to see you, " + CurrentUser.user!!.userName, Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }

    fun saveSessionOftheCurrentUser(){
        val currUserSharedPreference: SharedPreferences =  this!!.getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE)
        var currUserEditor = currUserSharedPreference.edit()
        val gson = Gson()
        val json:String = gson!!.toJson(CurrentUser.user)
        currUserEditor.putString("currentUser",json)
        currUserEditor.commit()
    }
}
