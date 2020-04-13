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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class LoginActivity: AppCompatActivity(), CoroutineScope {

    lateinit var loginBtn: Button
    lateinit var login: EditText
    lateinit var password: EditText
    lateinit var progressBar: ProgressBar

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE
        login = findViewById(R.id.login)
        password = findViewById(R.id.password)
        loginBtn = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener{
            val userLogin: String = login.text.toString().trim()
            val userPassword: String = password.text.toString().trim()
            loginCoroutine(userLogin, userPassword)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    /*
    private fun login(username: String, password: String) {
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
                        requestTokenResponse = gson.fromJson(response.body(), Token::class.java)
                        if (requestTokenResponse != null) {
                            val requestToken: String? = requestTokenResponse!!.requestToken
                            val body = JsonObject().apply {
                                addProperty("username", username)
                                addProperty("password", password)
                                addProperty("request_token", requestToken)
                            }
                            getLoginResponse(body)
                        }
                    }
                }
            })
    }
     */

    private fun loginCoroutine(username: String, password: String) {
        launch {
            var requestTokenResponse: Token?
            progressBar.visibility = View.VISIBLE
            val response = RetrofitService.getMovieApi().getNewTokenCoroutine(RetrofitService.getApiKey())
            if (response.isSuccessful) {
                Log.d("My_token_response", response.body().toString())
                var gson = Gson()
                requestTokenResponse = gson.fromJson(response.body(), Token::class.java)
                if (requestTokenResponse != null) {
                    val requestToken: String? = requestTokenResponse!!.requestToken
                    val body = JsonObject().apply {
                        addProperty("username", username)
                        addProperty("password", password)
                        addProperty("request_token", requestToken)
                    }
                    getLoginResponseCoroutine(body)
                }
            } else {
                Log.d("My_token_failure", response.body().toString())
                Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT)
            }
        }
    }

    /*
    private fun getLoginResponse(body: JsonObject) {
        var loginResponse: LoginResponse?
        RetrofitService.getMovieApi()
            .login(RetrofitService.getApiKey(), body).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson = Gson()
                    if (response.isSuccessful) {
                        loginResponse = gson.fromJson(response.body(), LoginResponse::class.java)
                        if (loginResponse != null) {
                            val body = JsonObject().apply {
                                addProperty("request_token", loginResponse!!.requestToken.toString())
                            }
                            getSession(body)
                        }
                    } else {
                        val error = "Incorrect login or password!"
                        showError(error)
                    }

                }
            })
    }
     */

    private fun getLoginResponseCoroutine(body: JsonObject) {
        launch {
            var loginResponse: LoginResponse?
            val response = RetrofitService.getMovieApi().loginCoroutine(RetrofitService.getApiKey(), body)
            if (response.isSuccessful) {
                var gson = Gson()
                loginResponse = gson.fromJson(response.body(), LoginResponse::class.java)
                if (loginResponse != null) {
                    val body = JsonObject().apply{
                        addProperty("request_token", loginResponse!!.requestToken.toString())
                    }
                    getSessionCoroutine(body)
                } else {
                    val error = "Incorrect login or password"
                    showError(error)
                }
            } else {
                Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun showError(error: String) {
        progressBar.visibility = View.INVISIBLE
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    /*
    private fun getSession(body: JsonObject) {
        var session: SessionResponse?
        RetrofitService.getMovieApi()
            .getSession(RetrofitService.getApiKey(), body).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson = Gson()
                    if(response.isSuccessful) {
                        session = gson.fromJson(response.body(), SessionResponse::class.java)
                        if (session != null) {
                            val session_id = session!!.sessionId
                            getAccountCoroutine(session_id)
                        }
                    }
                }
            })
    }
     */

    private fun getSessionCoroutine(body: JsonObject) {
        launch {
            var session: SessionResponse?
            val response = RetrofitService.getMovieApi().getSessionCoroutine(RetrofitService.getApiKey(), body)
            if (response.isSuccessful) {
                var gson = Gson()
                session = gson.fromJson(response.body(), SessionResponse::class.java)
                if (session != null) {
                    val sessionId = session!!.sessionId
                    getAccountCoroutine(sessionId)
                } else {
                    Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT)
                }
            }
        }
    }

    /*
    private fun getAccount(session: String?) {
        Toast.makeText(this, "Loading......", Toast.LENGTH_SHORT).show()

        var accountResponse: AccountResponse?

        RetrofitService.getMovieApi()
            .getAccount(RetrofitService.getApiKey(), session!!).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("My_token_failure", t.toString())
                }
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var gson = Gson()
                    if (response.isSuccessful) {
                        accountResponse = gson.fromJson(response.body(), AccountResponse::class.java)
                        if (accountResponse != null) {
                            showWelcome(accountResponse!!, session)
                        }
                    }
                }

            })

    }
    */

    private fun getAccountCoroutine(session: String?) {
        launch {
            Toast.makeText(this@LoginActivity, "Loading......", Toast.LENGTH_SHORT).show()
            var accountResponse: AccountResponse?
            val response = RetrofitService.getMovieApi().getAccountCoroutine(RetrofitService.getApiKey(), session!!)
            if (response.isSuccessful) {
                var gson = Gson()
                accountResponse = gson.fromJson(response.body(), AccountResponse::class.java)
                if (accountResponse != null) {
                    showWelcome(accountResponse!!, session)
                }
            } else {
                Log.d("My_token_failure", response.body().toString())
                Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun showWelcome(user: AccountResponse, session:String?) {
        progressBar.visibility = View.GONE
        CurrentUser.user = user
        CurrentUser.user!!.sessionId = session;
        saveSessionOfTheCurrentUser()
        val intent = Intent(this, MovieAppActivity::class.java)
        Toast.makeText(this, "Glad to see you, " + CurrentUser.user!!.userName, Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }

    private fun saveSessionOfTheCurrentUser(){
        val currUserSharedPreference: SharedPreferences = this!!.getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE)
        var currUserEditor = currUserSharedPreference.edit()
        val gson = Gson()
        val json: String = gson!!.toJson(CurrentUser.user)
        currUserEditor.putString("currentUser", json)
        currUserEditor.commit()
    }
}
