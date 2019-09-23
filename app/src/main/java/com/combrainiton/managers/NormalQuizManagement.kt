@file:Suppress("DEPRECATION", "NAME_SHADOWING")

package com.combrainiton.managers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import com.combrainiton.*
import com.combrainiton.adaptors.AdaptorMyQuizzesList
import com.combrainiton.models.*
import com.combrainiton.api.ApiClient
import com.combrainiton.api.ApiErrorParser
import com.combrainiton.authentication.ActivitySignIn
import com.combrainiton.main.ActivityNavExplore
import com.combrainiton.normalQuiz.ActivityNormalQuizDescription
import com.combrainiton.normalQuiz.ActivityNormalQuizQuestion
import com.combrainiton.normalQuiz.ActivityNormalQuizResult
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.sneaker_custom_view.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt


/**
 * Created by Dipendra Sharma  on 31-10-2018.
 */
class NormalQuizManagement(var mContext: Context, var mActivity: Activity, var mProgressDialog: AppProgressDialog, requestDataMap: Map<String, String>) {

    private var requestDataMap: Map<String, String> = HashMap()
    private var requestInterface: NormalQuizManagementInterface? = null
    private var currentScore: Double = 0.0
    private val TAG = "NormalQuizManagement"

    init {
        this.requestDataMap = requestDataMap
    }


    constructor(mContext: Context, mActivity: Activity, mProgressDialog: AppProgressDialog) : this(mContext, mActivity, mProgressDialog, HashMap())

