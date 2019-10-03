package com.combrainiton.adaptors

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import com.bumptech.glide.Glide
import com.combrainiton.R
import com.combrainiton.models.GetAllQuizResponceModel
import com.combrainiton.normalQuiz.ActivityNormalQuizDescription
import com.combrainiton.subscription.LessonsDataList_API
import com.combrainiton.subscription.SubscriptionDataList_API
import kotlinx.android.synthetic.main.card_view_report_item.view.*
import kotlinx.android.synthetic.main.course_lessons_card_view_item.view.*

class AdapterCourseLesson (var mContext: Context, var mActivity: Activity, val lessonsDataList: ArrayList<LessonsDataList_API>, val subscriptionDataList: ArrayList<SubscriptionDataList_API>) : androidx.recyclerview.widget.RecyclerView.Adapter<AdapterCourseLesson.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.course_lessons_card_view_item, parent, false))
    }

    override fun getItemCount(): Int {
        return lessonsDataList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(mContext)
                .load(lessonsDataList[position].quizImage)
                .into(holder.imgQuiz)
        holder.tvQuizTitle.text = lessonsDataList[position].lessonName
        holder.tvQuizHost.text = "By Unknown"
        holder.cvMain.tag = position

        //Setting lesson count
        val no = position + 1
        holder.lessonCount.text = no.toString()

        //Removing UpperLine and LowerLine from first and last card
        /*if(position == 0){ //First card
            holder.upperLine.visibility = View.INVISIBLE
        } else if(position == itemCount - 1){ //Last card
            holder.lowerLine.visibility = View.INVISIBLE
        }*/

        /*holder.cvMain.setOnClickListener { p0 ->

            val selectedPosition: Int = p0!!.tag as Int
            if (filterList!![selectedPosition].total_questions != 0) {
                mActivity.startActivity(Intent(mContext, ActivityNormalQuizDescription::class.java)
                        .putExtra("quizId", filterList!![selectedPosition].quiz_id)
                        .putExtra("description", filterList!![selectedPosition].description)
                        .putExtra("totalQuestion", filterList!![selectedPosition].total_questions)
                        .putExtra("quizName", filterList!![selectedPosition].quiz_title)
                        .putExtra("hostName", filterList!![selectedPosition].host_name)
                        .putExtra("image", filterList!![selectedPosition].image_url))
                mActivity.finish()
            } else {
                Toast.makeText(mContext, "There Is no Questions Added By Host", Toast.LENGTH_LONG).show()
            }
        }*/
    }

    class MyViewHolder(mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val imgQuiz = mView.course_lessons_quiz_image!!
        val tvQuizTitle = mView.course_lessons_quiz_name!!
        val tvQuizHost = mView.course_lessons_quiz_sponsor!!
        val cvMain = mView.course_lessons_Container!!
        val lessonCount = mView.course_lessons_lessons_count
        val upperLine = mView.course_lessons_upper_line
        val lowerLine = mView.course_lessons_lower_line
    }
}