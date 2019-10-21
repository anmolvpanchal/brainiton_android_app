package com.combrainiton.liveQuiz

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.utils.NetworkHandler
import com.combrainiton.webSocket.LiveQuizQuestionWebSocketLisner
import com.combrainiton.webSocket.PollQuestionWebSocketLisner
import kotlinx.android.synthetic.main.activity_live_quiz_instruction.*
import kotlinx.android.synthetic.main.activity_noraml_quiz_instruction.*
import kotlinx.android.synthetic.main.activity_noraml_quiz_instruction.my_quizzes_quiz_name
import kotlinx.android.synthetic.main.activity_noraml_quiz_instruction.tvQuizQuestionCount
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener

class ActivityLiveQuizInstruction : AppCompatActivity(), View.OnClickListener {

    private var isPoll: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_quiz_instruction)
        //intialize the main view
        initViews()
    }

    private fun initViews() {



        //set on click listener to quit button
        livequiz_instruction_quit_button.setOnClickListener(this@ActivityLiveQuizInstruction)

        //firstly we have to check it is poll or quiz
        isPoll = AppSharedPreference(this@ActivityLiveQuizInstruction).getBoolean("isPoll")

        //here we will get data from intent for setting on textview as well as starting web socket
        val quizName: String = intent.getStringExtra("quizName")
        val totalQuestion: String = "" + AppSharedPreference(this@ActivityLiveQuizInstruction).getInt("totalQuestion") + " Questions"

        my_quizzes_quiz_name.text = quizName //display the name of the quiz
        tvQuizQuestionCount.text = totalQuestion //display the total no of question

        //here we will start web socket when host will start quiz then through this web socket our question page will apear
        startWebSocket()
    }

    //start web socket for live quiz management
    private fun startWebSocket() {
        // save the question counter to 0 in intent data
        AppSharedPreference(this@ActivityLiveQuizInstruction).saveInt("currentQuestion", 0)
        val gamePin: String = intent.getStringExtra("gamePin") //get the game pin from intent
        val wsUrl = "ws://13.235.33.12:8000/pin-$gamePin/" //set the base url
        val client = OkHttpClient() //get http client
        val mRequest = Request.Builder().url(wsUrl).header("Origin", wsUrl).build()
        val webLisner: WebSocketListener
        if (isPoll) { //if it is a poll start poll question web socket
            webLisner = PollQuestionWebSocketLisner(this@ActivityLiveQuizInstruction, this@ActivityLiveQuizInstruction)

        } else { //if it's a quiz start quiz question web socket
            webLisner = LiveQuizQuestionWebSocketLisner(this@ActivityLiveQuizInstruction, this@ActivityLiveQuizInstruction)
        }
        client.newWebSocket(mRequest, webLisner)
        client.dispatcher().executorService().shutdown()
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.quiz_instruction_quit_button) {
            explore() //open explore activity
        }
    }

    // this will open the home activtiy
    private fun explore() {
        if (NetworkHandler(this@ActivityLiveQuizInstruction).isNetworkAvailable()) {
            //if internet is connected
            val mDialog = AppProgressDialog(this@ActivityLiveQuizInstruction)
            mDialog.show() //show progress dialog
            //this will open the home activity after retriving the data
            NormalQuizManagement(this@ActivityLiveQuizInstruction, this@ActivityLiveQuizInstruction, mDialog).getAllQuiz()
        } else {
            //display error message
            Toast.makeText(this@ActivityLiveQuizInstruction, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
        }
    }

    //open home activtiy on backpressed
    override fun onBackPressed() {
        explore()
    }

}
