package com.combrainiton.adaptors

import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.combrainiton.R
import com.combrainiton.main.BrandHomePage
import com.combrainiton.main.CourseHomePage
import kotlinx.android.synthetic.main.brand_homepage_cell.view.*
import java.util.ArrayList

class AdaptorBrandHomeList(var context: BrandHomePage, var Activity: BrandHomePage, var brandHomeTryList: ArrayList<String>) : RecyclerView.Adapter<AdaptorBrandHomeList.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val mainContainer = view.brand_home_baseCard
        val title = view.brand_home_cell_title

    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.brand_homepage_cell, p0, false))
    }



     override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

         holder?.title?.text = brandHomeTryList.get(position)

         holder?.mainContainer.setOnClickListener(object : View.OnClickListener{
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
