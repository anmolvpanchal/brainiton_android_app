package com.combrainiton.normalQuiz

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.combrainiton.BuildConfig
import com.combrainiton.R
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_normal_quiz_result.*
import java.io.File
import java.io.FileOutputStream
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.os.Build
import android.os.StrictMode
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.combrainiton.adaptors.ResultQuizRecAdapter
import com.combrainiton.api.ApiClient
import com.combrainiton.api.ApiErrorParser
import com.combrainiton.authentication.ActivitySignIn
import com.combrainiton.managers.NormalQuizManagementInterface
import com.combrainiton.models.*
import com.combrainiton.utils.AppAlerts
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.activity_quiz_question.*
import lecho.lib.hellocharts.formatter.ValueFormatterHelper
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivityNormalQuizResult : AppCompatActivity() {

    private lateinit var allData: ArrayList<ObjectQuizResult>

    /** Graph experiment */
    val entries = ArrayList<BarEntry>()
    val iDataSet = ArrayList<IBarDataSet>()
    lateinit var dataSet:BarDataSet
    /** end */

    private var quizId: Int = 0
    private var quizScore: String? = null
    private var playerName: String? = null
    private var quizName: String? = null
    private var userName: String? = null
    private var imageFileToShare: File? = null
    private var questionDescription: String = ""
    private var requestInterface: NormalQuizManagementInterface? = null
    private var currentQuestion: Int = 0
    var questionsList: ArrayList<QuestionResponceModel> = ArrayList()
    val quiz_result_recycler: RecyclerView? = null
    var totalQuestion: Int = 0
    private lateinit var questionModel: QuestionResponceModel
    private val result: ObjectQuizResult = ObjectQuizResult()
    private var correctAnswerList = ArrayList<String>()


    private val TAG: String = "ActivityNormalQuizResult"    // to check the log


    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_quiz_result)

        val i:Bundle = intent.extras

        quizId = intent.getIntExtra("quizId", 0)
        quizName = intent.getStringExtra("quizName")
        userName = AppSharedPreference(this@ActivityNormalQuizResult).getString("name")


        /** Graph experiment */

        val mDialog = AppProgressDialog(this@ActivityNormalQuizResult)
        mDialog.show() //show progress dialog

        val graph = findViewById<BarChart>(R.id.chart)

        entries.add(BarEntry(1.toFloat(),100.toFloat()))
        entries.add(BarEntry(2.toFloat(),220.toFloat()))
        entries.add(BarEntry(3.toFloat(),315.toFloat()))
        entries.add(BarEntry(4.toFloat(),408.toFloat()))
        entries.add(BarEntry(5.toFloat(),500.toFloat()))
        entries.add(BarEntry(6.toFloat(),600.toFloat()))
        entries.add(BarEntry(7.toFloat(),275.toFloat()))
        entries.add(BarEntry(8.toFloat(),365.toFloat()))
        entries.add(BarEntry(9.toFloat(),471.toFloat()))
        entries.add(BarEntry(10.toFloat(),558.toFloat()))
        entries.add(BarEntry(11.toFloat(),100.toFloat()))
        entries.add(BarEntry(12.toFloat(),220.toFloat()))
        entries.add(BarEntry(13.toFloat(),315.toFloat()))
        entries.add(BarEntry(14.toFloat(),408.toFloat()))
        entries.add(BarEntry(15.toFloat(),500.toFloat()))

        dataSet = BarDataSet(entries,"Result")
        dataSet.setColor(resources.getColor(R.color.colorAccent))

        //iDataSet.add(dataSet)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f
        graph.data = barData
        graph.setFitBars(false)
        //graph.invalidate()
        graph.getAxisRight().setEnabled(false)
        //graph.animateX(9000,Easing.EasingOption.EaseOutBack)
        graph.animateY(6000,Easing.EaseOutBack)
        graph.setPinchZoom(false)
        graph.setMaxVisibleValueCount(60)
        graph.setDrawBarShadow(false)
        graph.setDrawGridBackground(false)
        graph.setDrawValueAboveBar(true)
        graph.xAxis.valueFormatter = My()
        graph.isClickable = false
        graph.zoomToCenter(1f,1f)


        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f // only intervals of 1 day
        xAxis.labelCount = 7
        xAxis.valueFormatter = My()

        val leftAxis = chart.axisLeft
        leftAxis.setLabelCount(8, false)
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.spaceTop = 15f
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

        val rightAxis = chart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.setLabelCount(8, false)
        rightAxis.spaceTop = 15f
        rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

        val legend = chart.legend
        legend.setEnabled(false)

        val description = graph.description
        description.isEnabled = false //setEnabled()

        /** end */


        getQuestions(quizId, quizName!!)

        result_Cell_left_button.setOnClickListener {
            previousQuestion()
        }

        result_Cell_right_button.setOnClickListener {
            nextQuestion()
        }
