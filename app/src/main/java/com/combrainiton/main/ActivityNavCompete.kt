package com.combrainiton.main

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.adaptors.CompeteAdapter
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_nav_compete.*
import kotlinx.android.synthetic.main.activity_nav_explore.*
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_compete
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_enter_pin
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_explore
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_my_quizzes
import kotlinx.android.synthetic.main.activity_nav_explore.btm_nav_profile
import kotlin.collections.ArrayList


class ActivityNavCompete : AppCompatActivity(), View.OnClickListener {


    var images = ArrayList<String>()
    var imagesUri = ArrayList<String>()
    lateinit var viewPager: ViewPager

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_compete)

        initView()
        initBottomMenu()

        viewPager.setPageTransformer(true, ViewPagerStack())
        viewPager.offscreenPageLimit = 3
        viewPager.setClipToPadding(false)
        viewPager.setPadding(0, 0, 0, 45);
    }

    @SuppressLint("ResourceAsColor")
    override fun onClick(p0: View?) {

        when (p0?.id) {

            R.id.nav_compete_mysubscription -> {
                nav_compete_mysubscription.setBackgroundColor(R.color.colorTransperentDark)
                nav_compete_availableSub.setTextColor(R.color.colorTransperentLight)
            }

            R.id.nav_compete_availableSub -> {
                nav_compete_availableSub.setBackgroundColor(R.color.colorTransperentDark)
                nav_compete_mysubscription.setTextColor(R.color.colorTransperentLight)
            }
        }

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

        viewPager = findViewById(R.id.compete_viewPager) as ViewPager

        val adapter: PagerAdapter = CompeteAdapter(images, imagesUri, this)
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

    //initiates bottom navigation bar
    private fun initBottomMenu() {
        btm_nav_enter_pin.setOnClickListener {
            startActivity(Intent(this@ActivityNavCompete, ActivityNavEnterPin::class.java))
            (it.context as ActivityNavCompete).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        btm_nav_explore.setOnClickListener { explore() }
        btm_nav_my_quizzes.setOnClickListener {
            startActivity(Intent(this@ActivityNavCompete, ActivityNavMyQuizzes::class.java))
            (it.context as ActivityNavCompete).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        btm_nav_profile.setOnClickListener {
            startActivity(Intent(this@ActivityNavCompete, ActivityNavMyProfile::class.java))
            (it.context as ActivityNavCompete).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        btm_nav_compete.setOnClickListener {
            //do nothimg
        }
    }


    //this will open the home activity
    private fun explore() {
        if (NetworkHandler(this@ActivityNavCompete).isNetworkAvailable()) { //if internet is connected
            val mDialog = AppProgressDialog(this@ActivityNavCompete)
            mDialog.show() //show progress dialog
            NormalQuizManagement(this@ActivityNavCompete, this@ActivityNavCompete, mDialog).getAllQuiz() //this will open the home activity after retriving the data
        } else {
            Toast.makeText(this@ActivityNavCompete, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show() //display error message
        }

    }


    override fun onBackPressed() {
        explore()
    }

}

