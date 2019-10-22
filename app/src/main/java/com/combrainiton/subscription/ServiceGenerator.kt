package com.combrainiton.subscription

import android.os.Build
import com.combrainiton.BuildConfig
import com.combrainiton.api.ApiClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ServiceGenerator {

    internal var BASEURL = "http://13.233.130.106:8000/"
    private val builder = Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())

    fun <S> createService(serviceClass: Class<S>): S {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addNetworkInterceptor(logging)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            httpClient.readTimeout(10, TimeUnit.MINUTES).connectTimeout(10, TimeUnit.MINUTES)
                    .writeTimeout(10, TimeUnit.MINUTES)
        }// <-- this is the important line!

        val retrofit = builder.client(httpClient.build()).build()
        return retrofit.create(serviceClass)
    }

    //this method will use in case of when user will go for authorize api
    fun getClient(apiToken: String): Retrofit {

        val mGson: Gson = GsonBuilder()
                .setLenient()
                .create()
        val mHttpLoginInterceptor = HttpLoggingInterceptor()
        //here we will set our log level
        mHttpLoginInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val mOkClient: OkHttpClient.Builder = OkHttpClient.Builder().readTimeout(300,
                TimeUnit.SECONDS).writeTimeout(300, TimeUnit.SECONDS).connectTimeout(300, TimeUnit.SECONDS)
        // add logging as last interceptor always
        // we will add login interceptor only in case of debug.
        //here firstly we will add our header interseptor
        mOkClient.addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", apiToken)
                    .build()
            chain.proceed(newRequest)
        }

        if (BuildConfig.DEBUG) {
            mOkClient.addInterceptor(mHttpLoginInterceptor)
        }

        return Retrofit.Builder()
                .baseUrl(BASEURL)
                .client(mOkClient.build())
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .build()
    }


}