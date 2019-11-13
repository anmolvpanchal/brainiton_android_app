package com.combrainiton.normalQuiz

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.combrainiton.R
import com.combrainiton.api.ApiClient
import com.combrainiton.api.ApiErrorParser
import com.combrainiton.authentication.ActivitySignIn
import com.combrainiton.managers.NormalQuizManagementInterface
import com.combrainiton.models.*
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppSharedPreference
import com.squareup.picasso.Picasso
import com.wajahatkarim3.easyflipview.EasyFlipView
import kotlinx.android.synthetic.main.activity_learn_quiz.*
import kotlinx.android.synthetic.main.activity_quiz_question.*
import kotlinx.android.synthetic.main.flash_card_layout_back.*
import kotlinx.android.synthetic.main.flash_card_layout_front.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class Learn_quiz : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var currentQuestion: Int = 1
    private lateinit var questionModel: QuestionResponceModel
    var questionsList: ArrayList<QuestionResponceModel> = ArrayList()
    private var quizId: Int = 0
    private val TAG = "Learn_quiz"
    private val result: ObjectQuizResult = ObjectQuizResult()
    var totalQuestion: Int = 0
    private var questionDescription: String = ""
    private var requestInterface: NormalQuizManagementInterface? = null
    private var quizName: String? = null
    private var tts: TextToSpeech? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_quiz)

        quizId = intent.getIntExtra("quizId", 0)
        quizName = intent.getStringExtra("quizName")

        tts = TextToSpeech(this, this)

        Log.i("data", " data from intent" + quizId + " " + quizName)


        easyFlipView.flipDuration = 500
        easyFlipView.isFlipEnabled = true

        easyFlipView.onFlipListener = EasyFlipView.OnFlipAnimationListener { flipView, newCurrentSide ->

            val state: String = newCurrentSide.toString()

            previous_button.setOnClickListener {

                if (state == "BACK_SIDE") {
                    easyFlipView.flipTheView(true)
                    previousQuestion()
                    stopSpeaking()
                }
            }

            next_button.setOnClickListener {

                if (state.equals("BACK_SIDE")) {
                    easyFlipView.flipTheView(true)
                    nextQuestion()
                    stopSpeaking()
                }
            }

            speak_buton.setOnClickListener {
                if (state == "BACK_SIDE") {
                    if (tts!!.isSpeaking) {

                    } else {
                        speakOut()
                    }
                } else {
                    if (tts!!.isSpeaking) {

                    } else {
                        speakQuestion()
                    }
                }
            }

        }

        getQuestions(quizId, quizName!!)

        top_bar_cancle_button.setOnClickListener {
            //perform on backpress on click of close button
            onBackPressed()
        }

        // for the first time
        speak_buton.setOnClickListener {
            if (tts!!.isSpeaking) {

            } else {
                speakQuestion()
            }
        }
        next_button.setOnClickListener {
            nextQuestion()
            stopSpeaking()
        }
        previous_button.setOnClickListener {
            previousQuestion()
            stopSpeaking()
        }

        Log.i("state", " state " + easyFlipView.currentFlipState)


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

            var speachListener = object : UtteranceProgressListener() {
                @SuppressLint("LongLogTag")
                override fun onDone(string: String?) {

                }

                override fun onError(p0: String?) {
                    Toast.makeText(this@Learn_quiz, "Error cannot speak", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Error cannot speak", Toast.LENGTH_SHORT).show()
        }

    }

    //open home activtiy on backpressed
    override fun onBackPressed() {
        finish()
    }


    //to get the question at the end
    fun getQuestions(quizId: Int, quizName: String) {

        //create api client first
        val apiToken: String = AppSharedPreference(this).getString("apiToken")

        //intialize the normal quiz management interface
        requestInterface = ApiClient.getClient(apiToken).create(NormalQuizManagementInterface::class.java)

        //attach your get method with call object
        val getQuestionCall: Call<GetNormalQuestionListResponceModel>? = requestInterface!!.getQuestions(quizId)

        //request the data from backend
        getQuestionCall!!.enqueue(object : Callback<GetNormalQuestionListResponceModel> {

            //on request fail
            override fun onFailure(call: Call<GetNormalQuestionListResponceModel>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(this@Learn_quiz, "Error", this@Learn_quiz.resources.getString(R.string.error_server_problem))

            }

            //on response recieved
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<GetNormalQuestionListResponceModel>, response: Response<GetNormalQuestionListResponceModel>) {
                //mProgressDialog.dialog.dismiss()
                if (response.isSuccessful) {

                    try {
                        //get JSON object questions as array list of QuestionResponseModel
                        questionsList = response.body()!!.questions!!
                        //try to parse the question description from the question title
                        questionModel = questionsList[0]
                        questionDescription = (questionModel.question_title.subSequence(questionModel.question_title.indexOf(";"), questionModel?.question_title!!.length)).toString()
                        //set the question title after removing the description from it
                        text_of_question.text = questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";"))
                        totalQuestion = questionsList.size.toInt()
                        questionNo_text.text = "Question ${currentQuestion} / $totalQuestion"
                        val imageUrl = questionModel.question_image
                        Log.i("image url", "url " + imageUrl)
                        //If image is available it will be displayed
                        if (imageUrl != "") {
                            Picasso.get()
                                    .load(imageUrl)
                                    .into(image_of_question)
                        } else if (imageUrl == "") {
                            val params = text_of_question.getLayoutParams() as RelativeLayout.LayoutParams
                            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                            params.addRule(RelativeLayout.CENTER_IN_PARENT)
                            text_of_question.layoutParams = params
                        }

                        Log.e(TAG, "question" + questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";")))
                        result.questionText = questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";")) as String?
                        Log.e(TAG, "question list size ${questionsList.size.toString()}")
                        Log.e(TAG, "current $currentQuestion")

                        //If there's only 1 question then both buttons will become invisible
                        if (questionsList.size == 1) {
                            previous_button.visibility = View.INVISIBLE
                            next_button.visibility = View.INVISIBLE
                        } else {
                            previous_button.visibility = View.INVISIBLE
                        }

                        // for getting correct option

                        val requestData = HashMap<String, Int>()

                        requestData["quiz_id"] = quizId //add quiz id to request data
                        requestData["question_id"] = questionModel.question_id //add question id to request data
                        requestData["time_remaining"] = 0 //add time remaining to request data
                        requestData["question_time"] = questionModel.question_time //add quiz time to request data
                        requestData["option_id"] = 0 //add selected option id to request data
                        //get correct option data from normal quiz management

                        getCorrectOption(requestData)


                    } catch (e: java.lang.Exception) {
                        //if eror occurs then set the question without parsing
                        text_of_question.text = questionModel.question_title
                        totalQuestion = questionsList.size.toInt()
                        questionNo_text.text = "Question ${currentQuestion} / $totalQuestion"
                        val imageUrl = questionModel.question_image
                        //If image is available it will be displayed
                        Log.i("image url", "url " + imageUrl)
                        if (imageUrl != "") {
                            Picasso.get()
                                    .load(imageUrl)
                                    .into(image_of_question)
                        } else if (imageUrl == "") {
                            val params = text_of_question.getLayoutParams() as RelativeLayout.LayoutParams
                            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                            params.addRule(RelativeLayout.CENTER_IN_PARENT)
                            text_of_question.layoutParams = params
                        }

                        result.questionText = questionModel.question_title
                        val requestData = HashMap<String, Int>()

                        requestData["quiz_id"] = quizId //add quiz id to request data
                        requestData["question_id"] = questionModel.question_id //add question id to request data
                        requestData["time_remaining"] = 0 //add time remaining to request data
                        requestData["question_time"] = questionModel.question_time //add quiz time to request data
                        requestData["option_id"] = 0 //add selected option id to request data
                        //get correct option data from normal quiz management

                        //If there's only 1 question then both buttons will become invisible
                        if (questionsList.size == 1) {
                            previous_button.visibility = View.INVISIBLE
                            next_button.visibility = View.INVISIBLE
                        } else {
                            previous_button.visibility = View.INVISIBLE
                        }

                        getCorrectOption(requestData)

                        System.out.println("no description found")
                    }
                } else {
                    //if the response is not successfull then show the error

                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)

                }
            }
        })

    }


    fun previousQuestion() {

        next_button.visibility = View.VISIBLE
        currentQuestion -= 1

        if (currentQuestion == 1) {
            previous_button.visibility = View.INVISIBLE
            next_button.visibility = View.VISIBLE
        }

        try {
            questionModel = questionsList[currentQuestion - 1]
            text_of_question.text = questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";"))
            totalQuestion = questionsList.size.toInt()
            questionNo_text.text = "Question ${currentQuestion} / $totalQuestion"
            val imageUrl = questionModel.question_image
            //If image is available it will be displayed
            if (imageUrl != "") {
                Picasso.get()
                        .load(imageUrl)
                        .into(image_of_question)
            } else if (imageUrl == "") {
                val params = text_of_question.getLayoutParams() as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                text_of_question.layoutParams = params
            }
            Log.e(TAG, "previous clicked")
            val requestData = HashMap<String, Int>()

            requestData["quiz_id"] = quizId //add quiz id to request data
            requestData["question_id"] = questionModel.question_id //add question id to request data
            requestData["time_remaining"] = 0 //add time remaining to request data
            requestData["question_time"] = questionModel.question_time //add quiz time to request data
            requestData["option_id"] = 0 //add selected option id to request data
            //get correct option data from normal quiz management

            getCorrectOption(requestData)


        } catch (e: Exception) {
            questionModel = questionsList[currentQuestion - 1]
            text_of_question.text = questionModel.question_title
            totalQuestion = questionsList.size.toInt()
            questionNo_text.text = "Question ${currentQuestion} / $totalQuestion"
            val imageUrl = questionModel.question_image
            //If image is available it will be displayed
            if (imageUrl != "") {
                Picasso.get()
                        .load(imageUrl)
                        .into(image_of_question)
            } else if (imageUrl == "") {
                val params = text_of_question.getLayoutParams() as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                text_of_question.layoutParams = params
            }


            val requestData = HashMap<String, Int>()

            requestData["quiz_id"] = quizId //add quiz id to request data
            requestData["question_id"] = questionModel.question_id //add question id to request data
            requestData["time_remaining"] = 0 //add time remaining to request data
            requestData["question_time"] = questionModel.question_time //add quiz time to request data
            requestData["option_id"] = 0 //add selected option id to request data
            //get correct option data from normal quiz management

            getCorrectOption(requestData)

        }

    }

    fun nextQuestion() {

        totalQuestion = questionsList.size
        previous_button.visibility = View.VISIBLE
        currentQuestion += 1

        if (currentQuestion == totalQuestion) {
            next_button.visibility = View.INVISIBLE
            previous_button.visibility = View.VISIBLE
        }

        try {
            questionModel = questionsList[currentQuestion - 1]
            text_of_question.text = questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";"))
            totalQuestion = questionsList.size.toInt()
            questionNo_text.text = "Question ${currentQuestion} / $totalQuestion"
            val imageUrl = questionModel.question_image
            //If image is available it will be displayed
            if (imageUrl != "") {
                Picasso.get()
                        .load(imageUrl)
                        .into(image_of_question)
            } else if (imageUrl == "") {
                val params = text_of_question.getLayoutParams() as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                text_of_question.layoutParams = params
            }

            Log.e(TAG, "next clicked")

            val requestData = HashMap<String, Int>()

            requestData["quiz_id"] = quizId //add quiz id to request data
            requestData["question_id"] = questionModel.question_id //add question id to request data
            requestData["time_remaining"] = 0 //add time remaining to request data
            requestData["question_time"] = questionModel.question_time //add quiz time to request data
            requestData["option_id"] = 0 //add selected option id to request data
            //get correct option data from normal quiz management

            getCorrectOption(requestData)

        } catch (e: Exception) {
            questionModel = questionsList[currentQuestion - 1]
            text_of_question.text = questionModel.question_title
            totalQuestion = questionsList.size.toInt()
            questionNo_text.text = "Question ${currentQuestion} / $totalQuestion"
            val imageUrl = questionModel.question_image
            //If image is available it will be displayed
            if (imageUrl != "") {
                Picasso.get()
                        .load(imageUrl)
                        .into(image_of_question)
            } else if (imageUrl == "") {
                val params = text_of_question.getLayoutParams() as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                text_of_question.layoutParams = params
            }

            val requestData = HashMap<String, Int>()

            requestData["quiz_id"] = quizId //add quiz id to request data
            requestData["question_id"] = questionModel.question_id //add question id to request data
            requestData["time_remaining"] = 0 //add time remaining to request data
            requestData["question_time"] = questionModel.question_time //add quiz time to request data
            requestData["option_id"] = 0 //add selected option id to request data
            //get correct option data from normal quiz management

            getCorrectOption(requestData)

        }

    }

    fun getCorrectOption(requestData: Map<String, Int>) {

        //create client first
        val apiToken: String = AppSharedPreference(this).getString("apiToken")

        //intialize the normal quiz management interface
        requestInterface = ApiClient.getClient(apiToken).create(NormalQuizManagementInterface::class.java)

        //attach your call with your get method
        val getCorrectCall: Call<GetCorrectOptionResponceModel>? = requestInterface!!.getCorrectOption(requestData)

        //request for your data
        getCorrectCall!!.enqueue(object : Callback<GetCorrectOptionResponceModel> {
            override fun onFailure(call: Call<GetCorrectOptionResponceModel>, t: Throwable) {
                //mProgressDialog.hide()
                AppAlerts().showAlertMessage(this@Learn_quiz, "Error", this@Learn_quiz.resources.getString(R.string.error_server_problem))
            }

            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<GetCorrectOptionResponceModel>, response: Response<GetCorrectOptionResponceModel>) {

                if (response.isSuccessful) {
                    //get JSON Object correct_answer_id from response
                    val correctOptionId: Int = response.body()!!.correct_answer_id
                    val correctOptionText: String = response.body()!!.correct_answer_value
                    Log.e(TAG, "Correct result $correctOptionId and string is $correctOptionText")
                    text_of_answer.text = correctOptionText

                } else {
                    //if the response is not successfull then show the error
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)

                }

            }

        })
    }


    private fun speakQuestion() {
        val speakQuestion: String = text_of_question.text.toString()
        val text: String = speakQuestion
        tts!!.setSpeechRate(0.75F)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }

    }

    private fun speakOut() {
        val speakAnswer: String = text_of_answer.text.toString()
        val text = speakAnswer
        tts!!.setSpeechRate(0.75F)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }

    }

    private fun stopSpeaking() {
        if (tts!!.isSpeaking) {
            tts?.stop()
        }
    }

    private fun isSessionExpire(errorMsgModle: CommonResponceModel) {
        //if error message status is equal to 404
        if (errorMsgModle.status == 404) {

            //show the message to user
            Toast.makeText(this, "Your Session Is Expire. Login Again For Continue.", Toast.LENGTH_LONG).show()

            //delete all shared preferences
            AppSharedPreference(this).deleteSharedPreference()

            //start login activity
            this.startActivity(Intent(this, ActivitySignIn::class.java)
                    .putExtra("from", "fail"))//add set from key value to "fail"

            //finish current activity
            this.finish()

        } else {
            //display other message
            AppAlerts().showAlertMessage(this, "Error", errorMsgModle.message)
        }
    }


}
