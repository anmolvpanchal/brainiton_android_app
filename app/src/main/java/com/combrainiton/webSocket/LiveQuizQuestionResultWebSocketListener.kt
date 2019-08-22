package com.combrainiton.webSocket

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.combrainiton.liveQuiz.ActivityLiveQuizQuestionResult
import com.combrainiton.liveQuiz.ActivityLiveQuizResult
import com.combrainiton.models.SingleQuestionLeaderBoardResponceModel
import com.combrainiton.utils.AppSharedPreference
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject

/**
 * Created by Dipendra Sharma  on 17-11-2018.
 */
class LiveQuizQuestionResultWebSocketListener(var mContext: Context, var mActivity: Activity) : WebSocketListener() {

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            val msgJson = JSONObject(text)
            val userJson = msgJson.getJSONObject("msg")
            val mUserModel = Gson().fromJson<SingleQuestionLeaderBoardResponceModel>(userJson.toString(), SingleQuestionLeaderBoardResponceModel::class.java)
            if (!mActivity.isDestroyed) {
                mActivity.runOnUiThread {

                    //get total question count from app preference
                    val totalQuestion: Int = AppSharedPreference(mContext).getInt("totalQuestion")

                    //get current question from app preference
                    val currentQuestion: Int = AppSharedPreference(mContext).getInt("currentQuestion")

                    //if current question is last question
                    if (totalQuestion == currentQuestion) {

                        //reset current question counter to 1
                        AppSharedPreference(mContext).saveInt("currentQuestion", 1)

                        //start quiz result activity
                        mActivity.startActivity(Intent(mContext, ActivityLiveQuizResult::class.java)
                                .putExtra("leaderBoard", mUserModel))//add leader board data

                        //finish current activity
                        mActivity.finish()

                    }
                    //if current question is not last question
                    else {

                        //start question result activtiy
                        mActivity.startActivity(Intent(mContext, ActivityLiveQuizQuestionResult::class.java)
                                .putExtra("leaderBoard", mUserModel))//add leader board data

                        //finish current activity
                        mActivity.finish()
                    }
                }
            }

            //close the web socket
            webSocket.close(1000, null)

        } catch (e: Exception) {

        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {}

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {}

    override fun onOpen(webSocket: WebSocket, response: Response) {}

    //override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response) {}

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {}

}