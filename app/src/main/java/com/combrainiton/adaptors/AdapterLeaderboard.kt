package com.combrainiton.adaptors

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.combrainiton.R
import com.combrainiton.subscription.LeaderboardModel

class AdapterLeaderboard(var mContext: Context, var mActivity: Activity,val leaderboardModel: ArrayList<LeaderboardModel>) : androidx.recyclerview.widget.RecyclerView.Adapter<AdapterLeaderboard.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.leaderboard_item, parent, false))
    }

    override fun getItemCount(): Int {
        return leaderboardModel.size
    }

    //Always use this two following methods in recyclerView adapter
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.rank_number.text = leaderboardModel[position].rank
        holder.player_name.text = leaderboardModel[position].name
        holder.player_score.text = leaderboardModel[position].score

        if(position == 0){
            holder.background.setBackgroundDrawable(mActivity.resources.getDrawable(R.drawable.circle_shape_for_rank1))
        } else if(position == 1){
            holder.background.setBackgroundDrawable(mActivity.resources.getDrawable(R.drawable.circle_shape_for_rank2))
        }else if(position == 2){
            holder.background.setBackgroundDrawable(mActivity.resources.getDrawable(R.drawable.circle_shape_for_rank3))
        }

    }

    class MyViewHolder(mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {

        val rank_number: TextView
        val player_name: TextView
        val player_score: TextView
        val background: TextView

        init {
            rank_number = mView.findViewById(R.id.leaderboard_rank)
            player_name = mView.findViewById(R.id.leaderboard_name)
            player_score = mView.findViewById(R.id.leaderboard_score)
            background = mView.findViewById(R.id.leaderboard_backgroundCircle)
        }
    }

}