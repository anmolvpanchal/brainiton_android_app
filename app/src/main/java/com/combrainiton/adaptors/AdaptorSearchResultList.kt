package com.combrainiton.adaptors

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import com.bumptech.glide.Glide
import com.combrainiton.normalQuiz.ActivityNormalQuizDescription
import com.combrainiton.R
import com.combrainiton.models.GetAllQuizResponceModel
import kotlinx.android.synthetic.main.card_view_report_item.view.*
import kotlinx.android.synthetic.main.card_view_report_item.view.my_quizzes_list_item_main_container
import kotlinx.android.synthetic.main.card_view_report_item.view.my_quizzes_quiz_image
import kotlinx.android.synthetic.main.card_view_report_item.view.my_quizzes_quiz_name
import kotlinx.android.synthetic.main.card_view_report_item.view.my_quizzes_quiz_sponsor
import kotlinx.android.synthetic.main.course_lessons_card_view_item.view.*

/**
 * Created by Dipendra Sharma  on 18-12-2018.
 */
class AdaptorSearchResultList(var mContext: Context, var mActivity: Activity, var quizList: ArrayList<GetAllQuizResponceModel.Allquizzes>) : androidx.recyclerview.widget.RecyclerView.Adapter<AdaptorSearchResultList.MyViewHolder>(), Filterable {
    var filterList: ArrayList<GetAllQuizResponceModel.Allquizzes>? = ArrayList()

    init {
        filterList = quizList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_view_report_item, parent, false))
    }

    override fun getItemCount(): Int {
        return filterList!!.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val quizData = filterList!![position]
        Glide.with(mContext)
                .load(quizData.image_url)
                .into(holder.imgQuiz)
        holder.tvQuizTitle.text = quizData.quiz_title
        holder.tvQuizHost.text = "By " + quizData.host_name
        holder.cvMain.tag = position

        holder.cvMain.setOnClickListener { p0 ->

            val selectedPosition: Int = p0!!.tag as Int
            if (filterList!![selectedPosition].total_questions != 0) {
                mActivity.startActivity(Intent(mContext, ActivityNormalQuizDescription::class.java)
                        .putExtra("quizId", filterList!![selectedPosition].quiz_id)
                        .putExtra("description", filterList!![selectedPosition].description)
                        .putExtra("totalQuestion", filterList!![selectedPosition].total_questions)
                        .putExtra("quizName", filterList!![selectedPosition].quiz_title)
                        .putExtra("hostName", filterList!![selectedPosition].host_name)
                        .putExtra("image", filterList!![selectedPosition].image_url))
                //mActivity.finish()
            } else {
                Toast.makeText(mContext, "There Is no Questions Added By Host", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val userInput: String = constraint.toString()
                if (quizList.size > 0) {
                    filterList = if (userInput.isEmpty()) {
                        quizList
                    } else {
                        val mFilterList: ArrayList<GetAllQuizResponceModel.Allquizzes>? = ArrayList()
                        for (i in quizList.indices) {
                            if (quizList[i].quiz_title.toLowerCase().contains(userInput.toLowerCase())) {
                                mFilterList!!.add(quizList[i])
                            }
                        }
                        mFilterList
                    }
                }

                val filterResult = FilterResults()
                filterResult.values = filterList
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                filterList = results!!.values as ArrayList<GetAllQuizResponceModel.Allquizzes>
                notifyDataSetChanged()
            }

        }
    }

    class MyViewHolder(mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val imgQuiz = mView.my_quizzes_quiz_image!!
        val tvQuizTitle = mView.my_quizzes_quiz_name!!
        val tvQuizHost = mView.my_quizzes_quiz_sponsor!!
        val cvMain = mView.my_quizzes_list_item_main_container!!
    }

}