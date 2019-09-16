@file:Suppress("DEPRECATION")

package com.combrainiton.liveQuiz

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import com.combrainiton.R
import com.bumptech.glide.Glide
import com.combrainiton.managers.LivePollAndQuizManagement
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.models.QuestionResponceModel
import com.combrainiton.models.SendUserAnswerModel
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.utils.NetworkHandler
import com.combrainiton.utils.QuestionCountDownTimer
import com.combrainiton.webSocket.LiveQuizQuestionOptionWebSocketListener
import kotlinx.android.synthetic.main.activity_quiz_question.*
import okhttp3.OkHttpClient
import okhttp3.Request
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.view.Gravity
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import com.tapadoo.alerter.Alerter
import kotlin.system.measureTimeMillis


class ActivityLiveQuizQuestion : AppCompatActivity(), View.OnClickListener, Animator.AnimatorListener {

    private var optionTVArray: Array<TextView>? = null
    private var optionCVArray: Array<androidx.cardview.widget.CardView>? = null
    private var optionId: Int = 0
    private var questionId: Int = 0
    private var quizTime: Long = 0
    private var countDownTimer: QuestionCountDownTimer? = null
    private lateinit var timerSound: MediaPlayer
    private var sound: Boolean = false
    private lateinit var client: OkHttpClient
    private lateinit var timerAnimate: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)
        var currentQuestion: Int = AppSharedPreference(this@ActivityLiveQuizQuestion).getInt("currentQuestion")
        currentQuestion += 1
        AppSharedPreference(this@ActivityLiveQuizQuestion).saveInt("currentQuestion", currentQuestion)
        sound = AppSharedPreference(this@ActivityLiveQuizQuestion).getBoolean("sound")
        initViews()

    }

    @SuppressLint("NewApi")
    private fun initViews() {

        //set is user answer flag to false
        AppSharedPreference(this@ActivityLiveQuizQuestion).saveBoolean("isUserAnswer", false)

        //set user answer id to 0
        AppSharedPreference(this@ActivityLiveQuizQuestion).saveInt("userAnswerId", 0)

        //set next button visibility to gone
        //actvity_quiz_question_next_button_for_description.visibility = View.GONE

        //get total number of question
        val totalQuestion: String = "" + AppSharedPreference(this@ActivityLiveQuizQuestion).getInt("totalQuestion")

        //get current question
        val currentQuestion: String = "" + AppSharedPreference(this@ActivityLiveQuizQuestion).getInt("currentQuestion")

        //set question counter text view
        val queCountStr = "Question $currentQuestion/$totalQuestion"
        activity_quiz_question_count.visibility = View.VISIBLE
        activity_quiz_question_count.text = queCountStr

        //Timer animation and textview will remain invisible until 3 secs delay
        timer_layout.visibility = View.GONE
        tvTime.visibility = View.GONE
        progressBarCircle.visibility = View.GONE

        //get question from question response model
        val questionModel: QuestionResponceModel? = intent.getSerializableExtra("question") as QuestionResponceModel?

        //get question id
        questionId = questionModel!!.question_id

        //get quiz time
        quizTime = questionModel.question_time.toLong() * 1000

        //set option text view array
        optionTVArray = arrayOf(activity_quiz_question_text_view_option_one, activity_quiz_question_text_view_option_two, activity_quiz_question_text_view_option_three, activity_quiz_question_text_view_option_four)

        //set option card view array
        optionCVArray = arrayOf(activity_quiz_question_card_view_option_one, activity_quiz_question_card_view_option_two, activity_quiz_question_card_view_option_three, activity_quiz_question_card_view_option_four)

        //initialize count down timer
        countDownTimer = QuestionCountDownTimer(quizTime, 1000, this@ActivityLiveQuizQuestion, this@ActivityLiveQuizQuestion, progressBarStart, tvTime, optionTVArray!!, activity_quiz_question_result_top_bar)

        //countDownTimer = QuestionCountDownTimer(quizTime, 1000, this@ActivityLiveQuizQuestion, this@ActivityLiveQuizQuestion, progressBarStart, tvTime, optionTVArray!!, activity_quiz_question_result_top_bar)
        //start count down timer
        countDownTimer!!.start()

        //initialize progress bar with three second of delay
        initProgresBar()

        //start websocket
        startWebSocket()

        if (questionModel.question_image.isEmpty()) { //if question does not has image
            activity_quiz_question_image.visibility = View.GONE //set image view to invisible

        } else { //if question has image

            activity_quiz_question_image.visibility = View.VISIBLE //set image view to visible
            Glide.with(this@ActivityLiveQuizQuestion) //load image with glide
                    .load(questionModel.question_image)
                    .into(activity_quiz_question_image)
        }
        //set quiz title
        activity_quiz_question_text.text = questionModel.question_title

        //get the options from the option model
        val optionList: List<QuestionResponceModel.OptionListModel>? = questionModel.options

        //for each value in options arraylist
        for (i in optionTVArray!!.indices) {
            optionTVArray!![i].text = optionList!![i].option_title //set options text
            optionTVArray!![i].tag = optionList[i].option_id //set option id as tag
            optionTVArray!![i].setOnClickListener(this@ActivityLiveQuizQuestion) //set onclick listener
            optionCVArray!![i].tag = optionList[i].option_id //set option id as option text view tag
            optionTVArray!![i].setBackgroundColor(resources.getColor(setEnableBackground(i))) //set background colors
        }
        //start progress bar
        startProgressBar()

        quiz_instruction_quit_button.setOnClickListener(this)
    }


    //init progress bar before starting it with 3 second of delay
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initProgresBar() {
        if(sound) {
            timerSound = MediaPlayer.create(applicationContext, R.raw.question_timer_loop)
            timerSound.isLooping = true;
            timerSound.start()
        }
        //set progress on left side visible
        progressBarStart.visibility = View.VISIBLE
        //set progress bar on right side visible
        progressBarEnd.visibility = View.VISIBLE
        //make animation object for left progress bar
        val animation1 = ObjectAnimator.ofInt(progressBarStart, "progress", 0, 100)
        //set duration for animation
        animation1.duration = 3000 // 3 second
        //set animation style
        animation1.interpolator = LinearInterpolator()
        //start animation
        animation1.start()
        //make animation object for right progress bar
        val animation2 = ObjectAnimator.ofInt(progressBarEnd, "progress", 0, 100)
        //set duration for animation
        animation2.duration = 3000 // 3 second
        //set animation style
        animation2.interpolator = LinearInterpolator()
        //strart animation
        animation2.start()
    }

    private fun startProgressBar() {
        /**Circular timer */
        //Question count will disappear after 3 secs delay
        activity_quiz_question_count.visibility = View.GONE
        //Timer animation and textview will appear
        timer_layout.visibility = View.VISIBLE
        tvTime.visibility = View.VISIBLE
        progressBarCircle.visibility = View.VISIBLE
        //animation
        timerAnimate = ObjectAnimator.ofInt(progressBarCircle, "progress", 100, 0)
        timerAnimate.duration = quizTime // 0.5 second
        timerAnimate.interpolator = LinearInterpolator()
        timerAnimate.start()
        /**Circular timer end*/
        //set progress on left side visible
        progressBarStart.visibility = View.VISIBLE
        //make animation object for progress value of left progress bar
        val animation1 = ObjectAnimator.ofInt(progressBarStart, "progress", 100, 0)
        //set duration equal to the question time
        animation1.duration = quizTime // 0.5 second
        //set animation style to linear
        animation1.interpolator = LinearInterpolator()
        //start animation
        animation1.start()
        //add listener to perform action at the end of animation
        animation1.addListener(this)
        //set progress bar on right side visible
        progressBarEnd.visibility = View.VISIBLE
        val animation2 = ObjectAnimator.ofInt(progressBarEnd, "progress", 100, 0)
        //make animation object for progress value of left progress bar
        animation2.duration = quizTime
        //set duration equal to the question time
        animation2.interpolator = LinearInterpolator()
        //set animation style to linear
        animation2.start()
        //start animation
    }

    private fun stopProgressBar() {
        if(sound) {
            timerSound.release()
        }
        //set visibility of left progress bar to gone
        progressBarStart.visibility = View.GONE
        //set visibility of right progress bar to gone
        progressBarEnd.visibility = View.GONE
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.activity_quiz_question_text_view_option_one -> { //on click of option 1
                //Stopping timer animation and timer textview
                tvTime.text = "0"
                timerAnimate.end()

                val remainingTime: Int = progressBarStart.progress
                if (remainingTime > 0) {
                    //get the tag of view
                    optionId = p0.tag as Int
                    //set option background accordingly
                    setSelectedOptionBackground(optionId)
                    //cancle the count down timer
                    countDownTimer!!.cancel()
                    //set top bar result container visible
                    quiz_question_result_top_bar_container.visibility = View.VISIBLE
                    //set top bar text to result text 4
                    activity_quiz_question_result_top_bar.text = baseContext.getString(R.string.top_bar_question_result_text_4)
                    if(sound) {
                        val mp = MediaPlayer.create(applicationContext, R.raw.select)
                        mp.start()
                        mp.release()
                    }
                }
            }
            R.id.activity_quiz_question_text_view_option_two -> { //on click option 2

                //Stopping timer animation and timer textview
                tvTime.text = "0"
                timerAnimate.end()

                val remainingTime: Int = progressBarStart.progress
                if (remainingTime > 0) {
                    //get the tag of view
                    optionId = p0.tag as Int
                    //set option background accordingly
                    setSelectedOptionBackground(optionId)
                    //cancle the count down timer
                    countDownTimer!!.cancel()
                    //set top bar result container visible
                    quiz_question_result_top_bar_container.visibility = View.VISIBLE
                    //set top bar text to result text 4
                    activity_quiz_question_result_top_bar.text = baseContext.getString(R.string.top_bar_question_result_text_4)
                    if(sound) {
                        val mp = MediaPlayer.create(applicationContext, R.raw.select)
                        mp.start()
                    }
                }
            }
            R.id.activity_quiz_question_text_view_option_three -> { //on click option 3

                //Stopping timer animation and timer textview
                tvTime.text = "0"
                timerAnimate.end()

                val remainingTime: Int = progressBarStart.progress
                if (remainingTime > 0) {
                    //get the tag of view
                    optionId = p0.tag as Int
                    //set option background accordingly
                    setSelectedOptionBackground(optionId)
                    //cancle the count down timer
                    countDownTimer!!.cancel()
                    //set top bar result container visible
                    quiz_question_result_top_bar_container.visibility = View.VISIBLE
                    //set top bar text to result text 4
                    activity_quiz_question_result_top_bar.text = baseContext.getString(R.string.top_bar_question_result_text_4)
                    if(sound) {
                        val mp = MediaPlayer.create(applicationContext, R.raw.select)
                        mp.start()
                    }
                }
            }
            R.id.activity_quiz_question_text_view_option_four -> { //on click option 4

                //Stopping timer animation and timer textview
                tvTime.text = "0"
                timerAnimate.end()

                val remainingTime: Int = progressBarStart.progress
                if (remainingTime > 0) {
                    //get the tag of view
                    optionId = p0.tag as Int
                    //set option background accordingly
                    setSelectedOptionBackground(optionId)
                    //cancle the count down timer
                    countDownTimer!!.cancel()
                    //set top bar result container visible
                    quiz_question_result_top_bar_container.visibility = View.VISIBLE
                    //set top bar text to result text 4
                    activity_quiz_question_result_top_bar.text = baseContext.getString(R.string.top_bar_question_result_text_4)
                    if(sound) {
                        val mp = MediaPlayer.create(applicationContext, R.raw.select)
                        mp.start()
                    }
                }
            }
            R.id.quiz_instruction_quit_button -> {
                timerSound.stop()
                timerSound.release()
                explore()
            }
        }
    }

    //sets the background of options according to user input
    private fun setSelectedOptionBackground(optionId: Int) {

        //for each text view in option list text view
        for (i in optionTVArray!!.indices) {

            //if option text view tag equals to the option id{which means it's use selected option}
            if (optionTVArray!![i].tag == optionId) {
                //set enabled background
                optionTVArray!![i].setBackgroundColor(resources.getColor(setEnableBackground(i)))
            } else { //if not
                //set disabled background
                optionTVArray!![i].setBackgroundColor(resources.getColor(setDisableBackground(i)))
            }

            //set all option non clickable  and disabled
            optionTVArray!![i].isClickable = false
            optionTVArray!![i].isEnabled = false
        }

        //TODO we no longer use this progress bar remove it's usage from here and from everywhere else in the project
        val mDialog = AppProgressDialog(this@ActivityLiveQuizQuestion)
        mDialog.show()

        //get quiz id from intent data
        val quizId: Int = AppSharedPreference(this@ActivityLiveQuizQuestion).getInt("quizId")
        //get quiz pin from intent data
        val quizPin: String = AppSharedPreference(this@ActivityLiveQuizQuestion).getString("quizPin")
        //get remaining time from intent data
        var timeRemaining: Int = AppSharedPreference(this@ActivityLiveQuizQuestion).getInt("timeRemaning")

        //don't know what this does
        timeRemaining -= 1390
        if (timeRemaining < 0) {
            timeRemaining = 0
        }

        //attach user response daata into request map
        val requestMap = SendUserAnswerModel(quizId, optionId, quizPin, questionId, timeRemaining, quizTime.toInt())

        //pass the user response and check for correct answer
        LivePollAndQuizManagement(this@ActivityLiveQuizQuestion, this@ActivityLiveQuizQuestion, mDialog).checkUserAnswer(requestMap)
    }

    //sets the enabled background of view
    private fun setEnableBackground(i: Int): Int {
        return when (i) {
            0 -> R.color.colorCategoryOne
            1 -> R.color.colorCategoryTwo
            2 -> R.color.colorCategoryThree
            3 -> R.color.colorCategoryFour
            else -> R.color.colorTextPrimaryLight
        }
    }

    //sets the disabled background of view
    private fun setDisableBackground(i: Int): Int {
        return when (i) {
            0 -> R.color.colorCategoryOneDisabled
            1 -> R.color.colorCategoryTwoDisabled
            2 -> R.color.colorCategoryThreeDisabled
            3 -> R.color.colorCategoryFourDisabled
            else -> R.color.colorTextPrimaryLight
        }
    }

    override fun onAnimationEnd(animation: Animator?) {
        timerSound.release()
        if(!this.isDestroyed) {
            if (!AppSharedPreference(this@ActivityLiveQuizQuestion).getBoolean("isUserAnswer")) {
                for (i in optionTVArray!!.indices) {
                    optionTVArray!![i].setBackgroundColor(resources.getColor(setDisableBackground(i)))
                    //set all option non clickable  and disabled
                    optionTVArray!![i].isClickable = false
                    optionTVArray!![i].isEnabled = false
                    //set top bar result container visible
                    quiz_question_result_top_bar_container.visibility = View.VISIBLE
                    //set top bar text color to red
                    activity_quiz_question_result_top_bar.setTextColor(resources.getColor(R.color.colorCategoryThree))
                    //set top bar text to result text 4
                    activity_quiz_question_result_top_bar.text = baseContext.getString(R.string.top_bar_question_result_text_1)
                }
                if (sound) {
                    val mp = MediaPlayer.create(applicationContext, R.raw.wrong)
                    mp.start()
                }
            }else {
            }
        }
    }

    //start the web socket
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startWebSocket() {
        val gamePin: String = AppSharedPreference(this@ActivityLiveQuizQuestion).getString("quizPin")
        val wsUrl = "ws://13.67.94.139/pin-$gamePin-option-stats/"
        val client = OkHttpClient()
        val mRequest = Request.Builder().url(wsUrl).header("Origin", wsUrl).build()
        val webLisner = LiveQuizQuestionOptionWebSocketListener(this@ActivityLiveQuizQuestion, this@ActivityLiveQuizQuestion, llProgress, quiz_question_result_top_bar_container, activtiy_quiz_question_point, activity_quiz_question_result_top_bar, this.optionTVArray!!)
        client.newWebSocket(mRequest, webLisner)
        client.dispatcher().executorService().shutdown()
    }

    override fun onAnimationRepeat(animation: Animator?) {}

    override fun onAnimationCancel(animation: Animator?) {}

    override fun onAnimationStart(animation: Animator?) {}


    // this will open the home activity
    private fun explore() {
        if (NetworkHandler(this@ActivityLiveQuizQuestion).isNetworkAvailable()) { //if internet is connected
            val mDialog = AppProgressDialog(this@ActivityLiveQuizQuestion)
            mDialog.show() //show progress dialog
            //this will open the home activity after retriving the data
            NormalQuizManagement(this@ActivityLiveQuizQuestion, this@ActivityLiveQuizQuestion, mDialog).getAllQuiz()
        } else {
            //display error message
            Toast.makeText(this@ActivityLiveQuizQuestion, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
        }
    }

    //open home activtiy on backpressed
    override fun onBackPressed() {
        //explore()
    }

}

