package com.combrainiton.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.combrainiton.R
import com.combrainiton.adaptors.CoursePagerAdapter
import com.combrainiton.main.Activity_leaderboard
import com.combrainiton.main.CourseHomePage
import com.combrainiton.subscription.LessonsDataList_API
import com.combrainiton.subscription.ServiceGenerator
import com.combrainiton.subscription.SubscriptionDataList_API
import com.combrainiton.subscription.SubscriptionInterface
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppSharedPreference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_course_progress.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CourseProgressFragment(val subscriptionID: String, val activity: CourseHomePage) : androidx.fragment.app.Fragment() {

    lateinit var leaderboardCard: CardView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_course_progress, container, false)

        getLessonsFromApiForSubscribedUser(subscriptionID)

        leaderboardCard = view.findViewById(R.id.showleaderboard)

        leaderboardCard.setOnClickListener {
            startActivity(Intent(activity,Activity_leaderboard::class.java)
                    .putExtra("subId",subscriptionID)
                    .putExtra("from","progress"))
        }

        return view
    }


    fun getLessonsFromApiForSubscribedUser(subID: String) {

        //create api client first
        val apiToken: String = AppSharedPreference(activity).getString("apiToken")


        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)
        val call = apiClient.getAllLessons(subID.toInt())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(activity, "Error", resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return
                }

                try {

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)

                    val subscription = rootObj.getJSONArray("subscription")

                    val rank = rootObj.getString("rank")

                    for (j in 0 until subscription.length()) {

                        val innerobject_sub: JSONObject = subscription.getJSONObject(j)

                        val total_lessons = innerobject_sub.getString("total_lessons")
                        val unlocked = innerobject_sub.getString("unlocked")
                        val attempted = innerobject_sub.getString("attempted")
                        val accuracy = innerobject_sub.getString("accuracy")
                        val progress = innerobject_sub.getString("progress")

                        Totallessons_text.text = total_lessons+" Total lessons"
                        unlocked_text.text = unlocked+" Unlocked"
                        Attempted_text.text = attempted+" Attempted"
                        donut_accuracy.progress = accuracy.toFloat()
                        donut_completion.progress = progress.toFloat()
                        results_rank.text = rank
                    }


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
