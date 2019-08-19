package com.combrainiton.main

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.combrainiton.R
import com.combrainiton.authentication.ActivitySignIn
import com.combrainiton.utils.CheckInternetBroadCastReceiver
import com.combrainiton.utils.MyApplication
import java.io.Serializable

class NoInternet : AppCompatActivity(), CheckInternetBroadCastReceiver.ConnectionReceiverListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        //No internet code
        baseContext.registerReceiver(CheckInternetBroadCastReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        MyApplication.instance.setConnectionListener(this)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        if (isConnected) {

            Log.i("NoInternet", "yes")
            if (intent!!.extras.getString("nointernet").equals("ActivitySignIn")) {
                startActivity(Intent(this@NoInternet, ActivitySignIn::class.java)
                        .putExtra("from", "NoInternet"))
                finish()
            }

        }
    }
}
