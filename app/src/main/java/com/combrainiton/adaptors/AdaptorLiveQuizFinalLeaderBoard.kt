@file:Suppress("DEPRECATION")

package com.combrainiton.adaptors

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.combrainiton.R
import com.combrainiton.models.SingleQuestionLeaderBoardResponceModel
import kotlinx.android.synthetic.main.card_view_leaderboard_item.view.*

/**
 * Created by Dipendra Sharma  on 17-11-2018.
 */
class AdaptorLiveQuizFinalLeaderBoard(var mContext: Context, var mActivity: Activity, private var userList: ArrayList<SingleQuestionLeaderBoardResponceModel.UserList>, private var userPosition: Int) : RecyclerView.Adapter<AdaptorLiveQuizFinalLeaderBoard.MyViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_view_leaderboard_item, p0, false))
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        val user = userList[p1]
        p0.tvName.text = user.name
        val rankingInt: Int = p1 + 1
        p0.tvRank.text = "" + rankingInt
        p1 + 1
        p0.tvScore.text = "" + user.total_score
        if (p1 == userPosition - 1) {
            p0.rootLayout.background = mContext.resources.getDrawable(R.drawable.shape_rounded_button)
            p0.tvName.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
            p0.tvRank.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
            p0.tvScore.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
        }else {
            p0.tvName.setTextColor(mContext.resources.getColor(R.color.colorTextPrimaryDark))
            p0.tvRank.setTextColor(mContext.resources.getColor(R.color.colorTextPrimaryDark))
            p0.tvScore.setTextColor(mContext.resources.getColor(R.color.colorTextPrimaryDark))
        }

    }


    class MyViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val rootLayout = mView.llleaderboardContainer!!
        val tvRank = mView.tvRank!!
        val tvName = mView.tvName!!
        val tvScore = mView.activity_quiz_question_total_score!!
    }

}