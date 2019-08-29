@file:Suppress("DEPRECATION")

package com.combrainiton.adaptors

import android.app.Activity
import android.content.Context
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.combrainiton.R
import com.combrainiton.models.GetAllQuizResponceModel
import kotlinx.android.synthetic.main.card_view_category_item.view.*

/**
 * Created by Dipendra Sharma  on 06-11-2018.
 */

class AdaptorCategoryList(var mContext: Context, var mActivity: Activity, private var categoryList: ArrayList<GetAllQuizResponceModel.CategoryList>, var quizList: ArrayList<GetAllQuizResponceModel.Allquizzes>, var clickLisner: View.OnClickListener, var featured_quizzesList: ArrayList<GetAllQuizResponceModel.Allquizzes>) : androidx.recyclerview.widget.RecyclerView.Adapter<AdaptorCategoryList.MyViewHolder>() {

    var count = 0
    private var backGroundColor = arrayOf(R.color.colorCategoryOne, R.color.colorCategoryTwo, R.color.colorCategoryThree, R.color.colorCategoryFour)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_view_category_item, p0, false))
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {

        if (p1 == 16) {
            val params = LinearLayoutCompat.LayoutParams(convertToDp(154), convertToDp(60))
            params.setMargins(convertToDp(16), convertToDp(0), convertToDp(16), convertToDp(16))
            p0.cvMainContainer.layoutParams = params
        }

        if (p1 == 17) {
            val params = LinearLayoutCompat.LayoutParams(convertToDp(154), convertToDp(60))
            params.setMargins(convertToDp(16), convertToDp(0), convertToDp(16), convertToDp(16))
            p0.cvMainContainer.layoutParams = params
        }

        val categoryItem = categoryList[p1]
        p0.tvQuizCatName.text = categoryItem.category_name
        p0.tvQuizCatName.tag = p1
        p0.tvQuizCatName.setOnClickListener(clickLisner)
        p0.cardView.setCardBackgroundColor(mContext.resources.getColor(backGroundColor[count]))
        count++
        if (count > 3) {
            count = 0
            count = 0
        } // this will iterate through the counter to change the color of the category view

    }

    class MyViewHolder(mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {

        val cvMainContainer = mView.rlCategory!!
        val tvQuizCatName = mView.tvQuizCatName!!
        val cardView = mView.rlCategory!!

    }


    private fun convertToDp(a: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a.toFloat(), mActivity.resources.displayMetrics).toInt()
    }

    fun getCategoryData(id: Int): ArrayList<GetAllQuizResponceModel.Allquizzes> {

        val categoryQuizData = ArrayList<GetAllQuizResponceModel.Allquizzes>()
        for (i in quizList.indices) {
            val categoryId = quizList[i].category_id
            if (categoryId == id) {
                categoryQuizData.add(quizList[i])
            }

        }

        return categoryQuizData

    }
}