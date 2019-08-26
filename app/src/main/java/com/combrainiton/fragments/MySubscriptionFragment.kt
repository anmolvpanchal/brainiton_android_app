package com.combrainiton.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.combrainiton.R
import com.combrainiton.adaptors.CompeteAdapter

class MySubscriptionFragment : Fragment() {

    var images = ArrayList<String>()
    var imagesUri = ArrayList<String>()
    lateinit var viewPager: ViewPager
    lateinit var contexts: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_my_subscription, container, false)

        contexts = container!!.context

        viewPager = view.findViewById(R.id.my_sub_viewPager) as ViewPager

        viewPager.setPageTransformer(true, ViewPagerStack())
        viewPager.offscreenPageLimit = 3
        viewPager.setClipToPadding(false)
        viewPager.setPadding(0, 0, 0, 45)

        initView()

        return view
    }

    fun initView() {

        //This links will be displayed on card
        images.add("http://link.brainiton.in/imgcard4")
        images.add("http://link.brainiton.in/imgcard5")
        images.add("https://i.imgur.com/VFzhBmW.jpg")
        images.add("https://i.imgur.com/eXdt2ND.jpg")
        images.add("https://i.imgur.com/GGCHVIi.jpg")
        images.add("https://i.imgur.com/DH9QbAq.jpg")

        //This links will be opened when corresponding card is clicked
        imagesUri.add("http://link.brainiton.in/txtcard4")
        imagesUri.add("http://link.brainiton.in/txtcard5")
        imagesUri.add("http://link.brainiton.in/txtcard6")

        val adapter: PagerAdapter = CompeteAdapter(images, imagesUri, activity)
        viewPager.adapter = adapter

        viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
                //Log.i("Compete","check")
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                //Log.i("Compete","check")
            }

            override fun onPageSelected(p0: Int) {
            }

        })

    }

    private inner class ViewPagerStack : ViewPager.PageTransformer {
        @Override
        override fun transformPage(page: View, position: Float) {

            // transition 1
//            if (position >= 0) {
//                page.scaleY = 1f - 0.9f * position
//                page.scaleX = 1f
//                page.translationY = -page.width * position
//                page.translationX = 360 * position
//            }


            // transition 2

            if (position >= 0) {

                page.setScaleX(1f - 0.30f * position);
                page.setScaleY(1f);
                page.setTranslationX(-page.getWidth() * position);
                page.setTranslationY(30 * position);
            }
        }
    }
}
