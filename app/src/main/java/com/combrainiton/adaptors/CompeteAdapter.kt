package com.combrainiton.adaptors

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.combrainiton.R
import com.combrainiton.main.BrandHomePage
import com.combrainiton.normalQuiz.ActivityNormalQuizQuestion
import com.combrainiton.subscription.AllBrands
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

class CompeteAdapter(var brands: ArrayList<AllBrands>,var activity: FragmentActivity,var context: Context) : androidx.viewpager.widget.PagerAdapter() {


    lateinit var layoutInflater: LayoutInflater

    override fun getCount(): Int {
        return brands.size
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1 as View
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView: ImageView
        val card : androidx.cardview.widget.CardView

        layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rv: View = layoutInflater.inflate(R.layout.compete_cell_item,container,false)
        container.addView(rv)

        imageView = rv.findViewById(R.id.compete_imageView)
        card = rv.findViewById(R.id.compete_cardView)

        //If image is not available it will be shown
        if (brands[position].brandBanner != ""){
//
//            Glide.with(context)
//                    .load(brands.get(position).brandBanner)
//                    .into(imageView)
//
            Picasso.get()
                    .load(brands[position].brandBanner)
                    .fit()
                    .into(imageView)
        }

        imageView.setOnClickListener(object  : View.OnClickListener{
            override fun onClick(v: View?) {
               //context!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(imagesUri[position])))

                var intent = Intent(activity,BrandHomePage:: class.java)
                intent.putExtra("brandAuther",brands[position].brandAuthor)
                intent.putExtra("brandId",brands[position].brandId)
                intent.putExtra("brandName",brands[position].brandName)
                intent.putExtra("brandImage",brands[position].brandCard)
                activity.startActivity(intent)
            }

        })

        return rv
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


}

