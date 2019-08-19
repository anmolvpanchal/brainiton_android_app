package com.combrainiton.normalQuiz

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.combrainiton.R
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_noraml_quiz_instruction.*


class ActivityNormalQuizInstruction : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noraml_quiz_instruction)
        initMainView() //initialize the main view
    }

    //initialize the main view
    @SuppressLint("SetTextI18n")
    private fun initMainView() {
        quiz_instruction_quit_button.setOnClickListener(this@ActivityNormalQuizInstruction)
        normal_quiz_instruction_start_button.setOnClickListener(this@ActivityNormalQuizInstruction)
        normal_quiz_instruction_quiz_image
        //here we will get data from intent for setting on textview as well as starting web socket
        val quizName: String = intent.getStringExtra("quizName") //get quiz name
        val totalQuestion: Int = intent.getIntExtra("totalQuestion", 0) // get total number of questions

        Glide.with(this@ActivityNormalQuizInstruction)
                .load(intent.getStringExtra("quizImage")) //set quiz image
                .into(normal_quiz_instruction_quiz_image)

        my_quizzes_quiz_name.text = quizName  //set quiz name
        tvQuizQuestionCount.text = "$totalQuestion Questions" //set number of questions
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.normal_quiz_instruction_start_button -> { //on click of start button
                val quizId: Int = intent.getIntExtra("quizId", 0)//get quiz id from intent
                val quizName: String = intent.getStringExtra("quizName")
                if (NetworkHandler(this@ActivityNormalQuizInstruction).isNetworkAvailable()) { //if network is connected
                    val mDialog = AppProgressDialog(this@ActivityNormalQuizInstruction)
                    mDialog.show() //show progress dialog
                    //get question data from normal quiz management and pass the dialog object in the function
                    NormalQuizManagement(this@ActivityNormalQuizInstruction, this@ActivityNormalQuizInstruction, mDialog).getQuestions(quizId,quizName)
                } else {
                    //display error meessage
                    Toast.makeText(this@ActivityNormalQuizInstruction, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
                }
            }
            R.id.quiz_instruction_quit_button -> { //on click of quit button
                onBackPressed() //open main acitvity
            }
        }
    }

    // this will open the home activtiy
    private fun explore() {
        if (NetworkHandler(this@ActivityNormalQuizInstruction).isNetworkAvailable()) { //if internet is connected
            val mDialog = AppProgressDialog(this@ActivityNormalQuizInstruction)
            mDialog.show() //show progress dialog
            //this will open the home activity after retriving the data
            NormalQuizManagement(this@ActivityNormalQuizInstruction, this@ActivityNormalQuizInstruction, mDialog).getAllQuiz()
        } else {
            //display error message
            Toast.makeText(this@ActivityNormalQuizInstruction, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
        }
    }

    //open home activtiy on backpressed
    override fun onBackPressed() {
        finish()
    }

}
