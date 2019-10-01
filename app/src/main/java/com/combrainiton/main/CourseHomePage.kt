package com.combrainiton.main

import android.content.Intent
import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.adaptors.CoursePagerAdapter
import com.combrainiton.fragments.CourseDescriptionFragment
import com.combrainiton.fragments.CourseLessonsFragment
import com.combrainiton.fragments.CourseProgressFragment
import com.ebanx.swipebtn.OnActiveListener
import com.ebanx.swipebtn.OnStateChangeListener
import com.ebanx.swipebtn.SwipeButton
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import kotlinx.android.synthetic.main.activity_course_home_page.*


class CourseHomePage : AppCompatActivity() {

    var tabLayout: TabLayout? = null
    var viewPager: androidx.viewpager.widget.ViewPager? = null
    var collapseToolbarLayout: CollapsingToolbarLayout? = null
    lateinit var subscriptionButton: SwipeButton

//     val subscription_ID  = intent.getStringExtra("subscription_id")
//     val fromFragmentMysubscription  = intent.getStringExtra("from_Subscription")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_home_page)

        if(intent.getBooleanExtra("from_Subscription",false)){
            Log.i("course","Yes, from my subscription")
        } else{
            Log.i("course","No, from available subscription")
        }

        if(intent.getStringExtra("subscription_id") != null){
            Log.i("course",intent.getStringExtra("subscription_id"))
        } else{
            Log.i("course","no subscription id because coming from available subscription")
        }



//
//        if (fromFragmentMysubscription.equals(true)){
//            swipe_button_layout.visibility = View.INVISIBLE
//        }

        //Getting ids from xml
        viewPager = findViewById(R.id.course_viewPager)
        collapseToolbarLayout = findViewById(R.id.course_CollapseToolbar)
        tabLayout = findViewById(R.id.course_tabLayout)
        subscriptionButton = findViewById(R.id.course_subscriptionButton)

        //When user swipes to the end
        //subscriptionButton.onSwipedOnListener = { Toast.makeText(this@CourseHomePage,"Checked",Toast.LENGTH_LONG).show() }

        subscriptionButton.setOnStateChangeListener( OnStateChangeListener {
            active -> kotlin.run {
            if (active){ //fully swiped
                startActivity(Intent(this,ActivityPlanSelect::class.java))
            } else{ //when it's unswiped back to normal
                Toast.makeText(this@CourseHomePage,"Back to unswipe",Toast.LENGTH_LONG).show()
            }
        }
        })

        initView()

        //Creating and adding tab items
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Description"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Lessons"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Progress"))

        //creating instance of adapter
        var adapter = CoursePagerAdapter(supportFragmentManager)

        //adding fragment through adapter
        adapter.addFragment(CourseDescriptionFragment(),"Description")
        adapter.addFragment(CourseLessonsFragment(),"Lessons")
        adapter.addFragment(CourseProgressFragment(),"Progress")

        //setting view pager adapter
        viewPager!!.adapter = adapter

        //setting tab layout with view pager
        tabLayout?.setupWithViewPager(viewPager)

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    private fun initView() {
        //remove back button from toolbar
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        }

        //Setting fontStyle and color to title based on expand and collapse
        collapseToolbarLayout?.apply {

            //setting title
            setTitle("Course")

            //Creates typefaces for fonts to be used
            val bold = ResourcesCompat.getFont(this@CourseHomePage, R.font.raleway_bold)
            val medium = ResourcesCompat.getFont(this@CourseHomePage, R.font.raleway_medium)

            //setting typrface
            setCollapsedTitleTypeface(bold)
            setExpandedTitleTypeface(medium)

            //setting collapsed text color
            setCollapsedTitleTextColor(resources.getColor(R.color.colorAccent))
        }

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }
}