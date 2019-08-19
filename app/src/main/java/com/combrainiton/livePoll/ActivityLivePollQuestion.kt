@file:Suppress("DEPRECATION")

package com.combrainiton.livePoll

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.combrainiton.R
import com.combrainiton.models.QuestionResponceModel
import com.combrainiton.models.SendPollUserAnswerRequestModel
import com.combrainiton.managers.LivePollAndQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.utils.QuestionCountDownTimer
import com.combrainiton.webSocket.PollQuestionOptionWebSocketListener
import kotlinx.android.synthetic.main.activity_live_poll_question.*
import okhttp3.OkHttpClient
import okhttp3.Request

class ActivityLivePollQuestion : AppCompatActivity(), View.OnClickListener {

    private var optionTVArray: Array<TextView>? = null
    private var optionCVArray: Array<CardView>? = null
    private var optionId: Int = 0
    private var questionId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_poll_question)

        var currentQuestion: Int = AppSharedPreference(this@ActivityLivePollQuestion).getInt("currentQuestion")
        currentQuestion += 1
        AppSharedPreference(this@ActivityLivePollQuestion).saveInt("currentQuestion", currentQuestion)
        initViews()
    }

    private fun initViews() {
        //here we will update old data
        AppSharedPreference(this@ActivityLivePollQuestion).saveBoolean("isUserAnswer", false)
        AppSharedPreference(this@ActivityLivePollQuestion).saveInt("userAnswerId", 0)
        //tvQuit

        val totalQuestion: String = "" + AppSharedPreference(this@ActivityLivePollQuestion).getInt("totalQuestion")
        val currentQuestion: String = "" + AppSharedPreference(this@ActivityLivePollQuestion).getInt("currentQuestion")
        val queCountStr = "$currentQuestion/$totalQuestion"
        activity_quiz_question_count.text = queCountStr

        //llProgress
        //progressBarQuiz
        //tvTime

        //topBarResultViewContainer
        //tvQuestionPoint
        //topBarQuestionResultText
        llGraph.visibility = View.GONE

        val questionModel: QuestionResponceModel? = intent.getSerializableExtra("question") as QuestionResponceModel?
        questionId = questionModel!!.question_id
        val quizTime: Long = questionModel.question_time.toLong() * 1000
        progressBarStart.max = questionModel.question_time
        optionTVArray = arrayOf(activity_quiz_question_text_view_option_one, activity_quiz_question_text_view_option_two, activity_quiz_question_text_view_option_three, activity_quiz_question_text_view_option_four)
        optionCVArray = arrayOf(activity_quiz_question_card_view_option_one, activity_quiz_question_card_view_option_two, activity_quiz_question_card_view_option_three, activity_quiz_question_card_view_option_four)
        val countDownTimer = QuestionCountDownTimer(quizTime, 1000, this@ActivityLivePollQuestion, this@ActivityLivePollQuestion, progressBarStart, tvTime, optionTVArray!!)
        countDownTimer.start()
        //checkUserAnswer(quizTime)
        startWebSocket()

        if (questionModel.question_image.isEmpty()) {
            question_image_container.visibility = View.GONE

        } else {
            question_image_container.visibility = View.VISIBLE
            //imgQue
            Glide.with(this@ActivityLivePollQuestion)
                    .load(questionModel.question_image)
                    .into(imgQue)
        }

        activity_quiz_question_text.text = questionModel.question_title

        val optionList: List<QuestionResponceModel.OptionListModel>? = questionModel.options

        for (i in optionTVArray!!.indices) {

            optionTVArray!![i].text = optionList!![i].option_title
            optionTVArray!![i].tag = optionList[i].option_id
            optionTVArray!![i].setOnClickListener(this@ActivityLivePollQuestion)
            optionCVArray!![i].tag = optionList[i].option_id

        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.activity_quiz_question_text_view_option_one -> {
                optionId = p0.tag as Int
                setSelectedOptionBackground(optionId)
            }
            R.id.activity_quiz_question_text_view_option_two -> {
                optionId = p0.tag as Int
                setSelectedOptionBackground(optionId)
            }
            R.id.activity_quiz_question_text_view_option_three -> {
                optionId = p0.tag as Int
                setSelectedOptionBackground(optionId)
            }
            R.id.activity_quiz_question_text_view_option_four -> {
                optionId = p0.tag as Int
                setSelectedOptionBackground(optionId)
            }

        }
    }

    private fun setSelectedOptionBackground(optionId: Int) {

        for (i in optionTVArray!!.indices) {

            if (optionTVArray!![i].tag == optionId) {
                optionTVArray!![i].setBackgroundColor(resources.getColor(setEnableBackground(i)))
            } else {
                optionTVArray!![i].setBackgroundColor(resources.getColor(setDisableBackground(i)))
            }

            optionTVArray!![i].isClickable = false
            optionTVArray!![i].isEnabled = false

        }

        val mDialog = AppProgressDialog(this@ActivityLivePollQuestion)
        mDialog.show()

        val pollId: Int = AppSharedPreference(this@ActivityLivePollQuestion).getInt("pollId")
        val quizPin: String = AppSharedPreference(this@ActivityLivePollQuestion).getString("pollPin")

        val requestMap = SendPollUserAnswerRequestModel(pollId, questionId, quizPin, optionId)

        LivePollAndQuizManagement(this@ActivityLivePollQuestion, this@ActivityLivePollQuestion, mDialog).sendPollUserAnswer(requestMap)

    }

    private fun setEnableBackground(i: Int): Int {
        return when (i) {
            0 -> R.color.colorCategoryOne
            1 -> R.color.colorCategoryTwo
            2 -> R.color.colorCategoryThree
            3 -> R.color.colorCategoryFour
            else -> R.color.colorTextPrimaryLight
        }
    }

    private fun setDisableBackground(i: Int): Int {
        return when (i) {
            0 -> R.color.colorCategoryOneDisabled
            1 -> R.color.colorCategoryTwoDisabled
            2 -> R.color.colorCategoryThreeDisabled
            3 -> R.color.colorCategoryFourDisabled
            else -> R.color.colorTextPrimaryLight
        }
    }

    fun checkUserAnswer() {
        /*Handler().postDelayed(object : Runnable {
            override fun run() {
    startWebSocket()
                var mDialog=AppProgressDialog(this@ActivityLiveQuizQuestion)
                mDialog.show()

                var quizId:Int=AppSharedPreference(this@ActivityLiveQuizQuestion).getInt("quizId")
                var quizPin:String=AppSharedPreference(this@ActivityLiveQuizQuestion).getString("quizPin")

                var requestMap= SendUserAnswerModel(quizId,optionId,quizPin,questionId)
                *//*requestMap.put("quiz_id",quizId)
            requestMap.put("option_id",optionId)
            requestMap.put("quiz_pin",quizPin)
            requestMap.put("question_id",questionId)
            *//*
            if (optionId==0){
                var option1Id:Int= optionTVArray!![0].tag as Int
                var option2Id:Int= optionTVArray!![1].tag as Int
                var option3Id:Int= optionTVArray!![2].tag as Int
                var option4Id:Int= optionTVArray!![3].tag as Int
                var getCorrectModel= GetCorrectOptionRequestModel(quizPin,quizId,questionId,option1Id,option2Id,option3Id,option4Id)
                LivePollAndQuizManagement(this@ActivityLiveQuizQuestion, this@ActivityLiveQuizQuestion, mDialog).getCorrectOption(getCorrectModel,llProgress,topBarResultViewContainer,tvQuestionPoint,topBarQuestionResultText, optionTVArray!!,false,false)


            }else {
                var requestMap= SendUserAnswerModel(quizId,optionId,quizPin,questionId)
                LivePollAndQuizManagement(this@ActivityLiveQuizQuestion, this@ActivityLiveQuizQuestion, mDialog).checkUserAnswer(requestMap, llProgress, topBarResultViewContainer, tvQuestionPoint, topBarQuestionResultText, optionTVArray!!)
            }
        }


    },delayTime)



   */
    }

    private fun startWebSocket() {
        val gamePin: String = AppSharedPreference(this@ActivityLivePollQuestion).getString("pollPin")
        val wsUrl = "ws://13.67.94.139/pin-$gamePin-option-stats/"
        val client = OkHttpClient()
        val mRequest = Request.Builder().url(wsUrl).header("Origin", wsUrl).build()
        val webLisner = PollQuestionOptionWebSocketListener(this@ActivityLivePollQuestion, this@ActivityLivePollQuestion, llProgress, llGraph, chart)
        client.newWebSocket(mRequest, webLisner)
        client.dispatcher().executorService().shutdown()
    }

}
