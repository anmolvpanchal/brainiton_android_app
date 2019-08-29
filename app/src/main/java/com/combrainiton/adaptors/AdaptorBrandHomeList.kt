package com.combrainiton.adaptors

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.combrainiton.R
import com.combrainiton.main.BrandHomePage
import com.combrainiton.main.CourseHomePage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.brand_homepage_cell.view.*
import java.util.ArrayList

class AdaptorBrandHomeList(var context: Context, var brandHomeTryList: ArrayList<String>) : RecyclerView.Adapter<AdaptorBrandHomeList.MyViewHolder>() {

    class MyViewHolder : RecyclerView.ViewHolder {

        var imageView: ImageView
        var textView: TextView
        var cardView: CardView

        constructor(view: View) : super(view){
            imageView = view.findViewById(R.id.brand_home_cell_image)
            textView = view.findViewById(R.id.brand_home_cell_title)
            cardView = view.findViewById(R.id.brand_home_cardView)
        }

    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.brand_homepage_cell, p0, false))
    }



     override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

         holder.textView.text = brandHomeTryList.get(position)

         //This will load image to course cardView
         /*Picasso.get()
                 .load(images[position])
                 .fit()
                 .into(holder.imageView)*/


         /*holder.setOnClickListener(object : View.OnClickListener{
             override fun onClick(v: View?) {
                 val intent = Intent(context,CourseHomePage::class.java)
                 context.startActivity(intent)
             }

         })*/

         holder.cardView.setOnClickListener(object: View.OnClickListener{
             override fun onClick(v: View?) {
                 val intent = Intent(context,CourseHomePage::class.java)
                 context.startActivity(intent)
             }

         })

    }


    override fun getItemCount(): Int {
        return brandHomeTryList.size
    }


}