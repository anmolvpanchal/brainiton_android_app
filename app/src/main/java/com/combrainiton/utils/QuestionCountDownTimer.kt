package com.combrainiton.utils

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator
import android.widget.TextClock
import com.combrainiton.R
import com.combrainiton.models.QuestionResponceModel
import kotlinx.android.synthetic.main.activity_quiz_question.*


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
    //DHRUVAM
    private lateinit var progress: ProgressBar
    private lateinit var progressAnimate: ObjectAnimator
    //END
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

    /*constructor(millisInFuture: Long, countDownInterval: Long, mContext: Context, mActivity: Activity, progressBarStart: ProgressBar, timerTxt: TextView, optionTVArray: Array<TextView>) : super(millisInFuture, countDownInterval) {
        this.mContext = mContext
        this.mActivity = mActivity
        this.progressBarStart = progressBarStart
        this.timerTxt = timerTxt
        this.optionTVArray = optionTVArray
        quizTime = (millisInFuture / 1000).toFloat()
    }*/

    constructor(millisInFuture: Long, countDownInterval: Long, mContext: Context, mActivity: Activity, progress: ProgressBar, timerTxt: TextView, optionTVArray: Array<TextView>) : super(millisInFuture, countDownInterval) {
        this.mContext = mContext
        this.mActivity = mActivity
        this.progress = progress
        this.timerTxt = timerTxt
        this.optionTVArray = optionTVArray
        quizTime = (millisInFuture / 1000).toFloat()

        //progress.setProgress(100)

        //creating animation for timer circle
        val animation = ObjectAnimator.ofInt(progress, "progress", 1, 100)
        animation.duration = quizTime.toLong() // 3 second
        animation.interpolator = LinearInterpolator()
        animation.start()

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
        //timerTxt.text = "  " + String.format("%02d", timeElapsed % 60)

        //Changing seconds to percentage
        var perc = (timeElapsed.toInt() * 100) / quizTime.toInt()
        //setting progress value
        //progress.setProgress(perc)
        //changing seconds to timer textview
        timerTxt.text = timeElapsed.toString()

    }

    companion object {
        var timeElapsed: Long = 0
    }

}