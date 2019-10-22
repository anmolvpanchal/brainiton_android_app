package com.combrainiton.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.combrainiton.CustomViewPager
import com.combrainiton.R
import com.combrainiton.adaptors.AdaptorBrandHomeList
import com.combrainiton.adaptors.CompeteAdapter
import com.combrainiton.adaptors.MySubscribtionAdapter
import com.combrainiton.adaptors.SubscriptionAdapter
import com.combrainiton.api.ApiClient
import com.combrainiton.fragments.AvailableSubscriptionFragment
import com.combrainiton.fragments.MySubscriptionFragment
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.subscription.*
import com.combrainiton.utils.*
import com.google.firebase.messaging.FirebaseMessaging
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_nav_compete.*
import kotlinx.android.synthetic.main.activity_nav_explore.*
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_premium
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_enter_pin
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_explore
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_my_quizzes
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_profile
import kotlinx.android.synthetic.main.compete_activity_popup.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class ActivityNavCompete : AppCompatActivity() {

    lateinit var viewPager: androidx.viewpager.widget.ViewPager
    lateinit var tabLayout: TabLayout
    lateinit var builder: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog
    lateinit var viewGroup: ViewGroup
    lateinit var keepPlaying: Button


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_compete)

        viewGroup = findViewById(android.R.id.content)
        viewPager = findViewById(R.id.compete_viewPager)

        //showDialog()

//        uncomment this two line to statr subscription model
        tabLayout = findViewById<TabLayout>(R.id.compete_tabLayout)
        InitViews()
        initBottomMenu()

    }

    fun showDialog(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.compete_activity_popup, viewGroup, false)
        builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)
        alertDialog = builder.create()
        keepPlaying = dialogView.findViewById(R.id.activity_compete_playButton)


        //This won't allow dialog to dismiss if touched outside it's area
        alertDialog.setCanceledOnTouchOutside(false)

        //Transparent background for alert dialog
        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()

        keepPlaying.setOnClickListener {
            explore()
        }

    }

    //initiates bottom navigation bar
    private fun initBottomMenu() {
        btm_nav_enter_pin.setOnClickListener {
            startActivity(Intent(this@ActivityNavCompete, ActivityNavEnterPin::class.java))
            (it.context as ActivityNavCompete).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        btm_nav_explore.setOnClickListener { explore() }
        btm_nav_my_quizzes.setOnClickListener {
            startActivity(Intent(this@ActivityNavCompete, ActivityNavMyQuizzes::class.java))
            (it.context as ActivityNavCompete).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        btm_nav_profile.setOnClickListener {
            startActivity(Intent(this@ActivityNavCompete, ActivityNavMyProfile::class.java))
            (it.context as ActivityNavCompete).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        btm_nav_premium.setOnClickListener {
            //do nothing
        }
    }


    //this will open the home activity
    private fun explore() {
        if (NetworkHandler(this@ActivityNavCompete).isNetworkAvailable()) { //if internet is connected
            val mDialog = AppProgressDialog(this@ActivityNavCompete)
            mDialog.show() //show progress dialog
            NormalQuizManagement(this@ActivityNavCompete, this@ActivityNavCompete, mDialog).getAllQuiz() //this will open the home activity after retriving the data
        } else {
            Toast.makeText(this@ActivityNavCompete, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show() //display error message
        }

    }


    override fun onBackPressed() {
        finish()
    }

    fun InitViews(){
        //create api client first
        val apiToken: String = AppSharedPreference(applicationContext).getString("apiToken")

        Log.e("sigin token" , "API token "+apiToken)

        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)
        val call = apiClient.getMysubscriptions()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(this@ActivityNavCompete, "Error", resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(applicationContext, "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return;
                }

                try {

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)
                    val subscriptions = rootObj.getJSONArray("subscriptions")
                    if (subscriptions.length().equals(0)){

                        val adapter = SubscriptionAdapter(supportFragmentManager)

                        adapter.addFragment(MySubscriptionFragment(), "My Subscription")
                        adapter.addFragment(AvailableSubscriptionFragment(), "Available Subscription")

                        viewPager.adapter = adapter
                        viewPager.setCurrentItem(1)
                        tabLayout.setupWithViewPager(viewPager)
                        //Tab Layout
                        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                            override fun onTabSelected(tab: TabLayout.Tab) {
                                viewPager.currentItem = tab.position
                            }

                            override fun onTabUnselected(tab: TabLayout.Tab) {

                            }

                            override fun onTabReselected(tab: TabLayout.Tab) {

                            }
                        })



                    }else {

                        val adapter = SubscriptionAdapter(supportFragmentManager)

                        adapter.addFragment(MySubscriptionFragment(), "My Subscription")
                        adapter.addFragment(AvailableSubscriptionFragment(), "Available Subscription")

                        viewPager.adapter = adapter
                        tabLayout.setupWithViewPager(viewPager)

                        //Tab Layout
                        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                            override fun onTabSelected(tab: TabLayout.Tab) {
                                viewPager.currentItem = tab.position
                            }

                            override fun onTabUnselected(tab: TabLayout.Tab) {

                            }

                            override fun onTabReselected(tab: TabLayout.Tab) {

                            }
                        })
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

