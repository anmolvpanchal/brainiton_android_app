package com.combrainiton.adaptors

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.combrainiton.normalQuiz.ActivityNormalQuizDescription
import com.combrainiton.models.GetAllQuizResponceModel
import kotlinx.android.synthetic.main.card_view_featured_quiz_item.view.*


/**
 * Created by Dipendra Sharma  on 05-11-2018.
 */

class AdaptorFeaturedQuizPrevious(var mContext: Context, var mActivity: Activity, private var featuredQuizzes: ArrayList<GetAllQuizResponceModel.Allquizzes>) : RecyclerView.Adapter<AdaptorFeaturedQuizPrevious.MyViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(com.combrainiton.R.layout.card_view_featured_quiz_item, p0, false))
    }

    override fun getItemCount(): Int {
        return featuredQuizzes.size
    }

    //this method will be called when new instance of data will be added to view holder
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {

        if (p1 == 0) {
            p0.cvMainContainer.layoutParams = RelativeLayout.LayoutParams(0, 0)
        }

        if(p1 == itemCount-1) {
            val params = LinearLayoutCompat.LayoutParams(convertToDp(154),convertToDp(180))
            params.setMargins(convertToDp(16), convertToDp(16), convertToDp(16), convertToDp(16))
            p0.cvMainContainer.layoutParams =  params
        }

        val quizItem = featuredQuizzes[p1]
        p0.tvQuizTitle.text = quizItem.quiz_title
        p0.tvQuizBy.text = "By " + quizItem.host_name
        p0.cvMainContainer.tag = p1

        Glide.with(mContext)
                .load(quizItem.image_url)
                .into(p0.imgQuizCover)

        p0.cvMainContainer.setOnClickListener {
            val selectedPosition: Int = p0.cvMainContainer.tag as Int
            if (featuredQuizzes[selectedPosition].total_questions != 0) {
                mActivity.startActivity(Intent(mContext, ActivityNormalQuizDescription::class.java)
                        .putExtra("quizId", featuredQuizzes[selectedPosition].quiz_id)
                        .putExtra("description", featuredQuizzes[selectedPosition].description)
                        .putExtra("totalQuestion", featuredQuizzes[selectedPosition].total_questions)
                        .putExtra("quizName", featuredQuizzes[selectedPosition].quiz_title)
                        .putExtra("hostName", featuredQuizzes[selectedPosition].host_name)
                        .putExtra("image", quizItem.image_url))
                mActivity.finish()
            } else {
                Toast.makeText(mContext, "There Is no Questions Added By Host", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun convertToDp(a: Int) : Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a.toFloat(), mActivity.resources.displayMetrics).toInt()
    }

    //template class for view holder
    class MyViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {

        val imgQuizCover = mView.imgQuizCover!!
        val tvQuizTitle = mView.my_quizzes_quiz_name!!
        val tvQuizBy = mView.tvQuizBy!!
        val cvMainContainer = mView.cvMainContainer!!

    }

}