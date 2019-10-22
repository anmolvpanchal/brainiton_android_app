package com.combrainiton.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.combrainiton.R
import com.combrainiton.adaptors.CoursePagerAdapter
import com.combrainiton.fragments.*
import com.combrainiton.subscription.*
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppSharedPreference
import com.ebanx.swipebtn.OnStateChangeListener
import com.ebanx.swipebtn.SwipeButton
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_course_home_page.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class CourseHomePage : AppCompatActivity() {

    var tabLayout: TabLayout? = null
    var viewPager: androidx.viewpager.widget.ViewPager? = null
    var collapseToolbarLayout: CollapsingToolbarLayout? = null
    lateinit var subscriptionButton: SwipeButton
    var subscriptionID: String = ""
    val subscriptionDataList: ArrayList<SubscriptionDataList_API> = ArrayList()
    val lessonsDataList: ArrayList<LessonsDataList_API> = ArrayList()
    val lessonsDataListForAvailable : ArrayList<LessonsDataListForAvaiable_API> = ArrayList()
    var courseId: Int = 0
    lateinit var courseImage: ImageView
    var package_year : String = ""
    var package_quarter : String = ""
    var CourseID : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_home_page)

        CourseID = intent.getIntExtra("course_id",0)
        Log.i("course",CourseID.toString())

        if (intent.getBooleanExtra("from_Subscription", false)) {
            Log.i("course", "Yes, from my subscription")
            swipe_button_layout.visibility = View.GONE
        } else {
            Log.i("course", "No, from available subscription")
            getLessonsFromApiForAvailableSub(CourseID)
        }

        if (intent.getStringExtra("subscription_id") != null) {
            Log.i("course", intent.getStringExtra("subscription_id"))
            subscriptionID = intent.getStringExtra("subscription_id")
            getLessonsFromApiForSubscribedUser(subscriptionID)
        } else {
            Log.i("course", "no subscription id because coming from available subscription")
        }

        if (intent.getIntExtra("course_id",0) != null) {
            courseId = intent.getIntExtra("course_id",0)
        }


        //Getting ids from xml
        viewPager = findViewById(R.id.course_viewPager)
        collapseToolbarLayout = findViewById(R.id.course_CollapseToolbar)
        tabLayout = findViewById(R.id.course_tabLayout)
        subscriptionButton = findViewById(R.id.course_subscriptionButton)
        courseImage = findViewById(R.id.course_imageView)

        subscriptionButton.setOnStateChangeListener(OnStateChangeListener { active ->
            kotlin.run {
                if (active) { //fully swiped
                    val intent = Intent(this@CourseHomePage,ActivityPlanSelect::class.java)
                    intent.putExtra("course_id",courseId)
                    intent.putExtra("package_quarter",package_quarter)
                    intent.putExtra("package_year",package_year)
                    startActivity(intent)
                } else { //when it's unswiped back to normal
                    Toast.makeText(this@CourseHomePage, "Back to unswipe", Toast.LENGTH_LONG).show()
                }
            }
        })

        initView()

        //Creating and adding tab items
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Description"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Lessons"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Progress"))

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

            setTitle(intent.getStringExtra("course_name"))

            //Creates typefaces for fonts to be used
            val bold = ResourcesCompat.getFont(this@CourseHomePage, R.font.raleway_bold)
            val medium = ResourcesCompat.getFont(this@CourseHomePage, R.font.raleway_medium)

            //setting typrface
            setCollapsedTitleTypeface(bold)
            setExpandedTitleTypeface(medium)

            //setting collapsed text color
            setCollapsedTitleTextColor(resources.getColor(R.color.colorTextPrimaryDark))
        }

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

    }

    fun getLessonsFromApiForSubscribedUser(subID: String) {

        //create api client first
        val apiToken: String = AppSharedPreference(this).getString("apiToken")


        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)
        val call = apiClient.getAllLessons(subID.toInt())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(this@CourseHomePage, "Error", resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(this@CourseHomePage, "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return
                }

                try {

                    subscriptionDataList.clear()
                    lessonsDataList.clear()

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)
                    val lessons = rootObj.getJSONArray("lessons")

                    for (i in 0 until lessons.length()) {

                        val innerobject_lesson: JSONObject = lessons.getJSONObject(i)
                        val lesson_id = innerobject_lesson.getString("lesson_id")
                        val lesson_name = innerobject_lesson.getString("lesson_name")
                        val lesson_number = innerobject_lesson.getString("lesson_number")
                        val quiz_image = innerobject_lesson.getString("quiz_image")
                        val lesson_quiz = innerobject_lesson.getString("lesson_quiz")
                        //val course_description = innerobject_lesson.getString("course_description")

                        Log.e("working in CourseHome", " yess" + quiz_image + lesson_name + lesson_id)

                        Log.i("Course","${lesson_name}")

                        lessonsDataList.add(LessonsDataList_API(lesson_id, lesson_name, lesson_number, quiz_image, lesson_quiz))
                    }


                    //This line will reverse the list and will show the first lesson on 1st place
                    Collections.reverse(lessonsDataList)

                    val subscription = rootObj.getJSONArray("subscription")

                    for (j in 0 until subscription.length()) {

                        val innerobject_sub: JSONObject = subscription.getJSONObject(j)

                        val current_lesson_id = innerobject_sub.getString("current_lesson_id")
                        val current_lesson_name = innerobject_sub.getString("current_lesson_name")
                        val subscription_id = innerobject_sub.getString("subscription_id")
                        val current_lesson_number = innerobject_sub.getString("current_lesson_number")
                        val last_lesson_number = innerobject_sub.getString("last_lesson_number")
                        val course_id = innerobject_sub.getString("course_id")
                        val current_lesson_quiz = innerobject_sub.getString("current_lesson_quiz")
                        val course_name = innerobject_sub.getString("course_name")
                        val course_horizontal_image = innerobject_sub.getString("course_horizontal_image")


                        Log.e("working in CourseHome", " yess" + course_name + current_lesson_name + current_lesson_id)

                        //If image is available it will be displayed
                        if(course_horizontal_image != "") {
                            Picasso.get()
                                    .load(course_horizontal_image)
                                    .fit()
                                    .into(courseImage)
                        }


                        subscriptionDataList.add(SubscriptionDataList_API(current_lesson_id, current_lesson_name, subscription_id,
                                current_lesson_number, last_lesson_number, course_id, current_lesson_quiz, course_name))

                    }




                    //creating instance of adapter
                    val adapter = CoursePagerAdapter(supportFragmentManager)

                    //adding fragment through adapter
                    adapter.addFragment(CourseDescriptionFragmentForMySubscription(intent.getStringExtra("course_description")), "Description")
                    adapter.addFragment(CourseLessonsFragment(lessonsDataList, subscriptionDataList,intent.getIntExtra("position",0)), "Lessons")
                    adapter.addFragment(CourseProgressFragment(), "Progress")

                    //setting view pager adapter
                    viewPager!!.adapter = adapter

                    viewPager!!.setCurrentItem(1)

                    viewPager!!.setOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
                        override fun onPageScrollStateChanged(p0: Int) {
                            //Log.i("Compete","check")
                        }

                        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                            //Log.i("Compete","check")
                        }

                        override fun onPageSelected(p0: Int) {
                        }

                    })


                } catch (ex: Exception) {
                    when (ex) {
                        is IllegalAccessException, is IndexOutOfBoundsException -> {
                            Log.e("catch block", "some known exception" + ex)
                        }
                        else -> Log.e("catch block", "other type of exception" + ex)

                    }

                }
            }

        })


    }

    fun getLessonsFromApiForAvailableSub(courseID : Int){


        //create api client first
        val apiToken: String = AppSharedPreference(this).getString("apiToken")

        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)
        val call = apiClient.getLessonsForAvailableSubs(courseID)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(this@CourseHomePage, "Error", this@CourseHomePage.resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(this@CourseHomePage, "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return;
                }

                try {

                    lessonsDataListForAvailable.clear()

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)
                    val course_horizontal_image = rootObj.getString("course_horizontal_image")
                    package_year = rootObj.getString("package_year")
                    package_quarter = rootObj.getString("package_quarter")

                    //If image is available it will be displayed
                    if(course_horizontal_image != "") {
                        Picasso.get()
                                .load(course_horizontal_image)
                                .fit()
                                .into(courseImage)
                    }

                    val lessons = rootObj.getJSONArray("lessons")

                    if(lessons.length().equals(0)){
                        val toast : Toast  = Toast.makeText(this@CourseHomePage,"No Lessons Provided In This Course By Tutor New Lessons ComingSoon .", Toast.LENGTH_SHORT);  // to show toast in center
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show()
                    }else{

                        for (i in 0 until lessons.length()) {

                            val innerobject_lesson: JSONObject = lessons.getJSONObject(i)

                            val lesson_name = innerobject_lesson.getString("lesson_name")
                            val quiz_image = innerobject_lesson.getString("quiz_image")
                            val lesson_number = innerobject_lesson.getString("lesson_number")
                            val lesson_id = innerobject_lesson.getString("lesson_id")
                            val lesson_quiz_id = innerobject_lesson.getString("lesson_quiz_id")

                            Log.e("toCheckQuizID", " yess dsp" + lesson_quiz_id)
                            lessonsDataListForAvailable.add(LessonsDataListForAvaiable_API(lesson_name, quiz_image, lesson_number, lesson_id, lesson_quiz_id))

                        }
                    }

                    val adapter = CoursePagerAdapter(supportFragmentManager)
                    val course_description = intent.getStringExtra("course_description")

                    //adding fragment through adapter
                    adapter.addFragment(CourseDescriptionFragment(course_description), "Description")
                    adapter.addFragment(CourseLessonFragmentForAvailableSubscription(lessonsDataListForAvailable), "Lessons")

                    //setting view pager adapter
                    viewPager!!.adapter = adapter

                    viewPager!!.setCurrentItem(1)

                    viewPager!!.setOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
                        override fun onPageScrollStateChanged(p0: Int) {
                            //Log.i("Compete","check")
                        }

                        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                            //Log.i("Compete","check")
                        }

                        override fun onPageSelected(p0: Int) {
                        }

                    })


                } catch (ex: Exception) {
                    when (ex) {
                        is IllegalAccessException, is IndexOutOfBoundsException -> {
                            Log.e("catch block", "some known exception" + ex)
                        }
                        else -> Log.e("catch block", "other type of exception" + ex)

                    }

                }
            }

        })

    }


}