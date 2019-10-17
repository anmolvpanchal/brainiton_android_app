@file:Suppress("DEPRECATION")

package com.combrainiton.managers


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.api.ApiClient
import com.combrainiton.api.ApiErrorParser
import com.combrainiton.authentication.ActivityOTPVerification
import com.combrainiton.authentication.ActivitySignIn
import com.combrainiton.authentication.ActivityWelcomeUser
import com.combrainiton.models.CommonResponceModel
import com.combrainiton.models.UserResponseModel
import com.combrainiton.models.VerifyUserResponceModel
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Dipendra Sharma  on 27-09-2018.
 */

class UserManagement(var mContext: Context, var mActivity: Activity, var mProgressDialog: AppProgressDialog, requestDataMap: Map<String, String>) {

    var requestDataMap: Map<String, String> = HashMap()

    private var requestInterface: UserManagementInterface? = null

    init {
        this.requestDataMap = requestDataMap
    }

    constructor(mContext: Context, mActivity: Activity, mProgressDialog: AppProgressDialog) : this(mContext, mActivity, mProgressDialog, HashMap())

    //for getting otp in case of registration & login
    fun sendOTPRequest(fromStr: String, progressBar: ProgressBar) {

        //create call object
        val registrationCall: Call<UserResponseModel>

        when (fromStr) {

            "signUp" -> { //when user came from sign up activity

                //create client using shake hand method
                requestInterface = ApiClient.getClient().create(UserManagementInterface::class.java)

                //attach your call object to your get method
                registrationCall = requestInterface!!.getRegistrationOTP(requestDataMap)

            }
            "signIn" -> { //when user came from sign in activtiy

                //create client using header values
                requestInterface = ApiClient.getClient("okhttp", "okhttp").create(UserManagementInterface::class.java)

                //attach your call object to your get method
                registrationCall = requestInterface!!.getLoginOTP(requestDataMap)

            }
            else -> { //when user came from some other activity

                //create client using shake hand method
                requestInterface = ApiClient.getClient().create(UserManagementInterface::class.java)

                //attach your call object to your get method
                registrationCall = requestInterface!!.getLoginOTPForOtherDevice(requestDataMap)

            }

        }

        //request for data
        registrationCall.enqueue(object : Callback<UserResponseModel> {

            //on response recieved
            override fun onResponse(call: Call<UserResponseModel>, response: Response<UserResponseModel>) {

                //mProgressDialog.dialog.dismiss()
                progressBar.visibility = View.GONE

                //allow user to internet removing flags
                mActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                //if response if successful
                if (response.isSuccessful) {
                    //get string user_id from response data and store it app preference
                    AppSharedPreference(mContext).saveString("mobile_num", requestDataMap["user_id"]
                            ?: error(""))

                    //get JSON Object status value from response
                    val status: Int = response.body()!!.status

                    //if status equal to 200
                    if (status == 200) {

                        //create intent to open activity otp verification
                        val nextIntent = Intent(mContext, ActivityOTPVerification::class.java)

                        //add session id from JSON data into intent data
                        nextIntent.putExtra("session_id", response.body()!!.session_id)

                        //if from string equals to signUp
                        if (fromStr == "signUp") {

                            //store user name to app preferences
                            AppSharedPreference(mContext).saveString("name", requestDataMap["name"]
                                    ?: error(""))

                            //add user name in intent data
                            nextIntent.putExtra("name", requestDataMap["name"])
                        }

                        //add user in intent data
                        nextIntent.putExtra("mobile", requestDataMap["user_id"])

                        //pass from string value in intent data
                        nextIntent.putExtra("from", fromStr)

                        //start activity
                        mActivity.startActivity(nextIntent)

                        //finish current activity
                        mActivity.finish()
                    } else {
                        //show error message
                        AppAlerts().showAlertMessage(mContext, "Error", mContext.getString(R.string.error_server_problem))
                    }
                } else {
                    //parse error
                    val errorMsgModel: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    //if error message equals to 405
                    if (errorMsgModel.status == 405) {
                        //show log out alert
                        showLogoutAlert(errorMsgModel.message,progressBar)
                    } else {
                        //show other message
                        if (errorMsgModel.message.isNotEmpty())
                            AppAlerts().showAlertMessage(mContext, "Error", errorMsgModel.message)
                        else
                            AppAlerts().showAlertMessage(mContext, "Error", "Your internet connection is low \n Please try again")
                    }
                }
            }

            //on request fails
            override fun onFailure(call: Call<UserResponseModel>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                Log.d("dip", "" + t)
                AppAlerts().showAlertMessage(mContext, "Error", mContext.resources.getString(R.string.error_server_problem))
            }
        })
    }

     //shows log out alert dialog box
     private fun showLogoutAlert(message: String,progressBar: ProgressBar) {
         progressBar.visibility = View.VISIBLE
         AppAlerts().showAlertWithAction(mContext, "Info", message, "Yes", "No", DialogInterface.OnClickListener { _, _ -> sendOTPRequest("other",progressBar) }, true)
     }

