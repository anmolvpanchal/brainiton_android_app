package com.combrainiton.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
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
import kotlinx.android.synthetic.main.activity_nav_compete.*
import kotlinx.android.synthetic.main.activity_nav_explore.*
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_compete
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_enter_pin
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_explore
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_my_quizzes
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_profile
import kotlin.collections.ArrayList


class ActivityNavCompete : AppCompatActivity() {

    lateinit var viewPager: ViewPager

    private val TAG = "ActivityNavCompete"

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

        viewPager = findViewById(R.id.compete_viewPager) as ViewPager

        val adapter = SubscriptionAdapter(supportFragmentManager, this@ActivityNavCompete)

        adapter.addFragment(MySubscriptionFragment(), "My Subscription", this@ActivityNavCompete)
        adapter.addFragment(AvailableSubscriptionFragment(), "Available Subscription", this@ActivityNavCompete)

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
            //do nothimg
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
        explore()
    }
}

