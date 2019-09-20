package com.combrainiton.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.combrainiton.R
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.managers.UserManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_nav_my_profile.*
import java.lang.Exception

class ActivityNavMyProfile : AppCompatActivity(){

    lateinit var builder: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog
    lateinit var viewGroup: ViewGroup
    var alien1:Boolean = false
    var alien2:Boolean = false
    var alien3:Boolean = false
    var alien4:Boolean = false
    var alien5:Boolean = false
    var alien6:Boolean = false
    lateinit var alien1Img: ImageView
    lateinit var alien2Img: ImageView
    lateinit var alien3Img: ImageView
    lateinit var alien4Img: ImageView
    lateinit var alien5Img: ImageView
    lateinit var alien6Img: ImageView
    lateinit var alien1Layout: RelativeLayout
    lateinit var alien2Layout: RelativeLayout
    lateinit var alien3Layout: RelativeLayout
    lateinit var alien4Layout: RelativeLayout
    lateinit var alien5Layout: RelativeLayout
    lateinit var alien6Layout: RelativeLayout
    lateinit var doneBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_my_profile)

        viewGroup = findViewById(android.R.id.content)

        val sharedPreference = this.getSharedPreferences("ProfilePic", Context.MODE_PRIVATE)
        try {
            val profile = sharedPreference.getString("Profile",null)
            if(profile.equals("alien1")){
                profilePic.setImageDrawable(resources.getDrawable(R.drawable.alien1))
            }else if(profile.equals("alien2")){
                profilePic.setImageDrawable(resources.getDrawable(R.drawable.alien2))
            }else if(profile.equals("alien3")){
                profilePic.setImageDrawable(resources.getDrawable(R.drawable.alien3))
            }else if(profile.equals("alien4")){
                profilePic.setImageDrawable(resources.getDrawable(R.drawable.alien4))
            }else if(profile.equals("alien5")){
                profilePic.setImageDrawable(resources.getDrawable(R.drawable.alien5))
            }else if(profile.equals("alien6")){
                profilePic.setImageDrawable(resources.getDrawable(R.drawable.alien6))
            }
        }catch (e: Exception){

        }

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

        profilePic.setOnClickListener {
            showAvatarPopUp()
        }

        //logout user
        user_profile_logout.setOnClickListener { doLogOut() }

    }

    private fun showAvatarPopUp() {

        //create custom dialog for description
        val dialogView = LayoutInflater.from(this).inflate(R.layout.profile_pic_selector,viewGroup,false)
        builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        alertDialog= builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alien1Img = dialogView.findViewById(R.id.alien1_img)
        alien2Img = dialogView.findViewById(R.id.alien2_img)
        alien3Img = dialogView.findViewById(R.id.alien3_img)
        alien4Img = dialogView.findViewById(R.id.alien4_img)
        alien5Img = dialogView.findViewById(R.id.alien5_img)
        alien6Img = dialogView.findViewById(R.id.alien6_img)
        alien1Layout = dialogView.findViewById(R.id.alien1_layout)
        alien2Layout = dialogView.findViewById(R.id.alien2_layout)
        alien3Layout = dialogView.findViewById(R.id.alien3_layout)
        alien4Layout = dialogView.findViewById(R.id.alien4_layout)
        alien5Layout = dialogView.findViewById(R.id.alien5_layout)
        alien6Layout = dialogView.findViewById(R.id.alien6_layout)
        doneBtn = dialogView.findViewById(R.id.profile_pic_done)

        doneBtn.setOnClickListener {
            val sharedPreference = this.getSharedPreferences("ProfilePic", Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()

            if(alien1){
                editor.putString("Profile","alien1")
                profilePic.setImageDrawable(resources.getDrawable(R.drawable.alien1))
            }else if(alien2){
                editor.putString("Profile","alien2")
                profilePic.setImageDrawable(resources.getDrawable(R.drawable.alien2))
            }else if(alien3){
                editor.putString("Profile","alien3")
                profilePic.setImageDrawable(resources.getDrawable(R.drawable.alien3))
            }else if(alien4){
                editor.putString("Profile","alien4")
                profilePic.setImageDrawable(resources.getDrawable(R.drawable.alien4))
            }else if(alien5){
                editor.putString("Profile","alien5")
                profilePic.setImageDrawable(resources.getDrawable(R.drawable.alien5))
            }else if(alien6){
                editor.putString("Profile","alien6")
                profilePic.setImageDrawable(resources.getDrawable(R.drawable.alien6))
            }

            editor.apply()
            editor.commit()
            alertDialog.dismiss()
        }

        alien1Img.setOnClickListener {
            //Setting background and removing from other layouts
            alien1Layout.setBackgroundResource(R.drawable.avatar)
            alien2Layout.setBackgroundResource(R.drawable.avatar_white)
            alien3Layout.setBackgroundResource(R.drawable.avatar_white)
            alien4Layout.setBackgroundResource(R.drawable.avatar_white)
            alien5Layout.setBackgroundResource(R.drawable.avatar_white)
            alien6Layout.setBackgroundResource(R.drawable.avatar_white)

            alien1 = true
            alien2 = false
            alien3 = false
            alien4 = false
            alien5 = false
            alien6 = false

        }

        alien2Img.setOnClickListener {
            //Setting background and removing from other layouts
            alien1Layout.setBackgroundResource(R.drawable.avatar_white)
            alien2Layout.setBackgroundResource(R.drawable.avatar)
            alien3Layout.setBackgroundResource(R.drawable.avatar_white)
            alien4Layout.setBackgroundResource(R.drawable.avatar_white)
            alien5Layout.setBackgroundResource(R.drawable.avatar_white)
            alien6Layout.setBackgroundResource(R.drawable.avatar_white)

            alien1 = false
            alien2 = true
            alien3 = false
            alien4 = false
            alien5 = false
            alien6 = false
        }

        alien3Img.setOnClickListener {
            //Setting background and removing from other layouts
            alien1Layout.setBackgroundResource(R.drawable.avatar_white)
            alien2Layout.setBackgroundResource(R.drawable.avatar_white)
            alien3Layout.setBackgroundResource(R.drawable.avatar)
            alien4Layout.setBackgroundResource(R.drawable.avatar_white)
            alien5Layout.setBackgroundResource(R.drawable.avatar_white)
            alien6Layout.setBackgroundResource(R.drawable.avatar_white)

            alien1 = false
            alien2 = false
            alien3 = true
            alien4 = false
            alien5 = false
            alien6 = false
        }

        alien4Img.setOnClickListener {
            //Setting background and removing from other layouts
            alien1Layout.setBackgroundResource(R.drawable.avatar_white)
            alien2Layout.setBackgroundResource(R.drawable.avatar_white)
            alien3Layout.setBackgroundResource(R.drawable.avatar_white)
            alien4Layout.setBackgroundResource(R.drawable.avatar)
            alien5Layout.setBackgroundResource(R.drawable.avatar_white)
            alien6Layout.setBackgroundResource(R.drawable.avatar_white)

            alien1 = false
            alien2 = false
            alien3 = false
            alien4 = true
            alien5 = false
            alien6 = false
        }

        alien5Img.setOnClickListener {
            //Setting background and removing from other layouts
            alien1Layout.setBackgroundResource(R.drawable.avatar_white)
            alien2Layout.setBackgroundResource(R.drawable.avatar_white)
            alien3Layout.setBackgroundResource(R.drawable.avatar_white)
            alien4Layout.setBackgroundResource(R.drawable.avatar_white)
            alien5Layout.setBackgroundResource(R.drawable.avatar)
            alien6Layout.setBackgroundResource(R.drawable.avatar_white)

            alien1 = false
            alien2 = false
            alien3 = false
            alien4 = false
            alien5 = true
            alien6 = false
        }

        alien6Img.setOnClickListener {
            //Setting background and removing from other layouts
            alien1Layout.setBackgroundResource(R.drawable.avatar_white)
            alien2Layout.setBackgroundResource(R.drawable.avatar_white)
            alien3Layout.setBackgroundResource(R.drawable.avatar_white)
            alien4Layout.setBackgroundResource(R.drawable.avatar_white)
            alien5Layout.setBackgroundResource(R.drawable.avatar_white)
            alien6Layout.setBackgroundResource(R.drawable.avatar)

            alien1 = false
            alien2 = false
            alien3 = false
            alien4 = false
            alien5 = false
            alien6 = true
        }

        alertDialog.show()

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
        btm_nav_premium.setOnClickListener {
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
        finish()
    }

}

