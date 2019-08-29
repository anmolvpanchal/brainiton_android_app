package com.combrainiton.livePoll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.adaptors.AdaptorLivePollResult
import com.combrainiton.models.PollAllQuestionListResponceModel
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_live_poll_result.*

class ActivityLivePollResult : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_poll_result)
        initViews()
    }

    private fun initViews() {
        //here we will get result data from intent
        val resultData: PollAllQuestionListResponceModel? = intent.getSerializableExtra("result") as PollAllQuestionListResponceModel?
        tvPollName.text = resultData!!.poll_name
        val resultDataList: ArrayList<PollAllQuestionListResponceModel.QuestionsList>? = resultData.questions
        rvPollResult.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@ActivityLivePollResult)
        rvPollResult.adapter = AdaptorLivePollResult(this@ActivityLivePollResult, this@ActivityLivePollResult, resultDataList!!)

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        if (NetworkHandler(this@ActivityLivePollResult).isNetworkAvailable()) {
            val mDialog = AppProgressDialog(this@ActivityLivePollResult)
            mDialog.show()
            NormalQuizManagement(this@ActivityLivePollResult, this@ActivityLivePollResult, mDialog).getAllQuiz()
        } else {
            Toast.makeText(this@ActivityLivePollResult, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
        }
    }

}
