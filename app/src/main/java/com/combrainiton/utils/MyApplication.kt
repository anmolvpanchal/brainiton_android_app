package com.combrainiton.utils

import android.app.Application

 class MyApplication : Application(){

     override fun onCreate() {
         super.onCreate()
         instance = this
     }

     fun setConnectionListener(lister: CheckInternetBroadCastReceiver.ConnectionReceiverListener){
         CheckInternetBroadCastReceiver.connectionReceiverListener = lister
     }

     companion object{
         @get: Synchronized
         lateinit var instance: MyApplication
     }

}