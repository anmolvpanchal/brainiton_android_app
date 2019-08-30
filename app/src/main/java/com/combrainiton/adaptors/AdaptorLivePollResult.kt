package com.combrainiton.adaptors

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.combrainiton.R
import com.combrainiton.models.PollAllQuestionListResponceModel
import kotlinx.android.synthetic.main.card_view_live_poll_result.view.*
import java.text.DecimalFormat

/**
 * Created by Dipendra Sharma  on 27-11-2018.
 */
class AdaptorLivePollResult(var mContext: Context, var mActivity: Activity, private var resultDataList: ArrayList<PollAllQuestionListResponceModel.QuestionsList>) : androidx.recyclerview.widget.RecyclerView.Adapter<AdaptorLivePollResult.MyViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_view_live_poll_result, p0, false))
    }

    override fun getItemCount(): Int {
        return resultDataList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        val resultDataItem = resultDataList[p1]
        p0.tvPollQuiz.text = resultDataItem.question_text
        p0.tvOption1.text = resultDataItem.options!![0].option
        val option1Percent = DecimalFormat("##").format(resultDataItem.options!![0].option_percent * 100)
        p0.tvOption1Percent.text = "$option1Percent%"
        val option2Percent = DecimalFormat("##").format(resultDataItem.options!![1].option_percent * 100)
        p0.tvOption2.text = resultDataItem.options!![1].option
        p0.tvOption2Percent.text = "$option2Percent%"
        val option3Percent = DecimalFormat("##").format(resultDataItem.options!![2].option_percent * 100)
        p0.tvOption3.text = resultDataItem.options!![2].option
        p0.tvOption3Percent.text = "$option3Percent%"
        val option4Percent = DecimalFormat("##").format(resultDataItem.options!![3].option_percent * 100)
        p0.tvOption4.text = resultDataItem.options!![3].option
        p0.tvOption4Percent.text = "$option4Percent%"
    }

    class MyViewHolder(mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val tvPollQuiz = mView.tvPollQue!!
        val tvOption1 = mView.optionOne!!
        val tvOption1Percent = mView.optionOnePercent!!
        val tvOption2 = mView.optionTwo!!
        val tvOption2Percent = mView.optionTwoPercent!!
        val tvOption3 = mView.optionThree!!
        val tvOption3Percent = mView.optionThreePercent!!
        val tvOption4 = mView.optionFour!!
        val tvOption4Percent = mView.optionFourPercent!!

    }

}