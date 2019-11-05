package com.combrainiton.normalQuiz

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.api.ApiClient
import com.combrainiton.api.ApiErrorParser
import com.combrainiton.authentication.ActivitySignIn
import com.combrainiton.managers.NormalQuizManagementInterface
import com.combrainiton.models.*
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppSharedPreference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_learn_quiz.*
import kotlinx.android.synthetic.main.demo_result.*
import kotlinx.android.synthetic.main.flash_card_layout_back.*
import kotlinx.android.synthetic.main.flash_card_layout_front.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.wajahatkarim3.easyflipview.EasyFlipView
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.Gravity
import android.widget.RelativeLayout
import android.R.attr.button
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class Learn_quiz : AppCompatActivity() {
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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_quiz)

        quizId = intent.getIntExtra("quizId", 0)
        quizName = intent.getStringExtra("quizName")

        Log.i("data" ," data from intent" + quizId + " " + quizName)


        easyFlipView.flipDuration = 500
        easyFlipView.isFlipEnabled = true

        easyFlipView.onFlipListener = EasyFlipView.OnFlipAnimationListener { flipView, newCurrentSide ->

            val state : String = newCurrentSide.toString()

            previous_button.setOnClickListener {

            if (state == "BACK_SIDE" ){
                easyFlipView.flipTheView(true)
                previousQuestion()
            }
                previousQuestion()
            }
            next_button.setOnClickListener {

            if (state.equals("BACK_SIDE") ){
                easyFlipView.flipTheView(true)
                nextQuestion()
            }
                nextQuestion()
            }

        }

        getQuestions(quizId, quizName!!)

        // for y=the first time

        next_button.setOnClickListener {
            nextQuestion()
        }
        previous_button.setOnClickListener {
            previousQuestion()
        }

        Log.i("state", " state " + easyFlipView.currentFlipState)


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
                        val imageUrl = questionModel.question_image
                        Log.i("image url" , "url " + imageUrl)
                        //If image is available it will be displayed
                        if(imageUrl != "") {
                            Picasso.get()
                                    .load(imageUrl)
                                    .into(image_of_question)
                        }else if(imageUrl == ""){
                            val params = text_of_question.getLayoutParams() as RelativeLayout.LayoutParams
                            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                            params.addRule(RelativeLayout.CENTER_IN_PARENT)
                            text_of_question.layoutParams = params
                        }

                        Log.e(TAG, "question" + questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";")))
                        result.questionText = questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";")) as String?
                        //result_Cell_questionNo_text.text = "Question $currentQuestion"
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
                        val imageUrl = questionModel.question_image
                        //If image is available it will be displayed
                        Log.i("image url" , "url " + imageUrl)
                        if(imageUrl != "") {
                            Picasso.get()
                                    .load(imageUrl)
                                    .into(image_of_question)
                        }else if(imageUrl == ""){
                            val params = text_of_question.getLayoutParams() as RelativeLayout.LayoutParams
                            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                            params.addRule(RelativeLayout.CENTER_IN_PARENT)
                            text_of_question.layoutParams = params
                        }

                        result.questionText = questionModel.question_title
                        //result_Cell_questionNo_text.text = "Question ${currentQuestion}"
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


    fun previousQuestion()
    {

        next_button.visibility = View.VISIBLE
        currentQuestion -= 1

        if (currentQuestion == 1) {
            previous_button.visibility = View.INVISIBLE
            next_button.visibility = View.VISIBLE
        }

        try {
            questionModel = questionsList[currentQuestion - 1]
            text_of_question.text = questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";"))
            val imageUrl = questionModel.question_image
            //If image is available it will be displayed
            if(imageUrl != "") {
                Picasso.get()
                        .load(imageUrl)
                        .into(image_of_question)
            }else if(imageUrl == ""){
                val params = text_of_question.getLayoutParams() as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                text_of_question.layoutParams = params
            }
//            result_Cell_questionNo_text.text = "Question ${currentQuestion}"
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
            val imageUrl = questionModel.question_image
            //If image is available it will be displayed
            if(imageUrl != "") {
                Picasso.get()
                        .load(imageUrl)
                        .into(image_of_question)
            }else if(imageUrl == ""){
                val params = text_of_question.getLayoutParams() as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                text_of_question.layoutParams = params
            }

            //result_Cell_questionNo_text.text = "Question ${currentQuestion}"

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
            val imageUrl = questionModel.question_image
            //If image is available it will be displayed
            if(imageUrl != "") {
                Picasso.get()
                        .load(imageUrl)
                        .into(image_of_question)
            }else if(imageUrl == ""){
                val params = text_of_question.getLayoutParams() as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                text_of_question.layoutParams = params
            }

            //result_Cell_questionNo_text.text = "Question $currentQuestion"
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
            val imageUrl = questionModel.question_image
            //If image is available it will be displayed
            if(imageUrl != "") {
                Picasso.get()
                        .load(imageUrl)
                        .into(image_of_question)
            }else if(imageUrl == ""){
                val params = text_of_question.getLayoutParams() as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                text_of_question.layoutParams = params
            }

            //result_Cell_questionNo_text.text = "Question $currentQuestion"
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
