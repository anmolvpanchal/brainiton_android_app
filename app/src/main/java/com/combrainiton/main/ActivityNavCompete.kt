package com.combrainiton.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.widget.Button
import android.widget.Toast
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
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_compete
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_enter_pin
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_explore
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_my_quizzes
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_profile
import kotlin.collections.ArrayList


class ActivityNavCompete : AppCompatActivity() {

    lateinit var viewPager: androidx.viewpager.widget.ViewPager
    lateinit var tabLayout: TabLayout

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_compete)

        tabLayout = findViewById<TabLayout>(R.id.compete_tabLayout)

        initView()
        initBottomMenu()

    }


    fun initView() {

        viewPager = findViewById(R.id.compete_viewPager) as androidx.viewpager.widget.ViewPager

        val adapter = SubscriptionAdapter(supportFragmentManager)

        adapter.addFragment(MySubscriptionFragment(),"My Subscription")
        adapter.addFragment(AvailableSubscriptionFragment(),"Available Subscription")

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
        btm_nav_compete.setOnClickListener {
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

