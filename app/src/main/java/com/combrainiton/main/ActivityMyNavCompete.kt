package com.combrainiton.main

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import com.combrainiton.R

import android.view.View
import android.widget.Adapter
import android.widget.Toast
import com.combrainiton.adaptors.CompeteAdapter
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.models.CompeteModel
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_my_nav_compete.*

class ActivityMyNavCompete : AppCompatActivity(),View.OnClickListener{

    var images = ArrayList<String>()
    lateinit var viewPager: ViewPager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_nav_compete)

        initView()

    }

    fun initView(){

        images.add("http://link.brainiton.in/imgcard1")
        images.add("http://link.brainiton.in/imgcard2")
        images.add("http://link.brainiton.in/imgcard3")

        viewPager = findViewById(R.id.compete_viewPager) as ViewPager
        viewPager.setPadding(130,0,130,0)

        val adapter: PagerAdapter = CompeteAdapter(images,this)
        viewPager.adapter = adapter

        viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener{
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


    override fun onClick(v: View?) {
        val id = v?.id

        when (id) {
            R.id.btm_nav_compete -> {} //do nothing
            R.id.btm_nav_enter_pin -> startActivity(Intent(this@ActivityMyNavCompete, ActivityNavEnterPin::class.java))
            R.id.btm_nav_explore -> explore()
            R.id.btm_nav_my_quizzes -> startActivity(Intent(this@ActivityMyNavCompete, ActivityNavMyQuizzes::class.java))
            R.id.btm_nav_profile -> startActivity(Intent(this@ActivityMyNavCompete, ActivityNavMyProfile::class.java))
            R.id.compete_card_image_1 -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://link.brainiton.in/txtcard1")))
            R.id.compete_card_image_2 -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://link.brainiton.in/txtcard2")))
            R.id.compete_card_image_3 -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://link.brainiton.in/txtcard3")))
        }
    }

    //this will open the home activity
    private fun explore() {
        if (NetworkHandler(applicationContext).isNetworkAvailable()) { //if internet is connected
            val mDialog = com.combrainiton.utils.AppProgressDialog(this@ActivityMyNavCompete)
            //mDialog.show(); //show the progress dialog
            // get all quiz data and start he home activity
            NormalQuizManagement(applicationContext, this, mDialog).getAllQuiz()
        } else {
            //show error if network is not connected
            Toast.makeText(applicationContext, resources.getString(R.string.error_network_issue), Toast.LENGTH_SHORT).show()
        }
    }

}
