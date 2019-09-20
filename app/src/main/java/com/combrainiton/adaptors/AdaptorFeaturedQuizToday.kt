package com.combrainiton.adaptors

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.combrainiton.models.GetAllQuizResponceModel
import com.combrainiton.R
import com.combrainiton.normalQuiz.ActivityNormalQuizDescription
import kotlinx.android.synthetic.main.card_view_featured_quiz_main_item.view.*

class AdaptorFeaturedQuizToday(var mContext: Context, var mActivity: Activity, private var featuredQuizzes:ArrayList<GetAllQuizResponceModel.Allquizzes>): androidx.recyclerview.widget.RecyclerView.Adapter<AdaptorFeaturedQuizToday.MyViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_view_featured_quiz_main_item, p0, false))
    }

    override fun getItemCount(): Int {
        return  1
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {

            val quizItem = featuredQuizzes[p1]
            p0.tvQuizTitle.text = quizItem.quiz_title
            p0.tvQuizBy.text = "By " + quizItem.host_name

            //this will load the image into the view holder
            Glide.with(mContext)
                    .load(quizItem.image_url)
                    .into(p0.imgQuizCover)

            p0.cvMainContainer.tag=p1

            p0.cvMainContainer.setOnClickListener {
                val selectedPosition:Int= p0.cvMainContainer.tag as Int
                if (featuredQuizzes[selectedPosition].total_questions!=0) {
                    mActivity.startActivity(Intent(mContext, ActivityNormalQuizDescription::class.java)
                            .putExtra("quizId", featuredQuizzes[selectedPosition].quiz_id)
                            .putExtra("description", featuredQuizzes[selectedPosition].description)
                            .putExtra("totalQuestion", featuredQuizzes[selectedPosition].total_questions)
                            .putExtra("quizName", featuredQuizzes[selectedPosition].quiz_title)
                            .putExtra("hostName", featuredQuizzes[selectedPosition].host_name)
                            .putExtra("image",quizItem.image_url))
                    //mActivity.finish()
                }else{
                    Toast.makeText(mContext,"There Is no Questions Added By Host",Toast.LENGTH_LONG).show()
                }
            }

    }

    class MyViewHolder(mView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(mView){
        val imgQuizCover= mView.main_featured_quiz_image!!
        val tvQuizTitle= mView.main_featured_quiz_name!!
        val tvQuizBy= mView.main_featured_quiz_sponsor_name!!
        val cvMainContainer = mView.main_featured!!
    }

}
