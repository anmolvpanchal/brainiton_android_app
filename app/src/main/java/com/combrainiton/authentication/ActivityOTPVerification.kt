@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.combrainiton.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.combrainiton.managers.UserManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_otp_verifiocation.*
import android.os.CountDownTimer
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import android.R
import android.R.id.message
import android.util.Log
import java.util.logging.Logger
import java.util.regex.Pattern
import androidx.annotation.NonNull
import android.R.id.message


class ActivityOTPVerification : AppCompatActivity(), View.OnClickListener {

    private lateinit var smsVerifyCatcher: SmsVerifyCatcher
    private var oTPStr: String = ""
    private var fromStr: String = ""
    private val TAG: String = "ActivityOTPVerification"    // to check the log

    override fun onStart() { // used for SMS catcher
        super.onStart()
        smsVerifyCatcher.onStart();
    }

    override fun onStop() { // used for SMS catcher
        super.onStop()
        smsVerifyCatcher.onStop();
    }

    // used for SMS catcher to check permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.combrainiton.R.layout.activity_otp_verifiocation)
        initViews()
        tvResendOtp.visibility = View.GONE

        val timer = object : CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                timerTextView.text = "00:" + millisUntilFinished / 1000

                if (timerTextView.text.equals("00:0")){
                    tvResendOtp.visibility = View.VISIBLE
                }
            }

            override fun onFinish() {}
        }
        timer.start()




        // to automatically get and set the OTP

        smsVerifyCatcher = SmsVerifyCatcher(this, OnSmsCatchListener { message ->
            val code = parseCode(message)//Parse verification code
            etOTP.setText(code)//set OTP in edit text
            btnSubmit.performClick()// automatically clicks
            //then you can send verification code to server
        })
    }

    // this fun is used to fetch whole message and get OTP from message

    fun parseCode(message: String): String {

        val p = Pattern.compile("\\b\\d{6}\\b");
        val m = p.matcher(message)
        var code = ""
        while (m.find()) {
            code = m.group(0)
        }
        Log.e(TAG, "codenevalue $code")
        return code
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        fromStr = intent.extras.getString("from")

        val mobileNum: String = intent.extras.getString("mobile")

        mobileNumTextView.text = "+91 $mobileNum"

        tvResendOtp
        btnSubmit.setOnClickListener(this@ActivityOTPVerification)
        tvResendOtp.setOnClickListener(this@ActivityOTPVerification)
        tvEditMobile.setOnClickListener(this@ActivityOTPVerification)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            com.combrainiton.R.id.btnSubmit -> {
                getData()
                if (isValidInput()) {
                    if (NetworkHandler(this@ActivityOTPVerification).isNetworkAvailable()) {
                        verifyOTP()
                    } else {
                        Toast.makeText(this@ActivityOTPVerification, resources.getString(com.combrainiton.R.string.error_network_issue), Toast.LENGTH_LONG).show()
                    }
                }
            }

            com.combrainiton.R.id.tvResendOtp -> {
                if (NetworkHandler(this@ActivityOTPVerification).isNetworkAvailable()) {
                    reSendOTPRequest()
                } else {
                    Toast.makeText(this@ActivityOTPVerification, resources.getString(com.combrainiton.R.string.error_network_issue), Toast.LENGTH_LONG).show()
                }
            }

            com.combrainiton.R.id.tvEditMobile -> {
                startActivity(editMobile())
                finish()
            }

        }
    }

    private fun editMobile(): Intent {

        val backIntent: Intent
        val mobileStr: String = intent.extras.getString("mobile")

        when (fromStr) {

            "signUp" -> {
                backIntent = Intent(this@ActivityOTPVerification, ActivitySignUp::class.java)
                val nameStr: String = intent.extras.getString("name")
                backIntent.putExtra("name", nameStr)
                backIntent.putExtra("mobile", mobileStr)
                backIntent.putExtra("from", "verify")

            }

            "signIn" -> {
                backIntent = Intent(this@ActivityOTPVerification, ActivitySignIn::class.java)
                backIntent.putExtra("mobile", mobileStr)
                backIntent.putExtra("from", "verify")

            }

            else -> {
                backIntent = Intent(this@ActivityOTPVerification, ActivitySignIn::class.java)
                backIntent.putExtra("mobile", mobileStr)
                backIntent.putExtra("from", "verify")
            }

        }

        return backIntent

    }

    private fun verifyOTP() {
        val mProgressDialog = AppProgressDialog(this)
        mProgressDialog.show()
        val requestMap = HashMap<String, String>()
        if (fromStr == "signUp") {
            requestMap["otp"] = oTPStr

        } else {
            requestMap["mobile_otp"] = oTPStr
        }
        val sessionIdStr: String = intent.extras.getString("session_id")
        requestMap["session_id"] = sessionIdStr
        val requestManagement = UserManagement(this, this, mProgressDialog, requestMap)
        requestManagement.verifyOTP(fromStr, etOTP, tvEnterValidOtp)
    }

    private fun isValidInput(): Boolean {
        return if (oTPStr.isEmpty() || oTPStr.length < 6) {
            Toast.makeText(this@ActivityOTPVerification, resources.getString(com.combrainiton.R.string.error_invalid_input), Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    private fun getData() {
        oTPStr = etOTP.text.toString().trim()
    }

    private fun reSendOTPRequest() {

        progress_bar.visibility = View.VISIBLE
        val mProgressDialog = AppProgressDialog(this)
        mProgressDialog.show()

        val requestMap = HashMap<String, String>()

        val mobileStr: String = intent.extras.getString("mobile")
        if (fromStr == "signUp") {
            val nameStr: String = intent.extras.getString("name")
            requestMap["name"] = nameStr
        }

        requestMap["user_id"] = mobileStr

        val requestManagement = UserManagement(this, this, mProgressDialog, requestMap)
        requestManagement.sendOTPRequest(fromStr, progress_bar)

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

}
