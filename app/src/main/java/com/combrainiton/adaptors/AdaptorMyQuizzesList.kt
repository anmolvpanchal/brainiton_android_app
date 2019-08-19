package com.combrainiton.adaptors

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.combrainiton.R
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.models.GetUserRecentAllQuizResponceModel
import com.combrainiton.utils.AppProgressDialog
import kotlinx.android.synthetic.main.card_view_report_item.view.*

/**
 * Created by Dipendra Sharma  on 06-12-2018.
 */
class AdaptorMyQuizzesList(var mContext: Context, var mActivity: Activity, var recentList: ArrayList<GetUserRecentAllQuizResponceModel.RecentQuizList>) : RecyclerView.Adapter<AdaptorMyQuizzesList.MyViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_view_report_item, p0, false))
    }

    override fun getItemCount(): Int {
        return recentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        val recentList = recentList[p1]
        Glide.with(mContext)
                .load(recentList.image)
                .into(p0.imgQuiz)
        p0.tvQuizTitle.text = recentList.name
        p0.tvQuizHost.text = "By " + recentList.host_name
        p0.cvMainContainer.setOnClickListener{
            val mDialog = AppProgressDialog(mContext)
            mDialog.show()
            NormalQuizManagement(mContext, mActivity,mDialog).getQuizDetail(recentList.id)
        }
    }


    class MyViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val cvMainContainer = mView.cvMainContainer!!
        val imgQuiz = mView.my_quizzes_quiz_image!!
        val tvQuizTitle = mView.my_quizzes_quiz_name!!
        val tvQuizHost = mView.my_quizzes_quiz_sponsor!!
    }

}