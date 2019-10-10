package com.combrainiton.adaptors

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.combrainiton.R
import com.combrainiton.main.CourseHomePage
import com.combrainiton.subscription.AllCourses
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

class AdaptorBrandHomeList(var context: Context, var courses: ArrayList<AllCourses>, var brandHomeTryList: ArrayList<String>, var images: ArrayList<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<AdaptorBrandHomeList.MyViewHolder>() {

    class MyViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        var imageView: ImageView
        var textView: TextView
        var cardView: androidx.cardview.widget.CardView

        init {
            imageView = view.findViewById(R.id.brand_home_cell_image)
            textView = view.findViewById(R.id.brand_home_cell_title)
            cardView = view.findViewById(R.id.brand_home_cardView)
        }

    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.brand_homepage_cell, p0, false))
    }



     override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

         holder.textView.text = courses[position].courseName

         //If image is not available it will be shown
         if (courses[position].courseImage == ""){

         }else{
             Picasso.get()
                     .load(courses[position].courseImage)
                     .fit()
                     .into(holder.imageView)
         }


         holder.cardView.setOnClickListener(object : View.OnClickListener{
             override fun onClick(v: View?) {
                 val intent = Intent(context,CourseHomePage::class.java)
                 intent.putExtra("from_Subscription",false)
                 intent.putExtra("course_name",courses[position].courseName)
                 context.startActivity(intent)
             }

         })

         holder.cardView.setOnClickListener(object: View.OnClickListener{
             override fun onClick(v: View?) {
                 val intent = Intent(context,CourseHomePage::class.java)
                 intent.putExtra("course_id",courses[position].courseId)
                 intent.putExtra("courseImage",courses[position].courseImage)
                 context.startActivity(intent)
             }

         })

    }


    override fun getItemCount(): Int {
        return courses.size
    }


}