    //for getting all quiz list for home page categorywise
    fun getAllQuiz() {
        //get api client first
        requestInterface = ApiClient.getClient().create(NormalQuizManagementInterface::class.java)

        val getQuizCall: Call<GetAllQuizResponceModel>? = requestInterface!!.getAllQuiz()

        getQuizCall!!.enqueue(object : Callback<GetAllQuizResponceModel> {

            //called when http request fails
            override fun onFailure(call: Call<GetAllQuizResponceModel>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(mContext, "Error", mContext.resources.getString(R.string.error_server_problem))
            }

            //called when http response is recieved
            override fun onResponse(call: Call<GetAllQuizResponceModel>, response: Response<GetAllQuizResponceModel>) {
                //TODO after adding the progress bar replace it with relevant statement to hide or dismiss your new progress bar
                //DO NOT DELETE this commented line anywhere from the project
                //mProgressDialog.dialog.dismiss()
                if (response.isSuccessful) { //if response from the backend is recieved\

                    //parse the JSON object quizzes as array list of GetAllQuizResponceModel.AllQuizzes
                    val quizList: ArrayList<GetAllQuizResponceModel.Allquizzes>? = response.body()!!.quizzes as ArrayList<GetAllQuizResponceModel.Allquizzes>?
                    //parse the JSON object featured_quizzes as array list of GetAllQuizResponceModel.AllQuizzes
                    val featuredQuizzes: ArrayList<GetAllQuizResponceModel.Allquizzes>? = response.body()!!.featured_quizzes as ArrayList<GetAllQuizResponceModel.Allquizzes>?
                    //parse the JSON object categories as array list of GetAllQuizResponceModel.CategoryList
                    val categoryList: ArrayList<GetAllQuizResponceModel.CategoryList>? = response.body()!!.categories as ArrayList<GetAllQuizResponceModel.CategoryList>?
                    categoryList!!.add(GetAllQuizResponceModel.CategoryList(0, "quizzes"))

                    //TODO this should be taken care of at the backend side remove it after confirmation
                    //redudant code to replace null values from http response

                    //start activity explore and pass all three list recieve from the response
                    mActivity.startActivity(Intent(mContext, ActivityNavExplore::class.java)
                            .putExtra("allQuiz", quizList) //all quiz list
                            .putExtra("featured_quizzes", featuredQuizzes) //featured quiz list
                            .putExtra("categoryList", categoryList)) //category list
                    mActivity.finish() //finish current activtiy

                } else {
                    //if the response is not successfull then show the error
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)
                }
            }
        })
    }

    //for getting questions by quiz id
    fun getQuestions(quizId: Int,quizName: String) {

        //create api client first
        val apiToken: String = AppSharedPreference(mContext).getString("apiToken")

        //intialize the normal quiz management interface
        requestInterface = ApiClient.getClient(apiToken).create(NormalQuizManagementInterface::class.java)

        //attach your get method with call object
        val getQuestionCall: Call<GetNormalQuestionListResponceModel>? = requestInterface!!.getQuestions(quizId)

        //request the data from backend
        getQuestionCall!!.enqueue(object : Callback<GetNormalQuestionListResponceModel> {

            //on request fail
            override fun onFailure(call: Call<GetNormalQuestionListResponceModel>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(mContext, "Error", mContext.resources.getString(R.string.error_server_problem))

            }

            //on response recieved
            override fun onResponse(call: Call<GetNormalQuestionListResponceModel>, response: Response<GetNormalQuestionListResponceModel>) {
                //mProgressDialog.dialog.dismiss()
                if (response.isSuccessful) {

                    //get JSON object questions as array list of QuestionResponseModel
                    val questionsList: ArrayList<QuestionResponceModel>? = response.body()!!.questions
                    Log.e(TAG,"questions $questionsList")
                    //get JSON object quiz id as integer from response
                    val qid = response.body()!!.quiz_id


                    //start normal quiz activtiy
                    mActivity.startActivity(Intent(mContext, ActivityNormalQuizQuestion::class.java)
                            .putExtra("questionList", questionsList) //pass question list
                            .putExtra("quizId", qid)
                            .putExtra("quizName",quizName))//pass quiz id
                    //mActivity.finish()

                } else {
                    //if the response is not successfull then show the error
                    Log.e(TAG,response.errorBody().toString())
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)
                }
            }
        })

    }

    //for getting correct option
    fun getCorrectOption(result: ObjectQuizResult, requestData: Map<String, Int>,
                         oldProgressBarContainer: RelativeLayout, QuestionResultViewContainer: LinearLayout,
                         topBarQuestionResultText: TextView, optionTVArray: Array<TextView>?, userAnswerOptionId: Int,
                         imgCorrectIncorrect: ImageView, tvCorrectIncorrect: TextView, resultViewTotalScore: TextView,
                         QuestionResultView: RelativeLayout, scoreCard: androidx.cardview.widget.CardView,actvity_quiz_question_next_button_for_question: CardView) : Sneaker{

        var sneaker= Sneaker.with(mActivity)

        //create client first
        val apiToken: String = AppSharedPreference(mContext).getString("apiToken")

        //intialize the normal quiz management interface
        requestInterface = ApiClient.getClient(apiToken).create(NormalQuizManagementInterface::class.java)

        //attach your call with your get method
        val getCorrectCall: Call<GetCorrectOptionResponceModel>? = requestInterface!!.getCorrectOption(requestData)

        //request for your data
        getCorrectCall!!.enqueue(object : Callback<GetCorrectOptionResponceModel> {

            //on request fail
            override fun onFailure(call: Call<GetCorrectOptionResponceModel>, t: Throwable) {
                //mProgressDialog.hide()
                AppAlerts().showAlertMessage(mContext, "Error", mContext.resources.getString(R.string.error_server_problem))
            }

            //on reponse recieved
            override fun onResponse(call: Call<GetCorrectOptionResponceModel>, response: Response<GetCorrectOptionResponceModel>) {
                //mProgressDialog.hide()

                //if response is successful
                if (response.isSuccessful) {

                    //get JSON Object correct_answer_id from response
                    val correctOptionId: Int = response.body()!!.correct_answer_id
                    Log.e(TAG,"Correct result $correctOptionId and string is ${response.body()!!.correct_answer_value}")

                    //set backgrounds of wrong options to red and correct option to green
                    setOptionBackground(R.drawable.shape_answer_right, correctOptionId, optionTVArray!!, result)

                    //TODO remove all used of old progress bar from the project
                    //set the old progress bar container visibility to gone
                    oldProgressBarContainer.visibility = View.GONE

                    //set the result view screen visibility to visible
                    QuestionResultViewContainer.visibility = View.VISIBLE

                    //get the stored point from last question
                    val lastPoint: Double = AppSharedPreference(mContext).getString("normalQuizPoint").toDouble()

                    //add the current point to the last point
                    val currentTotalPoint: Double = lastPoint + response.body()!!.score
                    currentScore = response.body()!!.score

                    //store the current normal quiz point in the share prefrences
                    AppSharedPreference(mContext).saveString("normalQuizPoint", currentTotalPoint.toString())

                    //set total score value in result screen
                    val score: String = mContext.getString(R.string.text_total_score)+ " : " + currentTotalPoint.toInt().toString()

                    resultViewTotalScore.text = score

                    when {

                        //when user has not answered anything
                        userAnswerOptionId == 0 -> {

                            //If timer is up, the next question button will be shown
                            actvity_quiz_question_next_button_for_question.visibility = View.VISIBLE

                            if(AppSharedPreference(mContext).getBoolean("sound")) {
                                val mp = MediaPlayer.create(mContext, R.raw.wrong)
                                mp.start()
                            }

                            result.isUserAnswerCorrect = false

                            //set total score value in result screen
                            val score: String = mContext.getString(R.string.text_total_score)+ " : " + currentTotalPoint.toInt().toString()

                            resultViewTotalScore.text = score

                            //set the question result color to red
                            //topBarQuestionResultText.setTextColor(mContext.resources.getColor(R.color.colorCategoryThree))

                            //set the question result text to top bar question rsult text 1
                            //topBarQuestionResultText.text = mContext.getString(R.string.top_bar_question_result_text_1)

                            //TODO we no longet use question point text view remove it's usage from here and from other methods as well
                            //tvQuestionPoint.text = "+ 0"


                            //set the result view screen visibility to visible
                            QuestionResultViewContainer.visibility = View.VISIBLE

                            //set the question result screen color to blue
                            QuestionResultView.setBackgroundColor(mContext.resources.getColor(R.color.colorCategoryTwo))

                            //set image visibility to visible
                            imgCorrectIncorrect.visibility = View.VISIBLE

                            //set image time up
                            imgCorrectIncorrect.setImageResource(R.drawable.ic_time_up)

                            //set text to rsult screen text 1
                            tvCorrectIncorrect.text = mContext.getString(R.string.result_screen_question_result_text_1)

                            //set scroe visible
                            scoreCard.visibility = View.VISIBLE

                        }
                        //when user answer is not correct
                        correctOptionId != userAnswerOptionId -> {

                            if(AppSharedPreference(mContext).getBoolean("sound")) {
                                val mp = MediaPlayer.create(mContext, R.raw.wrong)
                                mp.start()
                            }

                            //Adding custom view in sneaker and displaying
                            val view = LayoutInflater.from(mContext).inflate(R.layout.sneaker_custom_view,sneaker.getView(),false)

                            //Setting background color
                            view.sneaker_linearLayout.setBackgroundColor(mActivity.resources.getColor(R.color.colorCategoryThree))

                            //Getting random response
                            val random = (0..8).shuffled().take(1)
                            val response = randomIncorrectResponse(random[0])

                            //Setting random response and answer feedback
                            view.sneaker_textViewResult.text = response

                            //Setting current score value to 0 as answer selected was not correct
                            view.sneaker_score.text = "+0"

                            //Setting sneaker properties
                            sneaker.setDuration(60000)
                            sneaker.sneakCustom(view)

                            result.isUserAnswerCorrect = false

                            //set image visbility to visible
                            imgCorrectIncorrect.visibility = View.VISIBLE

                            //set score visible
                            scoreCard.visibility = View.VISIBLE

                            //set background of other answers to transperant and user answer to opaque
                            setOptionBackground(R.drawable.shape_answer_wrong, userAnswerOptionId, optionTVArray, result)

                            //set top bar result text color to red
                            //topBarQuestionResultText.setTextColor(mContext.resources.getColor(R.color.colorCategoryThree))

                            //set text to topbar text 2
                            //topBarQuestionResultText.text = mContext.getString(R.string.top_bar_question_result_text_2)

                            //tvQuestionPoint.text = "+0"

                            //set result screen backgroun to red
                            QuestionResultView.setBackgroundColor(mContext.resources.getColor(R.color.colorCategoryThree))

                            //llResultView.background=mContext.resources.getColor(R.color.colorWrongRed)
                            imgCorrectIncorrect.setImageResource(R.drawable.ic_cross_opaque)

                            //set result screen text to result text 2
                            tvCorrectIncorrect.text = mContext.getString(R.string.result_screen_question_result_text_2)

                        }
                        //when user answer is correct
                        correctOptionId == userAnswerOptionId -> {
                            if(AppSharedPreference(mContext).getBoolean("sound")) {
                                val mp = MediaPlayer.create(mContext, R.raw.right)
                                mp.start()
                            }

                            //Adding custom view in sneaker and displaying
                            val view = LayoutInflater.from(mContext).inflate(R.layout.sneaker_custom_view,sneaker.getView(),false)

                            //Getting random response
                            val random = (0..8).shuffled().take(1)
                            val response = randomCorrectResponse(random[0])

                            //Setting random response and answer feedback
                            view.sneaker_textViewResult.text = response

                            //Setting current score value and background
                            view.sneaker_score.setBackgroundColor(mActivity.resources.getColor(R.color.correctAnswerPoint))
                            view.sneaker_score.text = "+"+ currentScore.roundToInt().toString()

                            //Setting background color
                            view.sneaker_linearLayout.setBackgroundColor(mActivity.resources.getColor(R.color.colorCategoryFour))

                            //Setting sneaker properties
                            sneaker.setDuration(10000)
                            sneaker.sneakCustom(view)


                            result.isUserAnswerCorrect = true

                            //set image visible
                            imgCorrectIncorrect.visibility = View.VISIBLE

                            //set image resource to correct image
                            imgCorrectIncorrect.setImageResource(R.drawable.ic_check_result)

                            //set score cord visbile
                            scoreCard.visibility = View.VISIBLE

                            //set top bar text color to green
                            //topBarQuestionResultText.setTextColor(mContext.resources.getColor(R.color.colorCategoryFour))

                            //set text to top bar text 3
                            //topBarQuestionResultText.text = mContext.getString(R.string.top_bar_question_result_text_3)

                            //tvQuestionPoint.text = "+" + response.body()!!.score.toInt().toString()

                            //set result screen background to greeen
                            QuestionResultView.setBackgroundColor(mContext.resources.getColor(R.color.colorCategoryFour))

                            //set result screen text to result text 3
                            tvCorrectIncorrect.text = mContext.getString(R.string.result_screen_question_result_text_3)

                        }

                    }

                } else {
                    //if the response is not successfull then show the error
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)
                }
            }
        })

        //returning so sneaker can be dismissed on next question button click
        return sneaker
    }

    private fun randomCorrectResponse(index: Int):String{
        var response = arrayOf("That’s right","Spot on","You’ve nailed it","Great going","Correct","Great job","Bull's eye","Well done","Awesome")

        return response[index]
    }

    private fun randomIncorrectResponse(index: Int):String{
        var response = arrayOf("That's not right","You are mistaken","You've got it wrong","It's no go","Bad mistake","Incorrect","Mistaken","Oops that's wrong","Unlucky")

        return response[index]
    }

    //set the bckgroungs of options as per user input
    private fun setOptionBackground(answer_bg: Int, optionId: Int, optionTVArray: Array<TextView>, result: ObjectQuizResult) {
        for (i in optionTVArray.indices) {
            val optionIdInt: Int = optionTVArray[i].tag as Int //get option tag from option view
            if (optionId == optionIdInt) { //if option id is equal to option tag
                if (answer_bg == R.drawable.shape_answer_right) { //if drawable is rigt answer
                    result.correctOptionText = optionTVArray[i].text as String? //
                } else if (answer_bg == R.drawable.shape_answer_wrong) {
                    result.userOptionText = optionTVArray[i].text as String?
                }
                optionTVArray[i].background = mContext.resources.getDrawable(answer_bg) //set background of option
            } else {
                if (answer_bg == R.drawable.shape_answer_right) { //if user selected option is right
                    //all other option will be set to transperant red color
                    optionTVArray[i].background = mContext.resources.getDrawable(R.color.colorCategoryThreeDisabled)
                }
            }

        }

    }

    //for getting quiz user score
    fun getQuizScore(requestData: GetNormalQuizScoreRequestModel, scoreBoardResult: ObjectQuizResult,quizName: String) {

        //get client first
        val apiToken: String = AppSharedPreference(mContext).getString("apiToken")

        //initialize the normalquizmanagement interface
        requestInterface = ApiClient.getClient(apiToken).create(NormalQuizManagementInterface::class.java)

        //attach your get method with call object
        val getScoreCall: Call<GetNormalQuizScoreResponceModel>? = requestInterface!!.getQuizScore(requestData)


        //request for data
        getScoreCall!!.enqueue(object : Callback<GetNormalQuizScoreResponceModel> {

            //on request fail
            override fun onFailure(call: Call<GetNormalQuizScoreResponceModel>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(mContext, "Error", mContext.getString(R.string.error_server_problem))
            }

            //on response recieved
            override fun onResponse(call: Call<GetNormalQuizScoreResponceModel>, response: Response<GetNormalQuizScoreResponceModel>) {

                //mProgressDialog.dialog.dismiss()

                //if reponse is successful
                if (response.isSuccessful) {

                    //parse JSON data as GetNormalQuizScoreResponceModel
                    val scoreData: GetNormalQuizScoreResponceModel = response.body()!!

                    //start result activity
                    mActivity.startActivity(Intent(mContext, ActivityNormalQuizResult::class.java)
                            .putExtra("scoreData", scoreData)//add score data
                            .putExtra("allData", scoreBoardResult.data)
                            .putExtra("quizName",quizName)
                            .putExtra("quizId",requestData.quiz_id))//add all other data
                    mActivity.finish()//close current activity

                } else {
                    //if the response is not successfull then show the error
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)
                }
            }
        })
    }

    //for getting list of recent quiz played by user
    fun getRecentQuiz(recyclerViewForMyQuizzes: androidx.recyclerview.widget.RecyclerView) {

        //get client first
        val apiToken: String = AppSharedPreference(mContext).getString("apiToken")
        Log.e(TAG,apiToken.toString())

        //initialize the normal quiz interface object
        requestInterface = ApiClient.getClient(apiToken).create(NormalQuizManagementInterface::class.java)

        //attach your call with your get method
        val getRecentCall = requestInterface!!.getRecentQuiz()

        //request for data
        getRecentCall.enqueue(object : Callback<GetUserRecentAllQuizResponceModel> {

            //on request fail
            override fun onFailure(call: Call<GetUserRecentAllQuizResponceModel>, t: Throwable) {
                //mProgressDialog.hide()
                AppAlerts().showAlertMessage(mContext, "Error", mContext.resources.getString(R.string.error_server_problem))
            }

            //on response recived
            override fun onResponse(call: Call<GetUserRecentAllQuizResponceModel>, response: Response<GetUserRecentAllQuizResponceModel>) {
                //mProgressDialog.hide()

                //if reponse is successful
                if (response.isSuccessful) {

                    //parse JSON Object quizzes as GetUserRecentAllQuizResponceModel
                    val recentDataList: ArrayList<GetUserRecentAllQuizResponceModel.RecentQuizList>? = response.body()!!.quizzes

                    //set linear layout manager for my quiizes recylcer view
                    recyclerViewForMyQuizzes.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(mContext)

                    //set adapter to recycler view
                    recyclerViewForMyQuizzes.adapter = AdaptorMyQuizzesList(mContext, mActivity, recentDataList!!)

                } else {
                    //if the response is not successfull then show the error
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)
                }
            }
        })
    }

    //for getting single quiz detail
    fun getQuizDetail(quizId: Int) {

        //get api client
        val apiToken: String = AppSharedPreference(mContext).getString("apiToken")

        //initialize the normal quiz interface object
        requestInterface = ApiClient.getClient(apiToken).create(NormalQuizManagementInterface::class.java)

        //attach you call to your get method
        val getDetailCall = requestInterface!!.getQuizDetail(quizId)

        //request for data
        getDetailCall.enqueue(object : Callback<QuizDetailResponceModel> {

            //on request fail
            override fun onFailure(call: Call<QuizDetailResponceModel>, t: Throwable) {
                //mProgressDialog.hide()
                AppAlerts().showAlertMessage(mContext, "Error", mContext.getString(R.string.error_server_problem))
            }

            //if response is receieved
            override fun onResponse(call: Call<QuizDetailResponceModel>, response: Response<QuizDetailResponceModel>) {

                //mProgressDialog.hide()

                //if response is successful
                if (response.isSuccessful) {

                    //start quiz description activity
                    mActivity.startActivity(Intent(mContext, ActivityNormalQuizDescription::class.java)
                            .putExtra("quizId", response.body()!!.quiz_id)//add quiz id
                            .putExtra("description", response.body()!!.description)//add description
                            .putExtra("totalQuestion", response.body()!!.total_questions)//add total no of question
                            .putExtra("quizName", response.body()!!.quiz_title)//add quiz name
                            .putExtra("hostName", response.body()!!.host_name)//add host name
                            .putExtra("image", response.body()!!.image_url))//add image url

                    //finish the cuurect activity
                    mActivity.finish()

                } else {
                    //if the response is not successfull then show the error
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)
                }
            }
        })
    }

    //this method is called to check use login status
    private fun isSessionExpire(errorMsgModle: CommonResponceModel) {
        //if error message status is equal to 404
        if (errorMsgModle.status == 404) {

            //show the message to user
            Toast.makeText(mContext, "Your Session Is Expire. Login Again For Continue.", Toast.LENGTH_LONG).show()

            //delete all shared preferences
            AppSharedPreference(mContext).deleteSharedPreference()

            //start login activity
            mActivity.startActivity(Intent(mContext, ActivitySignIn::class.java)
                    .putExtra("from", "fail"))//add set from key value to "fail"

            //finish current activity
            mActivity.finish()

        } else {
            //display other message
            AppAlerts().showAlertMessage(mContext, "Error", errorMsgModle.message)
            Log.e(TAG,errorMsgModle.message)
        }
    }
}