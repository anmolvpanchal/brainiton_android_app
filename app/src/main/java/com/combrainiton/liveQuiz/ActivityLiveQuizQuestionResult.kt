@file:Suppress("DEPRECATION")

package com.combrainiton.liveQuiz

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.adaptors.AdaptorLiveQuizLeaderBoard
import com.combrainiton.models.SingleQuestionLeaderBoardResponceModel
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.webSocket.LiveQuizQuestionWebSocketLisner
import kotlinx.android.synthetic.main.activity_live_quiz_question_result.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*

class ActivityLiveQuizQuestionResult : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_quiz_question_result)
        initView()
        startWebSocket()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {

        val isUserAnswerCorrect = AppSharedPreference(this@ActivityLiveQuizQuestionResult).getBoolean("isUserAnswerCorrect")
        val isUserAnswer = AppSharedPreference(this@ActivityLiveQuizQuestionResult).getBoolean("isUserAnswer")

        if (isUserAnswer) { //if user gave any answer

            if (isUserAnswerCorrect) { //if user answer is correct
                //set result screen background to green
                rootLeaderLayout.setBackgroundColor(resources.getColor(R.color.colorCategoryFour))
                //set result status image to check icon
                activity_quiz_question_answer_result_image.setImageResource(R.drawable.ic_check_result)
                //set result status text to correct
                activity_quiz_question_answer_result_text.text = getString(R.string.text_correct)

            } else {
                val mp = MediaPlayer.create(applicationContext, R.raw.wrong)
                mp.start()
                //set result screen background to red
                rootLeaderLayout.setBackgroundColor(resources.getColor(R.color.colorCategoryThree))
                //set result status image to cross icon
                activity_quiz_question_answer_result_image.setImageResource(R.drawable.ic_cross_opaque)
                //set result status text to incorrect
                activity_quiz_question_answer_result_text.text = getString(R.string.text_incorrect)

            }

        } else {
            val mp = MediaPlayer.create(applicationContext, R.raw.wrong)
            mp.start()
            //set result screen background to blue
            rootLeaderLayout.setBackgroundColor(resources.getColor(R.color.colorCategoryTwo))
            //set result status image to time up icon
            activity_quiz_question_answer_result_image.setImageResource(R.drawable.ic_time_up)
            //set result status text to no response given
            activity_quiz_question_answer_result_text.text = getString(R.string.result_screen_question_result_text_1)

        }

        activity_quiz_question_total_score.text = getString(R.string.text_NA) //set total  score to null
        tvPosition.text = getString(R.string.text_NA) //set position to null

        //get leaderboard object from intent
        val dataModel: SingleQuestionLeaderBoardResponceModel = intent.getSerializableExtra("leaderBoard") as SingleQuestionLeaderBoardResponceModel

        //parse user list from leaderboard object
        val userList: ArrayList<SingleQuestionLeaderBoardResponceModel.UserList>
        userList = dataModel.users!!

        //sort user using total score
        userList.sortWith(Comparator { o1, o2 -> o2.total_score.compareTo(o1.total_score) })

        //get logged in user apitoken
        val apiToken: String = AppSharedPreference(this@ActivityLiveQuizQuestionResult).getString("apiToken")

        var userPosition = 0
        for (i in userList.indices) {
            if (userList[i].api_token == apiToken) { //if api token from user list to match logged in user
                activity_quiz_question_total_score.text = """Point gain: ${userList[i].score}""" //set that user's score
                userPosition = i + 1 //set user position to +1
                tvPosition.text = "Position : $userPosition" //set position of user in result view
            }
        }

        //set linear layout manager to leaderboard recylcer
        rvLeaderBoard.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@ActivityLiveQuizQuestionResult)
        //attach adaptor to the leader board recycler view
        rvLeaderBoard.adapter = AdaptorLiveQuizLeaderBoard(this@ActivityLiveQuizQuestionResult, this@ActivityLiveQuizQuestionResult, userList, userPosition)

    }

    //start websocket to receive next question from backend
    private fun startWebSocket() {
        val gamePin: String = AppSharedPreference(this@ActivityLiveQuizQuestionResult).getString("quizPin")
        val wsUrl = "ws://13.67.94.139/pin-$gamePin/"
        val client = OkHttpClient()
        val mRequest = Request.Builder().url(wsUrl).header("Origin", wsUrl).build()
        val webLisner = LiveQuizQuestionWebSocketLisner(this@ActivityLiveQuizQuestionResult, this@ActivityLiveQuizQuestionResult)
        client.newWebSocket(mRequest, webLisner)
        client.dispatcher().executorService().shutdown()
    }

    override fun onBackPressed() {
        Toast.makeText(this@ActivityLiveQuizQuestionResult, "Wait For a Movement Next Question Will appear soon.", Toast.LENGTH_LONG).show()
    }

}
