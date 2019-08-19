@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.combrainiton.authentication

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.main.NoInternet
import com.combrainiton.managers.UserManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.CheckInternetBroadCastReceiver
import com.combrainiton.utils.MyApplication
import com.combrainiton.utils.NetworkHandler
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.io.Serializable


class ActivitySignIn : AppCompatActivity(),CheckInternetBroadCastReceiver.ConnectionReceiverListener, View.OnClickListener{

    private lateinit var mobileStr: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //No internet code
        baseContext.registerReceiver(CheckInternetBroadCastReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        MyApplication.instance.setConnectionListener(this)

        initViews()

    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
         if(! isConnected) {
             startActivity(Intent(this@ActivitySignIn, NoInternet::class.java)
                     .putExtra("nointernet", "ActivitySignIn"))
             finish()
             Log.i("Signin","No")
         }else{
             Log.i("Signin","Yes")
         }
    }

    private fun initViews() {
        btnSendOtp.setOnClickListener(this@ActivitySignIn)
        tvRegister.setOnClickListener(this@ActivitySignIn)
        val fromStr: String = intent.extras.getString("from")
        if (fromStr == "verify") {
            val mobile: String = intent.extras.getString("mobile")
            etPhone.setText(mobile)
            user_profile_user_policy.setOnClickListener(this@ActivitySignIn)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSendOtp -> {
                getData()
                if (isValidInput()) {
                    if (NetworkHandler(this@ActivitySignIn).isNetworkAvailable()) {
                        sendOTPRequest()
                    } else {
                        Toast.makeText(this@ActivitySignIn, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
                    }
                }
            }
            R.id.tvRegister -> {
                startActivity(Intent(this@ActivitySignIn, ActivitySignUp::class.java)
                        .putExtra("from", "signIn"))
                       // .putExtra("act",this@ActivitySignIn)
                finish()
            }
            //onclick of user policy link
            R.id.user_profile_user_policy -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://brainiton.in/privacy.html")))
            }
        }
    }

    private fun getData() {

        mobileStr = etPhone.text.toString().trim()

    }

    private fun isValidInput(): Boolean {

        return if (mobileStr.isEmpty() || mobileStr.length < 10 || !mobileStr.matches("[0-9.?]*".toRegex())) {
            etPhone.error = resources.getString(R.string.error_incorrect_mobile)
            false
        } else {
            true

        }

    }

    private fun sendOTPRequest() {
        progress_bar.visibility = View.VISIBLE
        val mProgressDialog = AppProgressDialog(this)
        mProgressDialog.show()
        val requestMap = HashMap<String, String>()
        requestMap["user_id"] = mobileStr
        val requestManagement = UserManagement(this, this, mProgressDialog, requestMap)
        requestManagement.sendOTPRequest("signIn",progress_bar)
    }

    override fun onBackPressed() {
        startActivity(Intent(this@ActivitySignIn, ActivitySignUp::class.java)
                .putExtra("from", "signIn"))
        finish()
    }

}
