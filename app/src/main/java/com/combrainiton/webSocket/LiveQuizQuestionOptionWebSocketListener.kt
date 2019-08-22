@file:Suppress("DEPRECATION")

package com.combrainiton.webSocket

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.combrainiton.R
import com.combrainiton.models.GetCorrectOptionResponceModel
import com.combrainiton.utils.AppSharedPreference
import com.google.gson.Gson
import okhttp3.*
import okio.ByteString
import org.json.JSONObject

/**
 * Created by Dipendra Sharma  on 17-11-2018.
 */
class LiveQuizQuestionOptionWebSocketListener(var mContext: Context, var mActivity: Activity, var llProgress: RelativeLayout, private var topBarResultViewContainer: LinearLayout, var tvQuestionPoint: TextView, private var topBarQuestionResultText: TextView, private var optionTVArray: Array<TextView>) : WebSocketListener() {

    override fun onMessage(webSocket: WebSocket, text: String) {

        try {

            val msgJson = JSONObject(text)
            val questionJson = msgJson.getJSONObject("msg")
            val mQuestionModel = Gson().fromJson<GetCorrectOptionResponceModel>(questionJson.toString(), GetCorrectOptionResponceModel::class.java)
            if (!mActivity.isDestroyed) {
                mActivity.runOnUiThread {
                    startWebSocket()

                    //TODO remove this all progress bar and all of it's usage from the project
                    //llProgress.visibility = View.GONE

                    //set top bar result container visible
                    topBarResultViewContainer.visibility = View.VISIBLE

                    //get options list from question model
                    val optionList: List<GetCorrectOptionResponceModel.OptionDetail>? = mQuestionModel.question!!.options

                    //get is user answer flag from app preference
                    val isUserAnswer = AppSharedPreference(mContext).getBoolean("isUserAnswer")

                    //for each option in option list
                    for (i in optionList!!.indices) {

                        //if user gave any answer
                        if (isUserAnswer) {

                            //get user answet id from app preference
                            val userAnswer = AppSharedPreference(mContext).getInt("userAnswerId")

                            //when user answer is correct
                            if (optionList[i].option_id == userAnswer && optionList[i].is_answer) {
                                if (AppSharedPreference(mContext).getBoolean("sound")) {
                                    val mp = MediaPlayer.create(mContext, R.raw.right)
                                    mp.start()
                                }

                                //set user answer correct flag true
                                AppSharedPreference(mContext).saveBoolean("isUserAnswerCorrect", true)

                                //set top bar result text color green
                                topBarQuestionResultText.setTextColor(mContext.resources.getColor(R.color.colorCategoryFour))

                                //set top bar text to result text 2
                                topBarQuestionResultText.text = mContext.getString(R.string.top_bar_question_result_text_3)

                                //set right option background
                                setOptionBackground(R.drawable.shape_answer_right, userAnswer, optionTVArray)

                            }
                            //if the option is selected by user and not the answer
                            else if (optionList[i].option_id == userAnswer && !optionList[i].is_answer) {
                                if (AppSharedPreference(mContext).getBoolean("sound")) {
                                    val mp = MediaPlayer.create(mContext, R.raw.wrong)
                                    mp.start()
                                }
                                //save wrong answer flag
                                AppSharedPreference(mContext).saveBoolean("isUserAnswerCorrect", false)

                                //set text color to red
                                topBarQuestionResultText.setTextColor(mContext.resources.getColor(R.color.colorCategoryThree))

                                //set result text to text 2
                                topBarQuestionResultText.text = mContext.getString(R.string.top_bar_question_result_text_2)

                                //set option background to red active
                                setOptionBackground(R.drawable.shape_answer_wrong, userAnswer, optionTVArray)

                            }
                            //if the option is answer
                            else if (optionList[i].is_answer) {
                                //set background to correct answer
                                setOptionBackground(R.drawable.shape_answer_right, optionList[i].option_id, optionTVArray)

                            } //if the option is not answer and also not selected by user
                            else if (optionList[i].option_id != userAnswer && !optionList[i].is_answer) {

                                //set background to disabled red
                                setOptionBackground(R.color.colorCategoryThreeDisabled, optionList[i].option_id, optionTVArray)
                            }

                        } else {
                            //set user answer correct flag to false
                            AppSharedPreference(mContext).saveBoolean("isUserAnswerCorrect", false)
                            AppSharedPreference(mContext).saveBoolean("isUserAnswer", false)

                            //set right a answer background to correct green
                            if (optionList[i].is_answer)
                                setOptionBackground(R.drawable.shape_answer_right, optionList[i].option_id, optionTVArray)
                            else
                                setOptionBackground(R.color.colorCategoryThreeDisabled, optionList[i].option_id, optionTVArray)
                            //set top bar text color to red
                            topBarQuestionResultText.setTextColor(mContext.resources.getColor(R.color.colorCategoryThree))

                            //set top bar text to text 1
                            topBarQuestionResultText.text = mContext.getString(R.string.top_bar_question_result_text_1)

                        }
                    }
                }
            }

            //close websocket
            webSocket.close(1000, null)
        } catch (e: Exception) {

        }
    }

    private fun startWebSocket() {
        val gamePin: String = AppSharedPreference(mContext).getString("quizPin")
        val wsUrl = "ws://13.67.94.139/pin-$gamePin-question-stats/"
        val client = OkHttpClient()
        val mRequest = Request.Builder().url(wsUrl).header("Origin", wsUrl).build()
        val webLisner = LiveQuizQuestionResultWebSocketListener(mContext, mActivity)
        client.newWebSocket(mRequest, webLisner)
        client.dispatcher().executorService().shutdown()
    }

    private fun setOptionBackground(answer_bg: Int, optionId: Int, optionTVArray: Array<TextView>) {
        for (i in optionTVArray.indices) {
            val optionIdInt: Int = optionTVArray[i].tag as Int
            if (optionId == optionIdInt) {
                optionTVArray[i].background = mContext.resources.getDrawable(answer_bg)
            }
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {}

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {}

    override fun onOpen(webSocket: WebSocket, response: Response) {}

    //override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response) {}

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {}

}