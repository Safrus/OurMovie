package com.example.ourmovie.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ourmovie.R
import com.example.ourmovie.RetrofitService
import com.example.ourmovie.activities.LoginActivity
import com.example.ourmovie.user.CurrentUser
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class ProfileFragment: Fragment(), CoroutineScope {

    private lateinit var userName: TextView
    private lateinit var logOutBtn: Button

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater!!.inflate(R.layout.profile_fragment, container, false)
        userName = view.findViewById(R.id.userName)
        logOutBtn = view.findViewById(R.id.logOutBtn)
        userName.text = CurrentUser.user?.userName
        logOutBtn.setOnClickListener {
            val body = JsonObject().apply {
                addProperty("session_id", CurrentUser.user!!.sessionId)
            }
            deleteSessionCoroutine(body)
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    /*
    private fun deleteSession(body: JsonObject) {
        RetrofitService.getMovieApi()
            .deleteSession(RetrofitService.getApiKey(),body).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        sayGoodBye()
                    }
                }
            })
    }
     */

    private fun deleteSessionCoroutine(body: JsonObject) {
        launch {
            val response = RetrofitService.getMovieApi().deleteSessionCoroutine(RetrofitService.getApiKey(), body)
            if (response.isSuccessful) {
                sayGoodBye()
            } else {
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun sayGoodBye() {
        val intent = Intent(this.activity, LoginActivity::class.java)
        val sharedPreference: SharedPreferences = this.activity!!.getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE)
        sharedPreference.edit().remove("currentUser").commit()
        Toast.makeText(this.context, "GoodBye, " + CurrentUser.user!!.userName + "!", Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }
}