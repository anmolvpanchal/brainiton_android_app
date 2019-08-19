package com.combrainiton.managers


import com.combrainiton.models.CommonResponceModel
import com.combrainiton.models.VerifyUserResponceModel
import com.combrainiton.models.UserResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by Dipendra Sharma  on 27-09-2018.
 */

interface UserManagementInterface {
    //for getRegistration  OTP
    @Headers("Content-Type: application/json")
    @POST("api/user/signup/")
    fun getRegistrationOTP(@Body requestData: Map<String, String>): Call<UserResponseModel>

    //for getLoginOTP
    @Headers("Content-Type: application/json")
    @POST("api/user/login/")
    fun getLoginOTP(@Body requestData: Map<String, String>): Call<UserResponseModel>

    //for get login otp in case of other device login
    @Headers("Content-Type: application/json")
    @POST("api/user/login/other/device/")
    fun getLoginOTPForOtherDevice(@Body requestData: Map<String, String>): Call<UserResponseModel>

    //for verify registration OTP
    @Headers("Content-Type: application/json")
    @POST("api/user/verify/")
    fun verifyRegistrationOTP(@Body requestData: Map<String, String>): Call<VerifyUserResponceModel>

    //for verify Login OTP
    @Headers("Content-Type: application/json")
    @POST("api/user/login/verify/ ")
    fun verifyLoginOTP(@Body requestData: Map<String, String>): Call<VerifyUserResponceModel>


    //for signIn
    @Headers("Content-Type: application/json")
    @POST("user/login")
    fun sendLoginData(@Body requestData: Map<String, String>): Call<UserResponseModel>

    //for logout
    @Headers("Content-Type: application/json")
    @POST("api/user/logout/")
    fun doLogOut(): Call<CommonResponceModel>

    //for getting user detail
    @Headers("Content-Type: application/json")
    @GET("api/user/name/")
    fun getUserDetail(): Call<UserResponseModel>
}