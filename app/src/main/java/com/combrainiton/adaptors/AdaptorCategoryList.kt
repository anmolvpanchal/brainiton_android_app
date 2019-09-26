@file:Suppress("DEPRECATION")

package com.combrainiton.adaptors

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import cn.nekocode.badge.BadgeDrawable
import com.combrainiton.R
import com.combrainiton.models.GetAllQuizResponceModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.card_view_category_item.view.*
import java.lang.Exception

/**
 * Created by Dipendra Sharma  on 06-11-2018.
 */

class AdaptorCategoryList(var mContext: Context, var mActivity: Activity, private var categoryList: ArrayList<GetAllQuizResponceModel.CategoryList>, var quizList: ArrayList<GetAllQuizResponceModel.Allquizzes>, var clickLisner: View.OnClickListener, var featured_quizzesList: ArrayList<GetAllQuizResponceModel.Allquizzes>, var flag: Boolean) : androidx.recyclerview.widget.RecyclerView.Adapter<AdaptorCategoryList.MyViewHolder>() {

    var category: HashMap<String, Int> = HashMap<String, Int>()
    val sharedPreferences = mActivity.getSharedPreferences("CategoryCount", Context.MODE_PRIVATE)
    var count = 0
    private var backGroundColor = arrayOf(R.color.colorCategoryOne, R.color.colorCategoryTwo, R.color.colorCategoryThree, R.color.colorCategoryFour)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_view_category_item, p0, false))
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {

        //Calling method to display new quizzes count on category cardView.
        //It will work only above Nougat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            categoryBadge(p1,p0.badge)
        }

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

    @RequiresApi(Build.VERSION_CODES.N)
    fun categoryBadge(p1: Int,badge: TextView) {
        val categoryString = sharedPreferences.getString("categorycount", null)

        if (flag) {
            Log.i("adapter", "inside")
            if (categoryString != null) {
                val type: java.lang.reflect.Type = object : TypeToken<HashMap<String, Int>>() {}.type
                category = Gson().fromJson(categoryString, type)
                val oldValue: Int? = category.get(categoryList[p1].category_name)
                val newValue = categoryList[p1].category_number

                if (category.containsKey(categoryList[p1].category_name)) { //Category is available
                    if (oldValue!! < newValue) { //Show badge
                        Log.i("adapter", (newValue - oldValue).toString())
                        Log.i("adapter", categoryList[p1].category_name)

                        badge.text = (newValue - oldValue).toString()
                        badge.visibility = View.VISIBLE
                    }

                    //creplacing old value with new
                    category.replace(categoryList[p1].category_name, oldValue, newValue)

                    //removing existing hashmap from shared prefeence and adding updated one
                    val catString = Gson().toJson(category)
                    sharedPreferences.edit().remove("categorycount")
                    sharedPreferences.edit().putString("categorycount", catString).apply()
                } else { //Category is not available
                    category.set(categoryList[p1].category_name, categoryList[p1].category_number)
                    val catString = Gson().toJson(category)
                    sharedPreferences.edit().remove("categorycount")
                    sharedPreferences.edit().putString("categorycount", catString).apply()
                }
            }
        } else {
            Log.i("adapter", "first time")
        }
    }

    class MyViewHolder(mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {

        val cvMainContainer = mView.rlCategory!!
        val tvQuizCatName = mView.tvQuizCatName!!
        val cardView = mView.rlCategory!!
        val badge = mView.badge

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