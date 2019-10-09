package com.combrainiton.normalQuiz

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.combrainiton.R
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.BataTime
import com.combrainiton.utils.BataTimeCallback
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.acivty_normal_quiz_countdown_layout.*
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.media.MediaPlayer


private val TAG: String = "ActivityNormalQuizInstruction"

class ActivityNormalQuizInstruction : AppCompatActivity() {

    private lateinit var timerSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acivty_normal_quiz_countdown_layout)
        //initMainView() //initialize the main view

        timerSound = MediaPlayer.create(applicationContext, R.raw.first_second)
        timerSound.isLooping = false
        timerSound.start()
        val timeMiliseconds = 3001 // in milliseconds = 2seconds
        val tickTime = 1000 // in milliseconds - 1 second - we trigger onUpdate in intervals of this time
        BataTime(timeMiliseconds, tickTime).start(object : BataTimeCallback {

            @SuppressLint("ResourceAsColor")
            override fun onUpdate(elapsed: Int) {
                Log.e("NormalQuizInstruction", "On update called...time elapsed = $elapsed")

                if (elapsed.equals(1000)) {
                    runOnUiThread {
                        root_layout_countdown.setBackgroundColor(resources.getColor(R.color.design_default_color_primary))
                        text_layout_countdown.setText("2")
                        text_layout_countdown.setTextColor(resources.getColor(R.color.colorTextPrimaryLight))
                        timerSound = MediaPlayer.create(applicationContext, R.raw.first_second)
                        timerSound.isLooping = false
                        timerSound.start()
                    }
                }
                if (elapsed.equals(2000)) {

                    runOnUiThread {
                        root_layout_countdown.setBackgroundColor(resources.getColor(R.color.design_default_color_primary))
                        text_layout_countdown.setText("1")
                        text_layout_countdown.setTextColor(resources.getColor(R.color.colorTextPrimaryLight))
                        timerSound = MediaPlayer.create(applicationContext, R.raw.first_second)
                        timerSound.isLooping = false
                        timerSound.start()

                    }
                }
                if (elapsed.equals(3000)) {

                    runOnUiThread {
                        root_layout_countdown.setBackgroundColor(resources.getColor(R.color.design_default_color_primary))
                        text_layout_countdown.setText("Lets BrainItOn!")
                        text_layout_countdown.textSize = 50F
                        text_layout_countdown.setTextColor(resources.getColor(R.color.colorTextPrimaryLight))
                        timerSound = MediaPlayer.create(applicationContext, R.raw.last_second_sound)
                        timerSound.isLooping = false
                        timerSound.start()
                    }

                }
            }

            override fun onComplete() {
                Log.e("NormalQuizInstruction", "On complete called...")

                val quizId: Int = intent.getIntExtra("quizId", 0)//get quiz id from intent

                Log.i("fromd instruction Id",quizId.toString())

                val quizName: String = intent.getStringExtra("quizName")
                if (NetworkHandler(this@ActivityNormalQuizInstruction).isNetworkAvailable()) { //if network is connected
                    val mDialog = AppProgressDialog(this@ActivityNormalQuizInstruction)
                    mDialog.show() //show progress dialog

                    /** Experiment*/

                    //get question data from normal quiz management and pass the dialog object in the function
                    NormalQuizManagement(this@ActivityNormalQuizInstruction, this@ActivityNormalQuizInstruction, mDialog).getQuestions(quizId, quizName)
                    Log.e("NormalQuizInstruction", "result of id $quizId and name $quizName")
                } else {
                    //display error meessage
                    Toast.makeText(this@ActivityNormalQuizInstruction, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
                }


            }
        })


    }

    //initialize the main view
    @SuppressLint("SetTextI18n")
    private fun initMainView() {
//        quiz_instruction_quit_button.setOnClickListener(this@ActivityNormalQuizInstruction)
//        normal_quiz_instruction_start_button.setOnClickListener(this@ActivityNormalQuizInstruction)
//        normal_quiz_instruction_quiz_image
//        //here we will get data from intent for setting on textview as well as starting web socket
        val quizName: String = intent.getStringExtra("quizName") //get quiz name
        val totalQuestion: Int = intent.getIntExtra("totalQuestion", 0) // get total number of questions

//        Glide.with(this@ActivityNormalQuizInstruction)
//                .load(intent.getStringExtra("quizImage")) //set quiz image
//                .into(normal_quiz_instruction_quiz_image)
//
//        my_quizzes_quiz_name.text = quizName  //set quiz name
//        tvQuizQuestionCount.text = "$totalQuestion Questions" //set number of questions
    }


    //open home activtiy on backpressed
    override fun onBackPressed() {
        finish()
    }

}
