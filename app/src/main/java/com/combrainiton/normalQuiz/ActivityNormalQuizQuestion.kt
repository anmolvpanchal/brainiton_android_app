@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package com.combrainiton.normalQuiz

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.combrainiton.R
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.models.GetNormalQuizScoreRequestModel
import com.combrainiton.models.ObjectQuizResult
import com.combrainiton.models.QuestionResponceModel
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.utils.NetworkHandler
import com.combrainiton.utils.QuestionCountDownTimer
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.activity_quiz_question.*
import android.R.id.edit
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.android.synthetic.main.activity_normal_quiz_result.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ActivityNormalQuizQuestion : AppCompatActivity(), View.OnClickListener, TextToSpeech.OnInitListener {

    private val TAG: String = "ActivityNormalQuizQuestion"

    private var tts: TextToSpeech? = null

    private var optionTextViewList: Array<TextView>? = null

    private var optionCardViewList: Array<androidx.cardview.widget.CardView>? = null

    private var userAnswer: Int = 0

    private var rightAnswer: Int = 0

    private var optionId: Int = 0

    private var questionId: Int = 0

    private var totalQuestion: Int = 0

    private var currentQuestion: Int = 0

    private lateinit var questionList: ArrayList<QuestionResponceModel>

    private lateinit var questionModel: QuestionResponceModel

    private lateinit var userAnswerList: ArrayList<GetNormalQuizScoreRequestModel.QuestionsList>

    private lateinit var countDownTimer: QuestionCountDownTimer

    private lateinit var questionLoader: Handler

    private lateinit var answerChecker: Handler

    private var actualTime: Int = 0

    private val result: ObjectQuizResult = ObjectQuizResult()

    private var questionDescription: String = ""

    private var OptionOne: String = ""
    private var OptionTwo: String = ""
    private var OptionThree: String = ""
    private var OptionFour: String = ""
    private var speakQuestion : String = ""
    private lateinit var timerSound: MediaPlayer

    private var sound: Boolean = true

    private var temp: Boolean = false

    private lateinit var timerAnimate: ObjectAnimator

    private lateinit var sneaker: Sneaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)
        sound = AppSharedPreference(this@ActivityNormalQuizQuestion).getBoolean("sound")

        sneaker = Sneaker(this@ActivityNormalQuizQuestion)

        initViews() //initiates main view

        // Create object of SharedPreferences.
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        //now get Editor
        val editor = sharedPref.edit()
        //put your value
        editor.putString("listofQues", "stackoverlow")

        tts = TextToSpeech(this, this)


    }


    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            var speachListener = object : UtteranceProgressListener(){
                @SuppressLint("LongLogTag")
                override fun onDone(string: String?) {
                    Log.e(TAG,"Sound completed " + string)
                    timerSound = MediaPlayer.create(applicationContext, R.raw.question_timer_loop)
                    timerSound.isLooping = true
                    timerSound.start()
                }

                override fun onError(p0: String?) {
                    Toast.makeText(this@ActivityNormalQuizQuestion,"Error cannot speak",Toast.LENGTH_SHORT).show()
                }

                override fun onStart(p0: String?) {
                    val result = tts!!.setLanguage(Locale("en", "IN"))

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language specified is not supported!")
                    } else {
                        actvity_quiz_question_speak_button_for_options!!.isEnabled = true
                    }
                }

            }

            tts?.setOnUtteranceProgressListener(speachListener)

        } else {
            Log.e("TTS", "Initilization Failed!")
            Toast.makeText(this@ActivityNormalQuizQuestion,"Error cannot speak",Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("NewApi")
    private fun initViews() {

        //save normal quiz point to 0.0
        AppSharedPreference(this@ActivityNormalQuizQuestion).saveString("normalQuizPoint", "0.00")

        //set on click listener to quit button
        quiz_instruction_quit_button.setOnClickListener(this@ActivityNormalQuizQuestion)

        userAnswerList = ArrayList()

        questionList = ArrayList()

        questionList = intent.getSerializableExtra("questionList") as ArrayList<QuestionResponceModel>

        //set total question count
        totalQuestion = questionList.size

        //load the current question from question list into question model
        questionModel = questionList[currentQuestion]

        //set the question model data into
        setData(questionModel)

        actvity_quiz_question_next_button_for_description.setOnClickListener(this@ActivityNormalQuizQuestion)

        actvity_quiz_question_next_button_for_question.setOnClickListener(this@ActivityNormalQuizQuestion)

        actvity_quiz_question_speak_button_for_options.setOnClickListener(this@ActivityNormalQuizQuestion)

        activity_quiz_question_text.setOnClickListener(this@ActivityNormalQuizQuestion)
    }

    @SuppressLint("LongLogTag")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setData(questionModel: QuestionResponceModel?) {

        result.questionNo = currentQuestion + 1
        val questionNo: Int = currentQuestion + 1

        //set total number of questions
        val questionNumber = "$questionNo/$totalQuestion"
        activity_quiz_question_count.visibility = View.VISIBLE
        activity_quiz_question_count.text = "Question " + questionNumber

        //Timer animation and textview will remain invisible until 3 secs delay
        timer_layout.visibility = View.GONE
        tvTime.visibility = View.GONE
        progressBarCircle.visibility = View.GONE

        //make question view visible
        activity_quiz_question_main_conatiner.visibility = View.VISIBLE

        //make question result view invisible
        activity_quiz_question_result_container.visibility = View.GONE

        //TODO we no longer use old progress bar remove it's usage from the project
        llProgress.visibility = View.GONE

        //set the next button visibility to gone
        actvity_quiz_question_next_button_for_description.visibility = View.GONE

        //set top bar result container to invisible
        quiz_question_result_top_bar_container.visibility = View.GONE

        //set speak container to invisible
        actvity_quiz_question_speak_button_for_options.visibility = View.GONE

        //make option view invisible
        activity_quiz_option_container.visibility = View.GONE

        //set question id from question model
        questionId = questionModel!!.question_id

        //set question time from question model
        actualTime = questionModel.question_time

        //calculate quiz time into millis
        val quizTime: Long = questionModel.question_time.toLong() * 1000

        //create array of option text view
        optionTextViewList = arrayOf(activity_quiz_question_text_view_option_one, activity_quiz_question_text_view_option_two, activity_quiz_question_text_view_option_three, activity_quiz_question_text_view_option_four)

        //create array of option container card view
        optionCardViewList = arrayOf(activity_quiz_question_card_view_option_one, activity_quiz_question_card_view_option_two, activity_quiz_question_card_view_option_three, activity_quiz_question_card_view_option_four)

        //initialize the count down timer  with quiz time
        //countDownTimer = QuestionCountDownTimer(quizTime, 1000, this@ActivityNormalQuizQuestion, this@ActivityNormalQuizQuestion, progressBarStart, progressBarEnd, tvTime, optionTextViewList!!)
        countDownTimer = QuestionCountDownTimer(quizTime, 1000, this@ActivityNormalQuizQuestion, this@ActivityNormalQuizQuestion, progressBarCircle, tvTime, optionTextViewList!!)

        if (questionModel.question_image.isEmpty()) { //if question doesn't have any image
            activity_quiz_question_image.visibility = View.GONE // make question image view visibility gone
        } else {
            activity_quiz_question_image.visibility = View.VISIBLE //else make question image visible
            Glide.with(this@ActivityNormalQuizQuestion) //load the image into question image view
                    .load(questionModel.question_image)
                    .into(activity_quiz_question_image)
        }

        try {
            //try to parse the question description from the question title
            questionDescription = (questionModel.question_title.subSequence(questionModel.question_title.indexOf(";"), questionModel.question_title.length)).toString()
            //set the question title after removing the description from it
            activity_quiz_question_text.text = questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";"))
            Log.e(TAG, "question" + questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";")) as String?)
            speakQuestion = (questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";")) as String?).toString()
            result.questionText = questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";")) as String?


        } catch (e: Exception) {
            //if eror occurs then set the question without parsing
            activity_quiz_question_text.text = questionModel.question_title
            Log.e(TAG, "error " + questionModel.question_title)
            speakQuestion = questionModel.question_title.toString()
            result.questionText = questionModel.question_title
            System.out.println("no description found")
        }

        //create list options from question model
        val optionList: List<QuestionResponceModel.OptionListModel>? = questionModel.options

        //for each option in option list
        for (i in optionTextViewList!!.indices) {
            //set option title
            optionTextViewList!![i].text = optionList!![i].option_title
            Log.e(TAG, "options text" + optionList!![i].option_title)

            //set option id as option text view tag
            optionTextViewList!![i].tag = optionList[i].option_id
            //make option view clickable
            optionTextViewList!![i].isClickable = true
            //make option view enable
            optionTextViewList!![i].isEnabled = true
            //add on click listener
            optionTextViewList!![i].setOnClickListener(this@ActivityNormalQuizQuestion)
            //set backgrounds
            optionTextViewList!![i].setBackgroundColor(resources.getColor(setEnableBackground(i)))
        }

        // assigning the options
        OptionOne = optionList!![0].option_title
        OptionTwo = optionList!![1].option_title
        OptionThree = optionList!![2].option_title
        OptionFour = optionList!![3].option_title

        Log.e(TAG, " options assigned " + OptionOne + " " + OptionTwo + " " + OptionThree + " " + OptionFour)

        //initiate progress bar with three second of delay
        initProgresBar()
        //show option after three second of dealy
        showOption(3 * 1000, countDownTimer, quizTime)
    }

    private fun speakQuestion(){
        val text:String = speakQuestion
        tts!!.setSpeechRate(0.75F)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
        }

    }
    private fun speakOut(){
        val text = "A... " + OptionOne +"... Bee... " + OptionTwo + "... C... "  + OptionThree + "... Dee... " + OptionFour
        tts!!.setSpeechRate(0.75F)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
        }

    }
    private fun stopSpeaking(){
        if (tts!!.isSpeaking){
            tts?.stop()
        }
    }
    //to show the options to the user after a delayed time
    @SuppressLint("NewApi")
    private fun showOption(delayTime: Long, mTimer: QuestionCountDownTimer, quizTime: Long) {
        temp = false
        //create new thread
        questionLoader = Handler()

        //start thread with delay time
        questionLoader.postDelayed({
            activity_quiz_option_container.visibility = View.VISIBLE //make option layout visible
            actvity_quiz_question_speak_button_for_options.visibility = View.VISIBLE // speak option visible
            startProgressBar() //start the vertical progress bar
            mTimer.start() // start the question timer
            notAnswer(quizTime) //start another thread with delay = quiztime which will be called if the user doesn't answers the question
        }, delayTime) //delay time of thread

    }

    //hnadles the screen when user doesn't response anything
    private fun notAnswer(quizTime: Long) {
        //create new thread
        answerChecker = Handler()
        temp = true
        //start thread with dealy time = quiztime
        answerChecker.postDelayed({
            if (sound) {
                timerSound.release()
            }
            for (i in optionTextViewList!!.indices) {
                optionTextViewList!![i].setBackgroundColor(resources.getColor(setDisableBackground(i)))
                //set all option non clickable  and disabled
                optionTextViewList!![i].isClickable = false
                optionTextViewList!![i].isEnabled = false
                //set top bar result container visible
                quiz_question_result_top_bar_container.visibility = View.GONE
                //set top bar text color to red
                //activity_quiz_question_result_top_bar.setTextColor(resources.getColor(R.color.colorCategoryThree))
                //set top bar text to result text 4
                //activity_quiz_question_result_top_bar.text = baseContext.getString(R.string.top_bar_question_result_text_1)
            }
            actvity_quiz_question_next_button_for_description.visibility = View.VISIBLE
            val mDialog = AppProgressDialog(this@ActivityNormalQuizQuestion)
            mDialog.show()
            //get quiz id
            val quizId: Int = intent.getIntExtra("quizId", 0)
            //create request data object
            val requestData = HashMap<String, Int>()
            requestData["quiz_id"] = quizId //add quiz id to request data
            requestData["question_id"] = questionId //add question id to request data
            requestData["time_remaining"] = 0 //add time remaining to request data
            requestData["question_time"] = actualTime //add quiz time to request data
            requestData["option_id"] = 0 //add selected option id to request data
            optionId = 0 //set option id equals to zero
            //get correct option data from normal quiz management

            sneaker = NormalQuizManagement(this@ActivityNormalQuizQuestion, this@ActivityNormalQuizQuestion, mDialog).getCorrectOption(result, requestData, llProgress, quiz_question_result_top_bar_container, activity_quiz_question_result_top_bar, optionTextViewList, optionId, activity_quiz_question_answer_result_image, activity_quiz_question_answer_result_text, activity_quiz_question_total_score, rootLeaderLayout, activity_quiz_question_score_card)
        }, quizTime)
    }

    @SuppressLint("NewApi", "LongLogTag")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.activity_quiz_question_text_view_option_one -> {
                //Stopping timer animation and timer textview
                tvTime.text = "0"
                timerAnimate.end()

                optionId = p0.tag as Int
                userAnswer = 1
                result.userOptionText = activity_quiz_question_text_view_option_one.text as String?
                Log.e(TAG, " option one text " + result.userOptionText)
                setSelectedOptionBackground(optionId)
                stopSpeaking()
                actvity_quiz_question_speak_button_for_options.visibility = View.GONE
                actvity_quiz_question_next_button_for_description.visibility = View.VISIBLE
            }
            R.id.activity_quiz_question_text_view_option_two -> {
                //Stopping timer animation and timer textview
                tvTime.text = "0"
                timerAnimate.end()

                userAnswer = 2
                optionId = p0.tag as Int
                result.userOptionText = activity_quiz_question_text_view_option_two.text as String?
                setSelectedOptionBackground(optionId)
                stopSpeaking()
                actvity_quiz_question_speak_button_for_options.visibility = View.GONE
                actvity_quiz_question_next_button_for_description.visibility = View.VISIBLE
            }
            R.id.activity_quiz_question_text_view_option_three -> {
                //Stopping timer animation and timer textview
                tvTime.text = "0"
                timerAnimate.end()

                userAnswer = 3
                optionId = p0.tag as Int
                result.userOptionText = activity_quiz_question_text_view_option_three.text as String?
                setSelectedOptionBackground(optionId)
                stopSpeaking()
                actvity_quiz_question_speak_button_for_options.visibility = View.GONE
                actvity_quiz_question_next_button_for_description.visibility = View.VISIBLE
            }
            R.id.activity_quiz_question_text_view_option_four -> {
                //Stopping timer animation and timer textview
                tvTime.text = "0"
                timerAnimate.end()

                userAnswer = 4
                optionId = p0.tag as Int
                result.userOptionText = activity_quiz_question_text_view_option_four.text as String?
                setSelectedOptionBackground(optionId)
                stopSpeaking()
                actvity_quiz_question_speak_button_for_options.visibility = View.GONE
                actvity_quiz_question_next_button_for_description.visibility = View.VISIBLE
            }
            R.id.actvity_quiz_question_speak_button_for_options -> {
                if (sound) {
                    timerSound.release()
                }
                if (tts!!.isSpeaking){

                }else {
                    speakOut()
                }
            }
            R.id.activity_quiz_question_text ->{
                if (sound){
                    timerSound.release()
                }
                if (tts!!.isSpeaking){

                }else{
                    speakQuestion()
                }
            }
            R.id.actvity_quiz_question_next_button_for_description -> {
                //rlQuestionView.visibility = View.GONE
                activity_quiz_question_result_container.visibility = View.VISIBLE
                quiz_question_result_top_bar_container.visibility = View.GONE
                activity_quiz_option_container.visibility = View.GONE
                if (questionDescription.isNotEmpty()) {
                    activity_quiz_question_description.text = questionDescription.subSequence(questionDescription.indexOf(";") + 1, questionDescription.length)
                    System.out.println(questionDescription)
                    result_with_description.visibility = View.VISIBLE
                } else {
                    result_with_description.visibility = View.GONE
                }
            }
            R.id.actvity_quiz_question_next_button_for_question -> {
                nextQuestion()
            }
            R.id.quiz_instruction_quit_button -> {
                questionLoader.removeCallbacksAndMessages(null)
                if (temp)
                    answerChecker.removeCallbacksAndMessages(null)
                if (sound) timerSound.release()
                explore()
            }
        }
    }

    private fun getQuizScore() {
        if (NetworkHandler(this@ActivityNormalQuizQuestion).isNetworkAvailable()) {
            val requestData = GetNormalQuizScoreRequestModel()
            val quizId: Int = intent.getIntExtra("quizId", 0)
            requestData.quiz_id = quizId
            //requestData.questions=userAnswerList
            val mDialog = AppProgressDialog(this@ActivityNormalQuizQuestion)
            mDialog.show()
            val quizName: String = intent.getStringExtra("quizName")
            NormalQuizManagement(this@ActivityNormalQuizQuestion, this@ActivityNormalQuizQuestion, mDialog).getQuizScore(requestData, result, quizName)
        } else {
            Toast.makeText(this@ActivityNormalQuizQuestion, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
        }
    }

    private fun setSelectedOptionBackground(optionId: Int) {
        countDownTimer.cancel()//stop the timer
        stopProgressBar() //stop the progress bar
        for (i in optionTextViewList!!.indices) {
            if (optionTextViewList!![i].tag == optionId) {
                optionTextViewList!![i].setBackgroundColor(resources.getColor(setEnableBackground(i)))
            } else {
                optionTextViewList!![i].setBackgroundColor(resources.getColor(setDisableBackground(i)))
            }
            optionTextViewList!![i].isClickable = false
            optionTextViewList!![i].isEnabled = false
        }
        val mDialog = AppProgressDialog(this@ActivityNormalQuizQuestion)
        mDialog.show()
        answerChecker.removeCallbacksAndMessages(null)
        val quizId: Int = intent.getIntExtra("quizId", 0)
        val requestData = HashMap<String, Int>()
        var timeRemaining: Int = AppSharedPreference(this@ActivityNormalQuizQuestion).getInt("timeRemaning")
        timeRemaining -= 1390
        if (timeRemaining < 0) {
            timeRemaining = 0
        }
        requestData["quiz_id"] = quizId
        requestData["question_id"] = questionId
        requestData["time_remaining"] = timeRemaining
        requestData["question_time"] = actualTime
        requestData["option_id"] = optionId

        //getting sneaker to dismiss on next question click
        sneaker = NormalQuizManagement(this@ActivityNormalQuizQuestion, this@ActivityNormalQuizQuestion, mDialog).getCorrectOption(result, requestData, llProgress, quiz_question_result_top_bar_container, activity_quiz_question_result_top_bar, optionTextViewList, optionId, activity_quiz_question_answer_result_image, activity_quiz_question_answer_result_text, activity_quiz_question_total_score, rootLeaderLayout, activity_quiz_question_score_card)

    }

    //for next question & end the quiz & show leaderboard
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun nextQuestion() {

        //Hiding the sneaker before going to next question
        sneaker.hide()
        //firstly we will save user answer id with question id
        result.addData(result)
        userAnswerList.add(GetNormalQuizScoreRequestModel.QuestionsList(questionModel.question_id, optionId))
        currentQuestion += 1
        if (currentQuestion == totalQuestion) {
            getQuizScore()
        } else {
            questionModel = questionList[currentQuestion]
            setData(questionModel)
        }
    }

    //init progress bar before starting it with 3 second of delay
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initProgresBar() {

        if (sound) {
            timerSound = MediaPlayer.create(applicationContext, R.raw.question_timer_loop)
            timerSound.isLooping = true
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
        timerAnimate.duration = questionModel.question_time.toLong() * 1000 // 0.5 second
        timerAnimate.interpolator = LinearInterpolator()
        timerAnimate.start()
        /**Circular timer end*/
        //set progress on left side visible
        progressBarStart.visibility = View.VISIBLE
        //make animation object for progress value of left progress bar
        val animation1 = ObjectAnimator.ofInt(progressBarStart, "progress", 100, 0)
        //set duration equal to the question time
        animation1.duration = questionModel.question_time.toLong() * 1000 // 0.5 second
        //set animation style to linear
        animation1.interpolator = LinearInterpolator()
        //start animation
        animation1.start()
        //set progress bar on right side visible
        progressBarEnd.visibility = View.VISIBLE
        val animation2 = ObjectAnimator.ofInt(progressBarEnd, "progress", 100, 0)
        //make animation object for progress value of left progress bar
        animation2.duration = questionModel.question_time.toLong() * 1000 // 0.5 second
        //set duration equal to the question time
        animation2.interpolator = LinearInterpolator()
        //set animation style to linear
        animation2.start()
        //start animation
    }

    private fun stopProgressBar() {
        if (sound) {
            timerSound.release()
        }
        //set visibility of left progress bar to gone
        progressBarStart.visibility = View.GONE
        //set visibility of right progress bar to gone
        progressBarEnd.visibility = View.GONE
    }

    //set the color of options button to make them look like enabled button
    private fun setEnableBackground(i: Int): Int {
        return when (i) {
            0 -> R.color.colorCategoryOne
            1 -> R.color.colorCategoryTwo
            2 -> R.color.colorCategoryThree
            3 -> R.color.colorCategoryFour
            else -> R.color.colorTextPrimaryLight
        }
    }

    //set the color of options button to make them look like disabled button
    private fun setDisableBackground(i: Int): Int {
        return when (i) {
            0 -> R.color.colorCategoryOneDisabled
            1 -> R.color.colorCategoryTwoDisabled
            2 -> R.color.colorCategoryThreeDisabled
            3 -> R.color.colorCategoryFourDisabled
            else -> R.color.colorTextPrimaryLight
        }
    }

    // this will open the home activity
    private fun explore() {
        if (NetworkHandler(this@ActivityNormalQuizQuestion).isNetworkAvailable()) { //if internet is connected
            val mDialog = AppProgressDialog(this@ActivityNormalQuizQuestion)
            mDialog.show() //show progress dialog
            //this will open the home activity after retriving the data
            NormalQuizManagement(this@ActivityNormalQuizQuestion, this@ActivityNormalQuizQuestion, mDialog).getAllQuiz()
        } else {
            //display error message
            Toast.makeText(this@ActivityNormalQuizQuestion, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
        }
    }

    //open home activtiy on backpressed
    override fun onBackPressed() {
        //explore()
    }

}


