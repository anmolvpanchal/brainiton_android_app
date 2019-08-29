package com.combrainiton.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.managers.UserManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_nav_my_profile.*

class ActivityNavMyProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_my_profile)
        initBottomMenu() //initialize bottom menu
        initMainView() //initialize main view
    }

    private fun initMainView() {

        val mDialog = AppProgressDialog(this@ActivityNavMyProfile)
        mDialog.show() //show progress bar
        UserManagement(this@ActivityNavMyProfile, this@ActivityNavMyProfile, mDialog)
                .getUserDetail(user_profile_mobile_no, user_profile_user_name) //this will get the user data and will close the progress dialog

        //open privacy link in browser
        user_profile_user_policy.setOnClickListener {
            //user_profile_user_policy.setTextColor(Color.parseColor("#16a5e1"))
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://brainiton.in/privacy.html")))
        }

        //open privacy link in browser
        user_profile_privacy_policy.setOnClickListener {
            //user_profile_privacy_policy.setTextColor(Color.parseColor("#16a5e1"))
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://brainiton.in/privacy.html")))
        }

        //open terms and condition link in browser
        user_profile_terms_and_conditions.setOnClickListener {
            //user_profile_terms_and_conditions.setTextColor(Color.parseColor("#16a5e1"))
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://brainiton.in/terms.html")))
        }

        user_setting_sound_button.isChecked = AppSharedPreference(this@ActivityNavMyProfile).getBoolean("sound")

        user_setting_sound_button.setOnClickListener {
            if(user_setting_sound_button.isChecked) {
                AppSharedPreference(this@ActivityNavMyProfile).saveBoolean("sound", true)
            }else{
                AppSharedPreference(this@ActivityNavMyProfile).saveBoolean("sound", false)
            }

        }

        //logout user
        user_profile_logout.setOnClickListener { doLogOut() }

    }

    //log out the user
    private fun doLogOut() {
        val mDialog = AppProgressDialog(this@ActivityNavMyProfile)
        mDialog.show() //open progress dialog
        // call logout method and pass the progress dialog object
        UserManagement(this@ActivityNavMyProfile, this@ActivityNavMyProfile, mDialog).doLogout()
    }

    /*this will inititlaize the Bottom Menu*/
    private fun initBottomMenu() {
        btm_nav_explore.setOnClickListener { explore() }
        btm_nav_compete.setOnClickListener {
            startActivity(Intent(this@ActivityNavMyProfile, ActivityNavCompete::class.java))
            (it.context as ActivityNavMyProfile).overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
        btm_nav_enter_pin.setOnClickListener { startActivity(Intent(this@ActivityNavMyProfile, ActivityNavEnterPin::class.java))
            (it.context as ActivityNavMyProfile).overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
        btm_nav_my_quizzes.setOnClickListener { startActivity(Intent(this@ActivityNavMyProfile, ActivityNavMyQuizzes::class.java))
            (it.context as ActivityNavMyProfile).overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
        //btm_nav_profile.setOnClickListener { Do Nothing }
    }

    // this will open the home activtiy
    private fun explore() {
        if (NetworkHandler(this@ActivityNavMyProfile).isNetworkAvailable()) { //if internet is connected
            val mDialog = AppProgressDialog(this@ActivityNavMyProfile)
            mDialog.show() //show progress dialog
            NormalQuizManagement(this@ActivityNavMyProfile, this@ActivityNavMyProfile, mDialog).getAllQuiz() //this will open the home activity after retriving the data
        } else {
            Toast.makeText(this@ActivityNavMyProfile, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show() //display error message
        }

    }

    //open home activtiy on backpressed
    override fun onBackPressed() {
        explore()
    }

}

