package com.combrainiton.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.combrainiton.R
import com.combrainiton.adaptors.CompeteAdapter
import com.combrainiton.adaptors.SubscriptionAdapter
import com.combrainiton.fragments.AvailableSubscriptionFragment
import com.combrainiton.fragments.MySubscriptionFragment
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.NetworkHandler
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_nav_compete.*
import kotlinx.android.synthetic.main.activity_nav_explore.*
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_premium
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_enter_pin
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_explore
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_my_quizzes
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_profile
import kotlinx.android.synthetic.main.compete_activity_popup.*
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

        showDialog()

//        uncomment this two line to statr subscription model
//        tabLayout = findViewById<TabLayout>(R.id.compete_tabLayout)
//        initView()
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


    fun initView() {

        viewPager = findViewById(R.id.compete_viewPager) as androidx.viewpager.widget.ViewPager

        val adapter = SubscriptionAdapter(supportFragmentManager)

        adapter.addFragment(MySubscriptionFragment(), "My Subscription")
        adapter.addFragment(AvailableSubscriptionFragment(), "Available Subscription")

        viewPager.adapter = adapter
        tabLayout!!.setupWithViewPager(viewPager)

        //Tab Layout
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

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

}

