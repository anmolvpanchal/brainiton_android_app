@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.combrainiton.authentication

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.combrainiton.managers.UserManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_otp_verifiocation.*
import android.os.CountDownTimer
import android.R
import android.R.id.message
import android.util.Log
import java.util.logging.Logger
import java.util.regex.Pattern
import androidx.annotation.NonNull
import android.R.id.message
import android.app.Activity
import android.content.*
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions


class ActivityOTPVerification : AppCompatActivity(), View.OnClickListener {

    private var oTPStr: String = ""
    private var fromStr: String = ""
    private val TAG: String = "ActivityOTPVerification"    // to check the log

    // Start listening for SMS User Consent broadcasts from senderPhoneNumber
// The Task<Void> will be successful if SmsRetriever was able to start
// SMS User Consent, and will error if there was an error starting.

    private val SMS_CONSENT_REQUEST = 2  // Set to an unused request code
    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get consent intent
                        val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            Log.e("SUCCESS","TIMEOUT  working")

                            // Start activity to show consent dialog to user, activity must be started in
                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST)
                        } catch (e: ActivityNotFoundException) {
                            // Handle the exception ...
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        Log.e("TIMEOUT","TIMEOUT so not working")
                    }
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // ...
            SMS_CONSENT_REQUEST ->
                // Obtain the phone number from the result
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // Get SMS message content
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    // Extract one-time code from the message and complete verification
                    // `message` contains the entire text of the SMS message, so you will need
                    // to parse the string.

                    Log.e("content","Get SMS message content" + message)


                    val code = parseCode(message)//Parse verification code
                    etOTP.setText(code)//set OTP in edit text
                    btnSubmit.performClick()// automatically clicks
                    //then you can send verification code to server
                    // send one time code to the server

                } else {
                    // Consent denied. User can type OTC manually.
                    Log.e("denied","User can type OTC manually")

                }
        }
    }

    fun parseCode(message: String): String {

        val p = Pattern.compile("\\b\\d{6}\\b");
        val m = p.matcher(message)
        var code = ""
        while (m.find()) {
            code = m.group(0)
        }
        Log.i(TAG, "codenevalue $code")
        return code
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.combrainiton.R.layout.activity_otp_verifiocation)
        initViews()
        tvResendOtp.visibility = View.GONE

        val task = SmsRetriever.getClient(this@ActivityOTPVerification).startSmsUserConsent(null)

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter)

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
