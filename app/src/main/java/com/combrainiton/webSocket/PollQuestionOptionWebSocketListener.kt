package com.combrainiton.webSocket

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.combrainiton.models.GetPollOptionStatisticsResponceModel
import com.combrainiton.utils.AppSharedPreference
import com.google.gson.Gson
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.view.ColumnChartView
import okhttp3.*
import okio.ByteString
import org.json.JSONObject

/*
 * Created by Dipendra Sharma  on 26-11-2018.
 */

@Suppress("DEPRECATION", "PrivatePropertyName")
class PollQuestionOptionWebSocketListener(var mContext: Context, var mActivity: Activity, private var llProgress: RelativeLayout, private var llChat: RelativeLayout, private var optionChat: ColumnChartView) : WebSocketListener() {

    private val DEFAULT_DATA = 0
    private val COLOR_ONE = Color.parseColor("#9c28b1")
    private val COLOR_TWO = Color.parseColor("#08a8f4")
    private val COLOR_THREE = Color.parseColor("#ff5523")
    private val COLOR_FOUR = Color.parseColor("#fc9900")
    private val COLORS = intArrayOf(COLOR_ONE, COLOR_TWO, COLOR_THREE, COLOR_FOUR)

    private var data: ColumnChartData? = null
    private var hasAxes = true
    private var hasAxesNames = true

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.v("dip", "inside option open web socket :" + response.message())
    }

    /*override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response) {
        Log.v("dip", "inside option fail :" + response.message() + t.message)
    }*/

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.v("dip", "inside option close :$code$reason")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            val msgJson = JSONObject(text)
            val questionJson = msgJson.getJSONObject("msg")
            Log.v("dip", "inside option on msg text :$msgJson")
            val mQuestionModel = Gson().fromJson<GetPollOptionStatisticsResponceModel>(questionJson.toString(), GetPollOptionStatisticsResponceModel::class.java)
            mActivity.runOnUiThread {
                startWebSocket()
                llProgress.visibility = View.GONE
                llChat.visibility = View.VISIBLE
                generateData(mQuestionModel.question!!.options!!)
            }

            webSocket.close(1000, null)
        } catch (e: Exception) {
            Log.v("dip", "inside catch of json convertor :" + e.message)

        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        //super.onMessage(webSocket, bytes)
        Log.v("dip", "inside on msg bytes :$bytes")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.v("dip", "inside close :")
    }

    private fun startWebSocket() {
        val gamePin: String = AppSharedPreference(mContext).getString("pollPin")
        val wsUrl = "ws://13.67.94.139/pin-$gamePin/"
        val client = OkHttpClient()
        val mRequest = Request.Builder().url(wsUrl).header("Origin", wsUrl).build()
        val webListener = PollQuestionWebSocketLisner(mContext, mActivity)
        client.newWebSocket(mRequest, webListener)
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

    private fun generateData(mOptionList: ArrayList<GetPollOptionStatisticsResponceModel.OptionList>) {
        optionChat.isZoomEnabled = false
        hasAxes = false
        hasAxesNames = true
        generateDefaultData(mOptionList)
    }

    private fun generateDefaultData(mOptionList: ArrayList<GetPollOptionStatisticsResponceModel.OptionList>) {
        val numSubcolumns = 1
        val numColumns = 4
        val columns = ArrayList<Column>()
        var values: MutableList<SubcolumnValue>
        for (i in 0 until numColumns) {

            values = ArrayList()
            for (j in 0 until numSubcolumns) {
                values.add(SubcolumnValue(mOptionList[i].option_count, COLORS[i]))
            }

            val column = Column(values)
            column.setHasLabels(true)
            columns.add(column)
        }

        data = ColumnChartData(columns)

        if (hasAxes) {
            val axisX = Axis()
            val axisY = Axis().setHasLines(true)
            if (hasAxesNames) {
                axisX.name = "Axis X"
                axisY.name = "Axis Y"
            }
            data!!.axisXBottom = axisX
            data!!.axisYLeft = axisY
        } else {
            data!!.axisXBottom = null
            data!!.axisYLeft = null
        }

        optionChat.columnChartData = data

    }

}