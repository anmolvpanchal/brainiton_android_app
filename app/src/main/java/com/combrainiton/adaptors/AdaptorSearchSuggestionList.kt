package com.combrainiton.adaptors

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.combrainiton.normalQuiz.ActivityNormalQuizDescription
import com.combrainiton.R
import com.combrainiton.models.GetAllQuizResponceModel
import kotlinx.android.synthetic.main.card_view_search_suggestion_item.view.*

/**
 * Created by Dipendra Sharma  on 18-12-2018.
 */
class AdaptorSearchSuggestionList(var mContext: Context, var mActivity: Activity, private var quizList: ArrayList<GetAllQuizResponceModel.Allquizzes>) : RecyclerView.Adapter<AdaptorSearchSuggestionList.MyViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_view_search_suggestion_item, p0, false))
    }

    override fun getItemCount(): Int {
        return quizList.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        val quizItem = quizList[p1]
        val quizTitle: String = quizItem.quiz_title
        p0.tvQuizTitle.text = quizTitle
        p0.cvMainContainer.tag = p1
        p0.cvMainContainer.setOnClickListener {
            val selectedPosition: Int = p0.cvMainContainer.tag as Int
            if (quizList[selectedPosition].total_questions != 0) {
                mActivity.startActivity(Intent(mContext, ActivityNormalQuizDescription::class.java)
                        .putExtra("quizId", quizList[selectedPosition].quiz_id)
                        .putExtra("description", quizList[selectedPosition].description)
                        .putExtra("totalQuestion", quizList[selectedPosition].total_questions)
                        .putExtra("quizName", quizList[selectedPosition].quiz_title)
                        .putExtra("hostName", quizList[selectedPosition].host_name)
                        .putExtra("image", quizItem.image_url))
                mActivity.finish()
            } else {
                Toast.makeText(mContext, "There Is no Questions Added By Host", Toast.LENGTH_LONG).show()
            }
        }
    }


    class MyViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {

        val tvQuizTitle = mView.card_view_search_suggestion_text_view!!
        val cvMainContainer = mView.card_view_search_suggestion_main_container!!

    }
}