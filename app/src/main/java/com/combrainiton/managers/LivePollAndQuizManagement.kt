package com.combrainiton.managers


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.combrainiton.liveQuiz.ActivityLiveQuizInstruction
import com.combrainiton.authentication.ActivitySignIn
import com.combrainiton.R
import com.combrainiton.api.ApiClient
import com.combrainiton.api.ApiErrorParser
import com.combrainiton.models.*
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Dipendra Sharma  on 23-10-2018.
 */
class LivePollAndQuizManagement(var mContext: Context, var mActivity: Activity, private var mProgressDialog: AppProgressDialog, requestDataMap: Map<String, String>) {

    private var requestDataMap: Map<String, String> = HashMap()
    private var requestInterface: LivePollAndQuizManagementInterface? = null

    init {
        this.requestDataMap = requestDataMap
    }

    constructor(mContext: Context, mActivity: Activity, mProgressDialog: AppProgressDialog) : this(mContext, mActivity, mProgressDialog, HashMap())

    //for verify game pin
    fun verifyQuizPin(gamePin: String) {

        //get api client first
        val apiToken: String = AppSharedPreference(mContext).getString("apiToken")

        //initialize live poll quiz management interface
        requestInterface = ApiClient.getClient(apiToken).create(LivePollAndQuizManagementInterface::class.java)

        //attach your call to your get method
        val pinCall: Call<VerifyGamePinResponceModel>? = requestInterface!!.verifyGamePin(gamePin)

        //request for data
        pinCall!!.enqueue(object : Callback<VerifyGamePinResponceModel> {

            //on request fail
            override fun onFailure(call: Call<VerifyGamePinResponceModel>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                //display error message
                AppAlerts().showAlertMessage(mContext, "Error", mContext.resources.getString(R.string.error_server_problem))
            }

            //on response recieved
            override fun onResponse(call: Call<VerifyGamePinResponceModel>, response: Response<VerifyGamePinResponceModel>) {

                //mProgressDialog.dialog.dismiss()

                //if repsonse is successful
                if (response.isSuccessful) {

                    //display message
                    Toast.makeText(mContext, response.body()!!.message, Toast.LENGTH_LONG).show()

                    //store quiz id
                    AppSharedPreference(mContext).saveInt("quizId", response.body()!!.quiz_id)

                    //store quiz pin
                    AppSharedPreference(mContext).saveString("quizPin", gamePin)

                    //store total number of questions
                    AppSharedPreference(mContext).saveInt("totalQuestion", response.body()!!.total_questions)

                    //set poll flag false
                    AppSharedPreference(mContext).saveBoolean("isPoll", false)

                    //start instruction activity
                    mActivity.startActivity(Intent(mContext, ActivityLiveQuizInstruction::class.java)
                            .putExtra("quizName", response.body()!!.quiz_name)//add quiz name
                            .putExtra("totalQuestion", response.body()!!.total_questions)//add total number of question
                            .putExtra("quizId", response.body()!!.quiz_id)//add quiz id
                            .putExtra("gamePin", gamePin)//add game pin
                            .putExtra("quizImage", response.body()!!.image_url))//add image link

                    //finish current activity
                    mActivity.finish()

                } else {
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)
                }
            }
        })
    }

    //for checking user answer is correct or not
    fun checkUserAnswer(mRequestMap: SendUserAnswerModel) {
        val apiToken: String = AppSharedPreference(mContext).getString("apiToken")
        requestInterface = ApiClient.getClient(apiToken).create(LivePollAndQuizManagementInterface::class.java)
        val checkAnswerCall: Call<CheckUserAnswerResponceModel>? = requestInterface!!.checkUserAnswer(mRequestMap)
        checkAnswerCall!!.enqueue(object : Callback<CheckUserAnswerResponceModel> {
            override fun onFailure(call: Call<CheckUserAnswerResponceModel>, t: Throwable) {
                ////mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(mContext, "Error", mContext.resources.getString(R.string.error_server_problem))

            }

            override fun onResponse(call: Call<CheckUserAnswerResponceModel>, response: Response<CheckUserAnswerResponceModel>) {
                if (response.isSuccessful) {
                    AppSharedPreference(mContext).saveInt("userAnswerId", mRequestMap.option_id)
                    AppSharedPreference(mContext).saveBoolean("isUserAnswer", true)
                } else {
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)
                }
            }
        })
    }

    //this method is used to verify user enterd poll pin
    fun verifyPollPin(pollPin: String) {
        val apiToken: String = AppSharedPreference(mContext).getString("apiToken")
        requestInterface = ApiClient.getClient(apiToken).create(LivePollAndQuizManagementInterface::class.java)
        val pinCall: Call<VerifyPollPinResponceModel>? = requestInterface!!.verifyPollPin(pollPin)
        pinCall!!.enqueue(object : Callback<VerifyPollPinResponceModel> {
            override fun onFailure(call: Call<VerifyPollPinResponceModel>, t: Throwable) {
                ////mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(mContext, "Error", mContext.resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<VerifyPollPinResponceModel>, response: Response<VerifyPollPinResponceModel>) {
                ////mProgressDialog.dialog.dismiss()
                if (response.isSuccessful) {
                    Toast.makeText(mContext, response.body()!!.message, Toast.LENGTH_LONG).show()
                    AppSharedPreference(mContext).saveInt("pollId", response.body()!!.poll_id)
                    AppSharedPreference(mContext).saveString("pollPin", pollPin)
                    AppSharedPreference(mContext).saveInt("totalQuestion", response.body()!!.total_questions)
                    AppSharedPreference(mContext).saveBoolean("isPoll", true)
                    mActivity.startActivity(Intent(mContext, ActivityLiveQuizInstruction::class.java)
                            .putExtra("quizName", response.body()!!.poll_name)
                            .putExtra("totalQuestion", response.body()!!.total_questions)
                            .putExtra("quizId", response.body()!!.poll_id)
                            .putExtra("gamePin", pollPin))
                    mActivity.finish()

                } else {
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)

                }
            }


        })

    }

    //for sending user poll answer
    fun sendPollUserAnswer(sendData: SendPollUserAnswerRequestModel) {
        val apiToken: String = AppSharedPreference(mContext).getString("apiToken")
        requestInterface = ApiClient.getClient(apiToken).create(LivePollAndQuizManagementInterface::class.java)
        val sendAnswerCall = requestInterface!!.sendPollUserAnswer(sendData)
        sendAnswerCall.enqueue(object : Callback<SendPollUserAnswerResponceModel> {
            override fun onFailure(call: Call<SendPollUserAnswerResponceModel>, t: Throwable) {
                ////mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(mContext, "Error", t.message!!)

            }

            override fun onResponse(call: Call<SendPollUserAnswerResponceModel>, response: Response<SendPollUserAnswerResponceModel>) {

                ////mProgressDialog.dialog.dismiss()
                if (response.isSuccessful) {
                    Toast.makeText(mContext, "Answer submitted", Toast.LENGTH_LONG).show()

                } else {
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)


                }
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