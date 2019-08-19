package com.combrainiton.managers

import com.combrainiton.models.*
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Dipendra Sharma  on 23-10-2018.
 */
interface LivePollAndQuizManagementInterface {
    //for verify Ppin
    @Headers("Content-Type: application/json")
    @POST("api/verify/player/{pin}/")
    fun verifyGamePin(@Path("pin") pin: String): Call<VerifyGamePinResponceModel>

    //check user answer is correct or not
    @Headers("Content-Type: application/json")
    @POST("api/player/answer/")
    fun checkUserAnswer(@Body requestData: SendUserAnswerModel): Call<CheckUserAnswerResponceModel>

    //get Correct Answer
    @Headers("Content-Type: application/json")
    @POST("api/host/options/stats/")
    fun getCorrectOption(@Body requestData: GetCorrectOptionRequestModel): Call<GetCorrectOptionResponceModel>

    @GET("api/quiz/all/")
    fun getAllQuiz(): Call<Any>

    @Headers("Content-Type: application/json")
    @POST("api/poll/verify/player/{pin}/")
    fun verifyPollPin(@Path("pin") pin: String): Call<VerifyPollPinResponceModel>

    //for sending user poll answer
    @Headers("Content-Type: application/json")
    @POST("api/poll/player/answer/")
    fun sendPollUserAnswer(@Body sendData: SendPollUserAnswerRequestModel): Call<SendPollUserAnswerResponceModel>

}