//
//        quiz_result_recycler?.findViewById<RecyclerView>(R.id.quiz_result_recycler)
//        quiz_result_recycler?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
//        quiz_result_recycler?.adapter = ResultQuizRecAdapter(this,questionsList,quizId,quizName!!)

        initViews()

    }

    class My : ValueFormatter(){
        val question = arrayOf("0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15")

        override fun getFormattedValue(value: Float): String {
            return question.getOrNull(value.toInt()) ?: value.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        //challange_view.visibility = View.INVISIBLE
    }


    @SuppressLint("SetTextI18n", "LongLogTag")
    private fun initViews() {

        //challange_view.isDrawingCacheEnabled = true
        top_view.isDrawingCacheEnabled = true

        //get score data
        val scoreData = intent.getSerializableExtra("scoreData") as GetNormalQuizScoreResponceModel?
        quizScore = String.format("%.2f", scoreData!!.total_score)

        //set score data
        tvTotalScore.text = String.format("%.2f", scoreData!!.total_score)

        //set accuracy
        val accuracy = (100 * scoreData.correct_answers) / scoreData.total_questions
        tvAccuracy.text = "$accuracy%"

        //play sound if sound is enabled
        if (AppSharedPreference(this@ActivityNormalQuizResult).getBoolean("sound")) {
            val mp = MediaPlayer.create(applicationContext, R.raw.final_leaderboard)
            mp.start()
        }

    }

    @SuppressLint("LongLogTag")
    fun previousQuestion() {

        result_Cell_right_button.visibility = View.VISIBLE
        currentQuestion -= 1

        if (currentQuestion.equals(-1)) {
            result_Cell_left_button.visibility = View.INVISIBLE
        } else {

            try {
                questionModel = questionsList[currentQuestion]
                question_layout.text = questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";"))
                result_Cell_questionNo_text.text = "Question " + currentQuestion.toString()
                Log.e(TAG,"previous clicked")
                val requestData = HashMap<String, Int>()

                requestData["quiz_id"] = quizId //add quiz id to request data
                requestData["question_id"] = questionModel.question_id //add question id to request data
                requestData["time_remaining"] = 0 //add time remaining to request data
                requestData["question_time"] = questionModel.question_time //add quiz time to request data
                requestData["option_id"] = 0 //add selected option id to request data
                //get correct option data from normal quiz management

                getCorrectOption(requestData)


            } catch (e: Exception) {
                questionModel = questionsList[currentQuestion]
                question_layout.text = questionModel.question_title
                result_Cell_questionNo_text.text = "Question " + currentQuestion.toString()

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

    }

    @SuppressLint("LongLogTag")
    fun nextQuestion() {

        totalQuestion = questionsList.size
        result_Cell_left_button.visibility = View.VISIBLE
        currentQuestion += 1

        if (currentQuestion == totalQuestion) {
            result_Cell_right_button.visibility = View.INVISIBLE
        } else {

            try {
                questionModel = questionsList[currentQuestion]
                question_layout.text = questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";"))
                result_Cell_questionNo_text.text = "Question $currentQuestion"
                Log.e(TAG,"next clicked")

                val requestData = HashMap<String, Int>()

                requestData["quiz_id"] = quizId //add quiz id to request data
                requestData["question_id"] = questionModel.question_id //add question id to request data
                requestData["time_remaining"] = 0 //add time remaining to request data
                requestData["question_time"] = questionModel.question_time //add quiz time to request data
                requestData["option_id"] = 0 //add selected option id to request data
                //get correct option data from normal quiz management

                getCorrectOption(requestData)

            } catch (e: Exception) {
                questionModel = questionsList[currentQuestion]
                question_layout.text = questionModel.question_title
                result_Cell_questionNo_text.text = "Question $currentQuestion"
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
    }

    //to get the correctoption in the result activity
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
                AppAlerts().showAlertMessage(this@ActivityNormalQuizResult, "Error", this@ActivityNormalQuizResult.resources.getString(R.string.error_server_problem))
            }

            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<GetCorrectOptionResponceModel>, response: Response<GetCorrectOptionResponceModel>) {

                if (response.isSuccessful){
                    //get JSON Object correct_answer_id from response
                    val correctOptionId: Int = response.body()!!.correct_answer_id
                    val correctOptionText : String = response.body()!!.correct_answer_value
                    Log.e(TAG,"Correct result $correctOptionId and string is $correctOptionText")
                    correct_option_layout.text = "Correct ans is "+correctOptionText

                }else{
                    //if the response is not successfull then show the error
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)

                }

            }

        })
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
                AppAlerts().showAlertMessage(this@ActivityNormalQuizResult, "Error", this@ActivityNormalQuizResult.resources.getString(R.string.error_server_problem))

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
                        questionModel = questionsList[currentQuestion]
                        questionDescription = (questionModel.question_title.subSequence(questionModel.question_title.indexOf(";"), questionModel?.question_title!!.length)).toString()
                        //set the question title after removing the description from it
                        question_layout.text = questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";"))
                        Log.e(TAG, "question" + questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";")))
                        result.questionText = questionModel.question_title.subSequence(0, questionModel.question_title.indexOf(";")) as String?
                        result_Cell_questionNo_text.text = "Question $currentQuestion"
                        Log.e(TAG, "question list size ${questionsList.size.toString()}")
                        Log.e(TAG, "current $currentQuestion")

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
                        question_layout.text = questionModel.question_title
                        result.questionText = questionModel.question_title
                        val requestData = HashMap<String, Int>()

                        requestData["quiz_id"] = quizId //add quiz id to request data
                        requestData["question_id"] = questionModel.question_id //add question id to request data
                        requestData["time_remaining"] = 0 //add time remaining to request data
                        requestData["question_time"] = questionModel.question_time //add quiz time to request data
                        requestData["option_id"] = 0 //add selected option id to request data
                        //get correct option data from normal quiz management

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


    fun shareScore(view: View) {
        if (isReadStoragePermissionGranted() && isWriteStoragePermissionGranted() && forceToAccessPath()) {
            //challange_view.visibility = View.VISIBLE

            quizId = intent.getIntExtra("quizId", 0)
            quizName = intent.getStringExtra("quizName")
            userName = AppSharedPreference(this@ActivityNormalQuizResult).getString("name")

            try {
                val path = Environment.getExternalStorageDirectory().toString() + "/$quizId.png"
                val outputStream = FileOutputStream(File(path))
                //mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
            } catch (exception: Exception) {
                Toast.makeText(this, exception.message + "Could not share score", Toast.LENGTH_SHORT).show()
            }

            val imagePath: String = Environment.getExternalStorageDirectory().toString() + "/$quizId.png"
            imageFileToShare = File(imagePath)
            val uri = Uri.fromFile(imageFileToShare)
            try {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "image/*"
                share.putExtra(Intent.EXTRA_STREAM, uri)
                share.putExtra(Intent.EXTRA_TEXT, "You've been challenged to play a quiz '$quizName'.\nUse PIN : N$quizId to accept the challenge.\n\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n")
                startActivity(Intent.createChooser(share, "Challenge your friend"))

            } catch (e: Exception) {
                Toast.makeText(this, e.message + "Could not share score", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Please give permission to store score image ", Toast.LENGTH_SHORT).show()
        }
    }


    // to delete the stored image when activity is destroyed
    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Because app crashes sometimes without the try->catch
        try {
            // if file exists in memory
            if (imageFileToShare!!.exists()) {
                imageFileToShare!!.delete()
                Log.e(TAG, "image deleted :- $imageFileToShare")
            } else {
//                Toast.makeText(this, "image not deleted", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    // forcefully allowes devices to give access to path to the sotred image
    fun forceToAccessPath(): Boolean {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        } else {
            Toast.makeText(this@ActivityNormalQuizResult, " Problem getting the image ", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    @SuppressLint("LongLogTag")
    override fun onBackPressed() {
        // Because app crashes sometimes without the try->catch
        try {
            // if file exists in memory
            if (imageFileToShare!!.exists()) {
                imageFileToShare!!.delete()
                Log.e(TAG, "image deleted :- $imageFileToShare")
            } else {
//                Toast.makeText(this, "image not deleted", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

        //here we have to call home page api
        if (NetworkHandler(this@ActivityNormalQuizResult).isNetworkAvailable()) {
            val mDialog = AppProgressDialog(this@ActivityNormalQuizResult)
            mDialog.show()
            NormalQuizManagement(this@ActivityNormalQuizResult, this@ActivityNormalQuizResult, mDialog).getAllQuiz()
        } else {
            Toast.makeText(this@ActivityNormalQuizResult, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
        }
    }

    fun isReadStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                true
            } else {

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 3)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    fun isWriteStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            return if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                true
            } else {


                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        2
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation

            return true
        }
    }

    //this method is called to check use login status
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
