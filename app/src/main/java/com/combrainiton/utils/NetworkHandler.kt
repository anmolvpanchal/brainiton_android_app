package com.combrainiton.utils

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast

class NetworkHandler(var context: Context) {
    fun isNetworkAvailable(): Boolean {
        val manager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo
        val isConnected = info != null && info.isConnected
        if (!isConnected) {
            Toast.makeText(context, "No Connection available!", Toast.LENGTH_SHORT).show()
        }
        return isConnected
    }
} 