package com.combrainiton.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.adaptors.MyReportsPagerAdapter
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_nav_my_quizzes.*

class ActivityNavMyQuizzes : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_my_quizzes)
        initBottomMenu() //initializ bottom nav bar
        initMainView() //initialize  main view
    }

    //this will initiate the main view in xml file
    private fun initMainView() {
        val viewPager = findViewById<ViewPager>(R.id.my_quizzes_view_pager)
        if (viewPager != null) {
            val adapter = MyReportsPagerAdapter(supportFragmentManager)
            viewPager.adapter = adapter
            my_quizzes_tab_layout.setupWithViewPager(my_quizzes_view_pager)
            my_quizzes_tab_layout.tabMode = TabLayout.MODE_SCROLLABLE
        }
        viewPager.offscreenPageLimit = 3
    }

    //this will initialize the bottom nav menu
    private fun initBottomMenu() {
        btm_nav_enter_pin.setOnClickListener { startActivity(Intent(this@ActivityNavMyQuizzes, ActivityNavEnterPin::class.java))
            (it.context as ActivityNavMyQuizzes).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        btm_nav_explore.setOnClickListener { explore()
            (it.context as ActivityNavMyQuizzes).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        //btm_nav_my_quizzes.setOnClickListener { do nothing }
        btm_nav_profile.setOnClickListener { startActivity(Intent(this@ActivityNavMyQuizzes, ActivityNavMyProfile::class.java))
            (it.context as ActivityNavMyQuizzes).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        btm_nav_compete.setOnClickListener { startActivity(Intent(this@ActivityNavMyQuizzes, ActivityNavCompete::class.java))
            (it.context as ActivityNavMyQuizzes).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

    }

    // this will open the home activtiy
    private fun explore() {
        if (NetworkHandler(this@ActivityNavMyQuizzes).isNetworkAvailable()) { //if internet is connected
            val mDialog = AppProgressDialog(this@ActivityNavMyQuizzes)
            mDialog.show() //show progress dialog
            NormalQuizManagement(this@ActivityNavMyQuizzes, this@ActivityNavMyQuizzes, mDialog).getAllQuiz() //this will open the home activity after retriving the data
        } else {
            Toast.makeText(this@ActivityNavMyQuizzes, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show() //display error message
        }
    }

    //open home activtiy on backpressed
    override fun onBackPressed() {
        explore()
    }

}