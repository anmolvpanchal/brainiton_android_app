package com.combrainiton.adaptors

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.combrainiton.models.CompeteModel
import com.combrainiton.R
import java.util.*
import kotlin.collections.ArrayList

class CompeteAdapter : PagerAdapter {

    var images = ArrayList<String>()
    var context: Context
    lateinit var layoutInflater: LayoutInflater

    constructor(images: ArrayList<String>, context: Context) : super() {
        this.images = images
        this.context = context
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1 as View
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var imageView: ImageView

        //inflating the layout
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var rv: View = layoutInflater.inflate(R.layout.compete_item,container,false)
        imageView = rv.findViewById(R.id.compete_imageView)

        Glide.with(context)
                .load(images[position])
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView)

        container!!.addView(rv)

        imageView.setOnClickListener(object  : View.OnClickListener{
            override fun onClick(v: View?) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(images[position])))
            }

        })

        return rv
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container!!.removeView(`object` as View)
    }

}