    //for verify otp in case of registration & login
    fun verifyOTP(fromStr: String, etOTP: EditText, tvEnterValidOtp: TextView) {

        //intialize the user management interface
        requestInterface = ApiClient.getClient().create(UserManagementInterface::class.java)

        //attach your call to get method
        val verifyOTPCall: Call<VerifyUserResponceModel> = if (fromStr == "signUp") { //if from string equals to signup
            //attach your call to registration method
            requestInterface!!.verifyRegistrationOTP(requestDataMap)
        } else { //if not
            //attach your call to login method
            requestInterface!!.verifyLoginOTP(requestDataMap)
        }

        //request for data
        verifyOTPCall.enqueue(object : Callback<VerifyUserResponceModel> {

            //on response receieved
            override fun onResponse(call: Call<VerifyUserResponceModel>, response: Response<VerifyUserResponceModel>) {

                //mProgressDialog.dialog.dismiss()

                //if response if successful
                if (response.isSuccessful) {

                    //get JSON Object api token from response
                    val apiToken: String = response.body()!!.api_token

                    //get JSON Object username from response
                    val username: String = response.body()!!.username

                    //store apitoken in app prefernce
                    AppSharedPreference(mContext).saveString("apiToken", apiToken)

                    //store username in app preference
                    AppSharedPreference(mContext).saveString("name", username)

                    //set islogin flag to true in app preference
                    AppSharedPreference(mContext).saveBoolean("isLogin", true)

                    //start welcome user activity
                    mActivity.startActivity(Intent(mContext, ActivityWelcomeUser::class.java))

                    //finish current activity
                    mActivity.finish()
                } else {
                    //parse the error
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    etOTP.background = mContext.resources.getDrawable(R.drawable.shape_rounded_edit_text)
                    tvEnterValidOtp.visibility = View.VISIBLE
                    AppAlerts().showAlertMessage(mContext, "Error", errorMsgModle.message)

                }

            }

            override fun onFailure(call: Call<VerifyUserResponceModel>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(mContext, "Error", mContext.resources.getString(R.string.error_server_problem))
            }

        })


    }

    //for LogOut
    fun doLogout() {

        val apiToken: String = AppSharedPreference(mContext).getString("apiToken")
        requestInterface = ApiClient.getClient(apiToken).create(UserManagementInterface::class.java)
        val logOutCall = requestInterface!!.doLogOut()

        logOutCall.enqueue(object : Callback<CommonResponceModel> {
            override fun onFailure(call: Call<CommonResponceModel>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(mContext, "Error", mContext.resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<CommonResponceModel>, response: Response<CommonResponceModel>) {
                //mProgressDialog.dialog.dismiss()
                if (response.isSuccessful) {
                    //AppAlerts().showAlertMessage(mContext,"Info", response.body()!!.message)
                    AppSharedPreference(mContext).deleteSharedPreference()
                    Toast.makeText(mContext, response.body()!!.message, Toast.LENGTH_LONG).show()
                    mActivity.startActivity(Intent(mContext, ActivitySignIn::class.java)
                            .putExtra("from", "splesh"))
                    mActivity.finish()

                } else {
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)
                    //AppAlerts().showAlertMessage(mContext,"Error", errorMsgModle.message)
                }
            }

        })
    }

    //for getting user detail
    fun getUserDetail(tvMobile: TextView, tvName: TextView) {
        val apiToken: String = AppSharedPreference(mContext).getString("apiToken")
        requestInterface = ApiClient.getClient(apiToken).create(UserManagementInterface::class.java)
        val getDetailCall = requestInterface!!.getUserDetail()
        getDetailCall.enqueue(object : Callback<UserResponseModel> {
            override fun onResponse(call: Call<UserResponseModel>, response: Response<UserResponseModel>) {
                //mProgressDialog.dialog.dismiss()
                if (response.isSuccessful) {
                    tvMobile.text = response.body()!!.mobile
                    tvName.text = response.body()!!.message
                } else {
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)
                }
            }

            override fun onFailure(call: Call<UserResponseModel>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                //display error message
                AppAlerts().showAlertMessage(mContext, "Error", mContext.getString(R.string.error_server_problem))
            }
        })
    }

    //this method is called to check use login status
    private fun isSessionExpire(errorMsgModle: CommonResponceModel) {
        //if error message status is equal to 404
        if (errorMsgModle.status == 404) {

            //show the message to user
            Toast.makeText(mContext, "Your Session Is Expire. Login Again For Continue.", Toast.LENGTH_LONG).show()

            //delete all shared preferences
            AppSharedPreference(mContext).deleteSharedPreference()

            //start login activity
            mActivity.startActivity(Intent(mContext, ActivitySignIn::class.java)
                    .putExtra("from", "fail"))//add set from key value to "fail"

            //finish current activity
            mActivity.finish()

        } else {
            //display other message
            AppAlerts().showAlertMessage(mContext, "Error", errorMsgModle.message)
        }
    }
}