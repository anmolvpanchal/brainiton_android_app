package com.combrainiton.api

import com.combrainiton.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Dipendra Sharma  on 27-09-2018.
 */
class ApiClient {

    companion object {

        private const val BASE_URL: String = "http://13.67.94.139/"
        //val BASE_URL: String = "http://52.187.71.94/" //test

        //create authentication with backend using shake hand method
        fun getClient(): Retrofit {
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

            if (BuildConfig.DEBUG) {
                mOkClient.addInterceptor(mHttpLoginInterceptor)
            }

            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(mOkClient.build())
                    .addConverterFactory(GsonConverterFactory.create(mGson))
                    .build()
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
                    .baseUrl(BASE_URL)
                    .client(mOkClient.build())
                    .addConverterFactory(GsonConverterFactory.create(mGson))
                    .build()
        }

        //this method will use for in case when header key & value will be dyanamic
        fun getClient(headerKey: String, headerValue: String): Retrofit {

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
                        .addHeader(headerKey, headerValue)
                        .build()
                chain.proceed(newRequest)
            }
            if (BuildConfig.DEBUG) {
                mOkClient.addInterceptor(mHttpLoginInterceptor)
            }

            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(mOkClient.build())
                    .addConverterFactory(GsonConverterFactory.create(mGson))
                    .build()
        }

    }
}