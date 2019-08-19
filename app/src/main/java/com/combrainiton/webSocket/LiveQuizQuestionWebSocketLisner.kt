package com.combrainiton.webSocket

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.combrainiton.livePoll.ActivityLivePollQuestion
import com.combrainiton.liveQuiz.ActivityLiveQuizQuestion
import com.combrainiton.models.QuestionResponceModel
import com.combrainiton.utils.AppSharedPreference
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject

/**
 * Created by Dipendra Sharma  on 23-10-2018.
 */
class LiveQuizQuestionWebSocketLisner(var mContext: Context, var mActivity: Activity) : WebSocketListener() {

    override fun onMessage(webSocket: WebSocket, text: String) {

        try {
            val msgJson = JSONObject(text)
            val questionJson = msgJson.getJSONObject("msg")
            val mQuestionModel = Gson().fromJson<QuestionResponceModel>(questionJson.toString(), QuestionResponceModel::class.java)

            mActivity.runOnUiThread {

                //get is poll flag from app preference
                val isPoll = AppSharedPreference(mContext).getBoolean("isPoll")

                //if it is poll
                if (isPoll) {

                    //start poll activity
                    mActivity.startActivity(Intent(mContext, ActivityLivePollQuestion::class.java)
                            .putExtra("question", mQuestionModel)) //add question model to intent

                    //finish current activity
                    mActivity.finish()
                }
                //if it is not poll
                else {
                    //start live quiz activtiy
                    mActivity.startActivity(Intent(mContext, ActivityLiveQuizQuestion::class.java)
                            .putExtra("question", mQuestionModel)) //add question model
                    //finish current activity
                    mActivity.finish()
                }

                //close websocket
                webSocket.close(1000, null)
            }

        } catch (e: Exception) {

        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {}

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {}

    override fun onOpen(webSocket: WebSocket, response: Response) {}

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response) {}

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {}

}