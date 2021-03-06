package com.combrainiton.subscription

import com.combrainiton.models.GetNormalQuestionListResponceModel
import com.combrainiton.models.UserResponseModel
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

    // getting all subscribed courses. Must be logged in
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

    // getting data for result from quiz id
    @GET("api/quiz/results/{quizid}/")
    fun getDataForResult (@Path("quizid") lessonID : Int) : Call<ResponseBody>

    //for getting single quiz detail.
    @GET("api/quiz/detail/{quizid}/")
    fun getQuizDetailForSubs(@Path("quizid") quizId: Int): Call<ResponseBody>


    @GET("api/subs/course/{courseid}/view/")
    fun getLessonsForAvailableSubs(@Path("courseid") quizId: Int): Call<ResponseBody>

    //Getting CHECKSUMHASH for Paytm
    @Headers("Content-Type: application/json")
    @POST("api/subs/subscribe/checksum/make/")
    fun getCheckSumHash(@Body type: MutableMap<String, String>): Call<ResponseBody>


    //for getting user detail
    @GET("api/user/name/")
    fun getUserDetail(): Call<ResponseBody>


    //for getting question list by quiz id
    @GET("api/quiz/{quizId}/play/")
    fun LearnOption(@Path("quizId") quzId: Int): Call<ResponseBody>

}