@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.combrainiton.authentication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.managers.UserManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.btnSendOtp
import kotlinx.android.synthetic.main.activity_sign_up.etPhone
import kotlinx.android.synthetic.main.activity_sign_up.progress_bar
import kotlinx.android.synthetic.main.activity_sign_up.user_profile_user_policy

class ActivitySignUp : AppCompatActivity(), View.OnClickListener {

    private lateinit var nameStr: String //for stroing user name
    private lateinit var mobileStr: String //for storing user mobile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initViews() //initialize main view
    }

    private fun initViews() {

        //set onclick listenet to send otp button
        btnSendOtp.setOnClickListener(this@ActivitySignUp)

        //set on cick listener to user profile link
        user_profile_user_policy.setOnClickListener(this@ActivitySignUp)

        //get from key from intent data
        val fromStr: String = intent.extras.getString("from")

        //if from key equals to verify user came from sign in activtiy to verify their number
        //in that case load the user name and mobile no from intent data
        if (fromStr == "verify") {
            val mobile: String = intent.extras.getString("mobile")
            val name: String = intent.extras.getString("name")
            etPhone.setText(mobile)
            etName.setText(name)
        }

        //set login button on clicklistener
        tvLogin.setOnClickListener(this@ActivitySignUp)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSendOtp -> { //onclick of opt send button
                getData() //get user name and uuser number
                if (isValidInput()) { //if input is valid
                    if (NetworkHandler(this@ActivitySignUp).isNetworkAvailable()) { //if network is availabel
                        sendOTPRequest() //send otp request to the server
                    } else { //show error if network is not available
                        Toast.makeText(this@ActivitySignUp, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
                    }
                }
            }
            //onclick of user policy link
            R.id.user_profile_user_policy -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://brainiton.in/privacy.html")))
            }
            //onclick of login button
            R.id.tvLogin -> {
                //start activity sign in and from key with signup value
                startActivity(Intent(this@ActivitySignUp, ActivitySignIn::class.java)
                        .putExtra("from", "signUP"))
                finish() //finish current activtity
            }
        }
    }

    //get user data
    private fun getData() {
        nameStr = etName.text.toString().trim()
        mobileStr = etPhone.text.toString().trim()
    }

    //check user data validity
    private fun isValidInput(): Boolean {
        return if (nameStr.isEmpty()) {
            etName.error = resources.getString(R.string.error_enter_your_name)
            false
        }else if( mobileStr.isEmpty()) {
            etPhone.error = getString(R.string.error_enter_mobileno)
            false
        } else if (nameStr.isEmpty() || !nameStr.matches("[a-zA-z .?]*".toRegex())) {
            etName.error = resources.getString(R.string.error_enter_your_name)
            false
        } else if (mobileStr.isEmpty() || mobileStr.length < 10 || !mobileStr.matches("[0-9.?]*".toRegex())) {
            etPhone.error = resources.getString(R.string.error_incorrect_mobile)
            false
        } else {
            true
        }
    }

    //send otp request
    private fun sendOTPRequest() {
        progress_bar.visibility = View.VISIBLE
        val mProgressDialog = AppProgressDialog(this)
        mProgressDialog.show()
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        val requestMap = HashMap<String, String>()
        requestMap["user_id"] = mobileStr
        requestMap["name"] = nameStr
        val requestManagement = UserManagement(this, this, mProgressDialog, requestMap)
        requestManagement.sendOTPRequest("signUp",progress_bar)

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

}