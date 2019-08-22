package com.combrainiton.webSocket

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.combrainiton.livePoll.ActivityLivePollQuestion
import com.combrainiton.livePoll.ActivityLivePollResult
import com.combrainiton.models.PollAllQuestionListResponceModel
import com.combrainiton.models.QuestionResponceModel
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject

/**
 * Created by Dipendra Sharma  on 27-11-2018.
 */
class PollQuestionWebSocketLisner(var mContext: Context, var mActivity: Activity) : WebSocketListener() {


    override fun onOpen(webSocket: WebSocket, response: Response) {


        Log.v("dip", "inside open web socket :" + response.message())

    }

    /*override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response) {
        //super.onFailure(webSocket, t, response)
        Log.v("dip", "inside fail :" + response.message() + t.message)
    }*/

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        //super.onClosing(webSocket, code, reason)
        Log.v("dip", "inside close :$code$reason")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        //super.onMessage(webSocket, text)
        try {


            val msgJson = JSONObject(text)
            val questionJson = msgJson.getJSONObject("msg")
            val isPollEnded: Boolean = questionJson.getBoolean("poll_ended")
            //val isPollEnded:Boolean=false
            Log.v("dip", "inside on msg text :$questionJson")

            mActivity.runOnUiThread {
                if (isPollEnded) {
                    val mQuestionModel = Gson().fromJson<PollAllQuestionListResponceModel>(questionJson.toString(), PollAllQuestionListResponceModel::class.java)
                    mActivity.startActivity(Intent(mContext, ActivityLivePollResult::class.java)
                            .putExtra("result", mQuestionModel))
                    mActivity.finish()

                } else {
                    val mQuestionModel = Gson().fromJson<QuestionResponceModel>(questionJson.toString(), QuestionResponceModel::class.java)
                    mActivity.startActivity(Intent(mContext, ActivityLivePollQuestion::class.java)
                            .putExtra("question", mQuestionModel))
                    mActivity.finish()
                }
                webSocket.close(1000, null)
            }

        } catch (e: Exception) {

            Log.v("dip", "inside catch of json convertor :" + e.message)
            val msgJson = JSONObject(text)
            val questionJson = msgJson.getJSONObject("msg")

            val mQuestionModel = Gson().fromJson<QuestionResponceModel>(questionJson.toString(), QuestionResponceModel::class.java)
            mActivity.startActivity(Intent(mContext, ActivityLivePollQuestion::class.java)
                    .putExtra("question", mQuestionModel))
            mActivity.finish()
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        //super.onMessage(webSocket, bytes)
        Log.v("dip", "inside on msg bytes :$bytes")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        //super.onClosed(webSocket, code, reason)
        Log.v("dip", "inside close :")
    }
}