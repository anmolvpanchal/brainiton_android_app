package com.combrainiton.adaptors

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
import com.combrainiton.R
import com.combrainiton.main.BrandHomePage
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

class CompeteAdapter(var images: ArrayList<String>, var imagesUri: ArrayList<String>, var context: androidx.fragment.app.FragmentActivity?) : androidx.viewpager.widget.PagerAdapter() {


    lateinit var layoutInflater: LayoutInflater

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1 as View
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView: ImageView
        val card : androidx.cardview.widget.CardView

        layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rv: View = layoutInflater.inflate(R.layout.compete_cell_item,container,false)
        container.addView(rv)

        imageView = rv.findViewById(R.id.compete_imageView)
        card = rv.findViewById(R.id.compete_cardView)

        // use picaso as its fast and reduces the complexity of code

        Picasso.get()
                .load(images[position])
                .fit()
                .into(imageView)


        card.setOnClickListener(object  : View.OnClickListener{
            override fun onClick(v: View?) {
               //context!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(imagesUri[position])))

                var intent = Intent(context,BrandHomePage:: class.java)
                context?.startActivity(intent)
            }

        })

        return rv
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


}

