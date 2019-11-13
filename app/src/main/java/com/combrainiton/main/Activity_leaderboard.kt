package com.combrainiton.main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.combrainiton.R
import com.combrainiton.adaptors.AdapterLeaderboard
import com.combrainiton.adaptors.CoursePagerAdapter
import com.combrainiton.fragments.CourseDescriptionFragmentForMySubscription
import com.combrainiton.fragments.CourseLessonsFragment
import com.combrainiton.fragments.CourseProgressFragment
import com.combrainiton.subscription.*
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppSharedPreference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_leaderboard.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class Activity_leaderboard : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    val leaderboardModel : ArrayList<LeaderboardModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        if(intent.getStringExtra("from").equals("description")){
            gettingDetailsForPlayers(intent.getIntExtra("quizId",0))
        } else if(intent.getStringExtra("from").equals("progress")){
            getLessonsFromApiForSubscribedUser(intent.getStringExtra("subId"))
        }

        recyclerView = findViewById(R.id.leaderboard_recycler)
    }

    fun gettingDetailsForPlayers(QuizID: Int) {

        //create api client first
        val apiToken: String = AppSharedPreference(this).getString("apiToken")

        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)
        val call = apiClient.getDataForResult(QuizID)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(this@Activity_leaderboard, "Error", this@Activity_leaderboard.resources.getString(R.string.error_server_problem))
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(this@Activity_leaderboard, "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return
                }

                try {

                    leaderboardModel.clear()

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)
                    val leaderboard: JSONArray = rootObj.getJSONArray("leaderboard")

                    val latest_score = rootObj.getString("latest_score")
                    val rank = rootObj.getString("rank")
                    val name = rootObj.getString("name")

                    myname.text = name
                    myrank.text = rank
                    myscoreoraccuracy.text = latest_score
                    scoreoraccuracyTextView.text = "score"

                    for(i in 0 until leaderboard.length()){
                        val obj = leaderboard.getJSONObject(i)

                        val rank = obj.getString("rank")
                        val score = obj.getString("score")
                        val name = obj.getString("player")

                        leaderboardModel.add(LeaderboardModel(rank,score,name))
                    }

                    val adapter = AdapterLeaderboard(this@Activity_leaderboard,this@Activity_leaderboard,leaderboardModel)
                    recyclerView.layoutManager = LinearLayoutManager(this@Activity_leaderboard)
                    recyclerView.adapter = adapter

                } catch (ex: Exception) {
                    when (ex) {
                        is IllegalAccessException, is IndexOutOfBoundsException -> {
                            Log.e("catch block", "some known exception" + ex)
                        }
                        else -> Log.e("catch block", "other type of exception" + ex)

                    }

                }
            }

        })


    }

    fun getLessonsFromApiForSubscribedUser(subID: String) {

        //create api client first
        val apiToken: String = AppSharedPreference(this).getString("apiToken")


        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)
        val call = apiClient.getAllLessons(subID.toInt())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(this@Activity_leaderboard, "Error", resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(this@Activity_leaderboard, "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return
                }

                try {

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)
                    val name = rootObj.getString("name")
                    val rank = rootObj.getString("rank")
                    val accuracy = rootObj.getString("accuracy")

                    myname.text = name
                    myrank.text = rank
                    myscoreoraccuracy.text = "${accuracy}%"
                    scoreoraccuracyTextView.text = "accuracy"

                    val leaderboard = rootObj.getJSONArray("leaderboard")

                    for (i in 0 until leaderboard.length()) {

                        val innerObject: JSONObject = leaderboard.getJSONObject(i)

                        val name = innerObject.getString("player")
                        val score = innerObject.getString("accuracy")
                        val rank = innerObject.getString("rank")

                        leaderboardModel.add(LeaderboardModel(rank,score,name))
                    }

                    val adapter = AdapterLeaderboard(this@Activity_leaderboard,this@Activity_leaderboard,leaderboardModel)
                    recyclerView.layoutManager = LinearLayoutManager(this@Activity_leaderboard)
                    recyclerView.adapter = adapter


                } catch (ex: Exception) {
                    when (ex) {
                        is IllegalAccessException, is IndexOutOfBoundsException -> {
                            Log.e("catch block", "some known exception" + ex)
                        }
                        else -> Log.e("catch block", "other type of exception" + ex)

                    }

                }
            }

        })


    }
}
