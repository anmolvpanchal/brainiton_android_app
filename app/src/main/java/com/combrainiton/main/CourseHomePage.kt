package com.combrainiton.main

import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.TabLayout
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.Toolbar
import cn.hugeterry.coordinatortablayout.CoordinatorTabLayout
import cn.hugeterry.coordinatortablayout.listener.OnTabSelectedListener
import com.combrainiton.R
import com.combrainiton.adaptors.CoursePagerAdapter
import com.combrainiton.fragments.CourseDescriptionFragment
import com.combrainiton.fragments.CourseLessonsFragment
import com.combrainiton.fragments.CourseProgressFragment
import kotlinx.android.synthetic.main.activity_course_home_page.*
import kotlinx.android.synthetic.main.activity_course_home_page.course_tabLayout
import kotlinx.android.synthetic.main.activity_course_home_page.course_viewPager

class CourseHomePage : AppCompatActivity() {

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var toolbar: android.support.v7.widget.Toolbar? =null
    var collapseToolbarLayout: CollapsingToolbarLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_home_page)

        //Getting ids from xml
        //coordinatorTabLayout = findViewById<CoordinatorTabLayout>(R.id.course_coordinatorTabLayout)
        viewPager = findViewById<ViewPager>(R.id.course_viewPager)
        toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        collapseToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.course_CollapseToolbar)
        tabLayout = findViewById<TabLayout>(R.id.course_tabLayout)

        setSupportActionBar(toolbar)
        if (getSupportActionBar() != null)
        /*{
            //Sets title and subtitle along with their color for toolbar
            getSupportActionBar()?.setTitle("Courses")
            toolbar?.setTitleTextColor(resources.getColor(R.color.colorAccent))
            getSupportActionBar()?.setSubtitle("jdjd")
            toolbar?.setSubtitleTextColor(resources.getColor(R.color.colorAccent))
        }*/

        //Setting fontStyle and color to title based on expand and collapse
        collapseToolbarLayout?.apply {

            setTitle("Course")
            //Creates typefaces for fonts to be used
            val bold = ResourcesCompat.getFont(this@CourseHomePage, R.font.raleway_bold)
            val medium = ResourcesCompat.getFont(this@CourseHomePage, R.font.raleway_medium)

            //setting typrface
            setCollapsedTitleTypeface(bold)
            setExpandedTitleTypeface(medium)

            //setting collapsed text color
            setCollapsedTitleTextColor(resources.getColor(R.color.colorCategoryFour))
        }

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

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

        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

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
}