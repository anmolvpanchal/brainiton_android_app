package com.combrainiton.subscription

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface SubscriptionInterface {

    //Getting all brands
    @GET("api/subs/get_all_brands/")
    fun getAllBrands(): Call<BrandsResponseModel>

    //Getting all courses for the brand whose id has been passed
    @GET("api/subs/brand/{brandID}/view/")
    fun getAllCourses(@Path("brandID") brandId: Int): Call<CoursesResponseModel>

    // getting all subscribed cources must be logged in
    @GET("api/subs/get_my_subs/")
    fun getMysubscriptions(): Call<ResponseBody>

    // subscribing with code
    @Headers("Content-Type: application/json")
    @POST("api/subs/subscribe/")
    fun subscribeWithCode(@Body requestData: Map<String, String>): Call<ResponseBody>

    // getting details of lessons for fragments
    @GET("api/subs/subscription/{subscriptionID}/view/")
    fun getAllLessons (@Path("subscriptionID")subscriptionID : Int) : Call<ResponseBody>

    // getting quiz id from lesson id
    @GET("api/subs/lesson/{lessonID}/play/")
    fun getQuizID (@Path("lessonID") lessonID : Int) : Call<ResponseBody>




}