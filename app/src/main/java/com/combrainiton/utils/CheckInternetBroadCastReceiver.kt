package com.combrainiton.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast
import android.net.NetworkInfo
import android.support.annotation.IntegerRes
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import java.util.logging.Handler

import java.net.URL
import java.net.URLConnection


class CheckInternetBroadCastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val isConnected = checkConnectivity(context)

        if(connectionReceiverListener != null)
            connectionReceiverListener!!.onNetworkConnectionChanged(isConnected)

    }

    interface ConnectionReceiverListener{
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object{
        var connectionReceiverListener: ConnectionReceiverListener? =null

        val isConnected: Boolean

        get() {
            val cm = MyApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }
    }

    private fun checkConnectivity(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    private fun isNetworkInterfaceAvailable(context: Context): Boolean{
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    private fun isAbleToConnect(url: String,timeout: Int): Boolean{
        try {
            val myUrl = URL(url)
            val connection = myUrl.openConnection()
            connection.setConnectTimeout(timeout)
            connection.connect()
            return true
        } catch (e: Exception) {
            Log.i("exception", "" + e.message)
            return false
        }

    }

}