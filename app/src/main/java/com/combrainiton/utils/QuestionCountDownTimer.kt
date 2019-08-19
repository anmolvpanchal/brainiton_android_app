package com.combrainiton.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import com.combrainiton.R


/**
 * Created by RonakJain on 10-12-2016.
 */

@SuppressLint("ResourceAsColor")
class QuestionCountDownTimer : CountDownTimer {

    var mContext: Context
    var mActivity: Activity

    private lateinit var timerTxt: TextView
    private lateinit var progressBarStart: ProgressBar
    private lateinit var progressBarEnd: ProgressBar
    private lateinit var progressBarQuiz: ProgressBar
    private lateinit var optionTVArray: Array<TextView>
    private lateinit var resultTxt: TextView
    private var quizTime: Float = 0F
    private var quizTimeTick: Float = 0F


    constructor(startTime: Long, interval: Long, context: Context, activity: Activity) : super(startTime, interval) {
        this.mContext = context
        this.mActivity = activity
        Log.v("akram", "ontick------ constrotor")
    }


    constructor(millisInFuture: Long, countDownInterval: Long, mContext: Context, mActivity: Activity, progressBarStart: ProgressBar, progressBarEnd: ProgressBar, timerTxt: TextView, optionTVArray: Array<TextView>) : super(millisInFuture, countDownInterval) {
        this.mContext = mContext
        this.mActivity = mActivity
        this.progressBarStart = progressBarStart
        this.progressBarEnd = progressBarEnd
        this.timerTxt = timerTxt
        this.optionTVArray = optionTVArray
    }

    constructor(millisInFuture: Long, countDownInterval: Long, mContext: Context, mActivity: Activity, progressBarStart: ProgressBar, timerTxt: TextView, optionTVArray: Array<TextView>, resultTxt: TextView) : super(millisInFuture, countDownInterval) {
        this.mContext = mContext
        this.mActivity = mActivity
        this.progressBarStart = progressBarStart
        this.timerTxt = timerTxt
        this.optionTVArray = optionTVArray
        quizTime = (millisInFuture / 1000).toFloat()
        this.resultTxt = resultTxt
    }

    constructor(millisInFuture: Long, countDownInterval: Long, mContext: Context, mActivity: Activity, progressBarStart: ProgressBar, timerTxt: TextView, optionTVArray: Array<TextView>) : super(millisInFuture, countDownInterval) {
        this.mContext = mContext
        this.mActivity = mActivity
        this.progressBarStart = progressBarStart
        this.timerTxt = timerTxt
        this.optionTVArray = optionTVArray
        quizTime = (millisInFuture / 1000).toFloat()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    override fun onFinish() {

    }

    @SuppressLint("SetTextI18n")
    override fun onTick(millisUntilFinished: Long) {
        timeElapsed = millisUntilFinished / 1000
        AppSharedPreference(mContext).saveInt("timeRemaning", millisUntilFinished.toInt())
        AppSharedPreference(mContext).saveString("Livescore", (500 * ((quizTime - quizTimeTick) / quizTime)).toString())
        System.out.println(quizTime)
        System.out.println(quizTimeTick)
        quizTimeTick += 1
        System.out.println((500 * ((quizTime - quizTimeTick) / quizTime)).toString())
        timerTxt.text = "  " + String.format("%02d", timeElapsed % 60)
    }

    companion object {
        var timeElapsed: Long = 0
    }

}