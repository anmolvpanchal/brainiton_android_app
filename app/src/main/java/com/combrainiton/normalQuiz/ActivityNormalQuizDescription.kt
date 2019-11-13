package com.combrainiton.normalQuiz

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.combrainiton.BuildConfig
import com.combrainiton.R
import com.combrainiton.adaptors.AdapterResultDemo
import com.combrainiton.main.Activity_leaderboard
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.subscription.ScoreDataList_API
import com.combrainiton.subscription.ServiceGenerator
import com.combrainiton.subscription.SubscriptionInterface
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_normal_quiz_description.*
import kotlinx.android.synthetic.main.demo_result.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri


class ActivityNormalQuizDescription : AppCompatActivity() {

    private var quizId: Int = 0  //for quiz id
    private var totalQuestiontotalQuestion: String = ""//for total quiz question
    private var quizName: String = "" //for quiz name
    private var hostName: String = ""
    private var quizDescriptionStr: String = ""
    private var quiz_video :String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_quiz_description)
        result_view.visibility = View.GONE

        quizId = intent.getIntExtra("quizId", 0) //get quiz id

        LernOrNot(quizId)

        initMainView() //initialize the main view

    }

    fun shareQuiz(view: View) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Brain It On !")
        var shareMessage = "\nHey, try $quizName quiz on this amazing Quizzing App 'BrainItOn'.\nEnter PIN N$quizId to Play Now.\n\n"
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "choose one"))
    }

    //initialize the main view
    private fun initMainView() {


        result_text.setOnClickListener {
            rl_description.visibility = View.GONE
            normal_quiz_description_play_video_button.visibility = View.GONE
            result_view.visibility = View.VISIBLE
            gettingDetailsForPlayers(quizId)

            details_text.setTextColor(resources.getColor(R.color.black))
            details_text.setBackgroundResource(R.drawable.white_for_unselected)
            result_text.setTextColor(resources.getColor(R.color.colorPrimary))
            result_text.setBackgroundResource(R.drawable.shape_tab_bottom_selected)

        }

        details_text.setOnClickListener {
            rl_description.visibility = View.VISIBLE
            normal_quiz_description_play_video_button.visibility = View.VISIBLE
            result_view.visibility = View.GONE


            details_text.setTextColor(resources.getColor(R.color.colorPrimary))
            details_text.setBackgroundResource(R.drawable.shape_tab_bottom_selected)
            result_text.setTextColor(resources.getColor(R.color.black))
            result_text.setBackgroundResource(R.drawable.white_for_unselected)
        }



        leaderboard_cardView.setOnClickListener {
            startActivity(Intent(this@ActivityNormalQuizDescription,Activity_leaderboard::class.java)
                    .putExtra("quizId",quizId)
                    .putExtra("from","description"))
        }

        Log.i("fromdescription Id",quizId.toString())


        quizName = intent.getStringExtra("quizName") //get quiz name
        totalQuestiontotalQuestion = intent.getStringExtra("totalQuestion")//get total number of question
        hostName = "By " + intent.getStringExtra("hostName") //get hostname
        quizDescriptionStr = intent.getStringExtra("description") //get quiz description


        activity_quiz_question_text.text = quizName //set quiz name
        normal_quiz_description_description_text.text = quizDescriptionStr //set quiz description
        normal_quiz_description_host_name.text = hostName //set host name

        Glide.with(this@ActivityNormalQuizDescription) //set quiz image
                .load(intent.getStringExtra("image"))
                .into(normal_quiz_description_image)

        top_bar_cancle_button.setOnClickListener {
            //perform on backpress on click of close button
            onBackPressed()
        }

        normal_quiz_description_play_video_button.setOnClickListener {

            if (quiz_video != null && !quiz_video.isEmpty() && !quiz_video.equals("null")) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(quiz_video)))

            }else{
                Toast.makeText(this,"No video available",Toast.LENGTH_LONG).show()
            }
        }


        normal_quiz_description_learn_button.setOnClickListener {
            startActivity(Intent(this@ActivityNormalQuizDescription, Learn_quiz::class.java)
                    .putExtra("quizId", quizId) //pass quiz id
                    .putExtra("quizName", quizName) //pass quiz name
            )
        }

        //set on click listener for play button
        normal_quiz_description_play_button.setOnClickListener {
            startActivity(Intent(this@ActivityNormalQuizDescription, ActivityNormalQuizInstruction::class.java)
                    .putExtra("quizId", quizId) //pass quiz id
                    .putExtra("totalQuestion", totalQuestiontotalQuestion) //pass total question
                    .putExtra("quizName", quizName) //pass quiz name
                    .putExtra("quizImage", intent.getStringExtra("image"))) //pass quiz image
            //close activity
        }

        normal_quiz_description_play_button_learn.setOnClickListener {
            startActivity(Intent(this@ActivityNormalQuizDescription, ActivityNormalQuizInstruction::class.java)
                    .putExtra("quizId", quizId) //pass quiz id
                    .putExtra("totalQuestion", totalQuestiontotalQuestion) //pass total question
                    .putExtra("quizName", quizName) //pass quiz name
                    .putExtra("quizImage", intent.getStringExtra("image"))) //pass quiz image
            //close activity
        }

    }

    // this will open the home activity
    private fun explore() {
        if (NetworkHandler(this@ActivityNormalQuizDescription).isNetworkAvailable()) { //if internet is connected
            val mDialog = AppProgressDialog(this@ActivityNormalQuizDescription)
            mDialog.show() //show progress dialog
            //this will open the home activity after retriving the data
            NormalQuizManagement(this@ActivityNormalQuizDescription, this@ActivityNormalQuizDescription, mDialog).getAllQuiz()
        } else {
            //display error message
            Toast.makeText(this@ActivityNormalQuizDescription, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
        }
    }

    //open home activtiy on backpressed
    override fun onBackPressed() {
        finish()
    }

    fun gettingDetailsForPlayers(QuizID: Int) {

        //create api client first
        val apiToken: String = AppSharedPreference(this).getString("apiToken")

        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)
        val call = apiClient.getDataForResult(QuizID)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(this@ActivityNormalQuizDescription, "Error", this@ActivityNormalQuizDescription.resources.getString(R.string.error_server_problem))
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(this@ActivityNormalQuizDescription, "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return;
                }

                try {

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)

                    val max_score = rootObj.getString("max_score")
                    val latest_score = rootObj.getString("latest_score")
                    val accuracy = rootObj.getString("accuracy")
                    val average = rootObj.getString("average")
                    val rank = rootObj.getString("rank")


                    topscore_text.text = max_score
                    myscore_text.text = latest_score
                    accuracy_text.text = accuracy + "%"
                    avgscore_text.text = average
                    yourrank_text.text = rank


                } catch (ex: Exception) {
                    when (ex) {
                        is IllegalAccessException, is IndexOutOfBoundsException -> {
                            Log.e("catch block", "some known exception" + ex)
                        }
                        else -> Log.e("catch block", "other type of exception" + ex)

                    }

                }
            }

        })


    }

    fun LernOrNot(QuizID: Int) {

        //create api client first
        val apiToken: String = AppSharedPreference(this).getString("apiToken")


        val subscriptionInterface = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)

        val call = subscriptionInterface.LearnOption(QuizID)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    Toast.makeText(applicationContext, "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show()
                    return
                }

                try {
                    val resp = response.body()!!.string()
                    val rootObj = JSONObject(resp)

                    val practice = rootObj.getString("practice")
                    quiz_video = rootObj.getString("quiz_video")
                    Log.i("description", " prectice " + practice)

                    if (practice.equals("false")) {
                        normal_quiz_description_play_button_learn.visibility = View.GONE
                        normal_quiz_description_learn_button.visibility = View.GONE

                    } else {
                        normal_quiz_description_play_button.visibility = View.GONE
                    }

                    if (quiz_video != null && !quiz_video.isEmpty() && !quiz_video.equals("null")){

                    }else{
                        normal_quiz_description_play_video_button.visibility =View.GONE
                    }

                } catch (e: Exception) {
                    Log.e("quiz escription", "error$e")
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, "Something went Wrong !!", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
