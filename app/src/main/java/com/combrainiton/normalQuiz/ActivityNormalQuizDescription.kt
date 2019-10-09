package com.combrainiton.normalQuiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.combrainiton.BuildConfig
import com.combrainiton.R
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_normal_quiz_description.*


class ActivityNormalQuizDescription : AppCompatActivity() {

    private var quizId: Int = 0  //for quiz id
    private var totalQuestiontotalQuestion: Int = 0 //for total quiz question
    private var quizName: String = "" //for quiz name
    private var hostName: String = ""
    private var quizDescriptionStr: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_quiz_description)
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
        quizId = intent.getIntExtra("quizId", 0) //get quiz id

        Log.i("fromdescription Id",quizId.toString())

        quizName = intent.getStringExtra("quizName") //get quiz name
        totalQuestiontotalQuestion = intent.getIntExtra("totalQuestion", 0)//get total number of question
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

        //set on click listener for play button
        normal_quiz_description_play_button.setOnClickListener {
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

}
