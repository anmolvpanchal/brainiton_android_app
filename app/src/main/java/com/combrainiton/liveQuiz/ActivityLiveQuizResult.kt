package com.combrainiton.liveQuiz

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.adaptors.AdaptorLiveQuizFinalLeaderBoard
import com.combrainiton.adaptors.AdaptorLiveQuizLeaderBoard
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.models.SingleQuestionLeaderBoardResponceModel
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_live_quiz_result.*
import java.util.*

class ActivityLiveQuizResult : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_quiz_result)
        initViews()//initialize the main view
    }


    @SuppressLint("SetTextI18n")
    private fun initViews() {

        //set onclick listener to quit button
        live_quize_result_quit_button.setOnClickListener{ onBackPressed() }

        //get leaderboard data from intent
        val dataModel: SingleQuestionLeaderBoardResponceModel = intent.getSerializableExtra("leaderBoard") as SingleQuestionLeaderBoardResponceModel

        //parse user list object from leaderboard list
        val userList: ArrayList<SingleQuestionLeaderBoardResponceModel.UserList>
        userList = dataModel.users!!
        if(AppSharedPreference(this@ActivityLiveQuizResult).getBoolean("sound")){
            val mp = MediaPlayer.create(applicationContext, R.raw.final_leaderboard)
            mp.start()
        }

        //sort user list with score
        userList.sortWith(Comparator { o1, o2 -> o2.total_score.compareTo(o1.total_score) })

        //here we will check the leaderboard list have data or not for every three cases
        //set the first player score
        if (userList.size >= 1) { //if player list contains 1 or more than three players
            tvPlayerFirstName.text = userList[0].name
            val scoreStr: String = "" + userList[0].total_score
            tvPlayerFirstScore.text = scoreStr
        }
        //set the second player score
        if (userList.size >= 2) { //if player list contains 2 or more than three players
            tvPlayerSecondName.text = userList[1].name
            val scoreStr: String = "" + userList[1].total_score
            tvPlayerSecondScore.text = scoreStr
        }
        //set the third player score
        if (userList.size >= 3) { //if player list contains 3 or more than three players
            tvPlayerThirdName.text = userList[2].name
            val scoreStr: String = "" + userList[2].total_score
            tvPlayerThirdScore.text = scoreStr
        }

        //get logged in user apitoken
        val apiToken: String = AppSharedPreference(this@ActivityLiveQuizResult).getString("apiToken")

        var userPosition = 0
        for (i in userList.indices) {
            if (userList[i].api_token == apiToken) { //if api token from user list to match logged in user
                userPosition = i + 1 //set user position to +1
            }
        }

        //set recycler view layout manager to linear layout manager
        rvLeaderBoard.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@ActivityLiveQuizResult)
        //set adaptor to recycler view
        rvLeaderBoard.adapter = AdaptorLiveQuizFinalLeaderBoard(this@ActivityLiveQuizResult, this@ActivityLiveQuizResult, userList, userPosition)

    }

    override fun onBackPressed() {
        //here we have to call home page api
        if (NetworkHandler(this@ActivityLiveQuizResult).isNetworkAvailable()) {
            val mDialog = AppProgressDialog(this@ActivityLiveQuizResult)
            mDialog.show()
            NormalQuizManagement(this@ActivityLiveQuizResult, this@ActivityLiveQuizResult, mDialog).getAllQuiz()
        } else {
            Toast.makeText(this@ActivityLiveQuizResult, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
        }
    }
}
