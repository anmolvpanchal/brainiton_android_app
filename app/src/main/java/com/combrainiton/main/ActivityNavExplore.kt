package com.combrainiton.main

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.adaptors.AdaptorCategoryList
import com.combrainiton.adaptors.AdaptorFeaturedQuizPrevious
import com.combrainiton.adaptors.AdaptorFeaturedQuizToday
import com.combrainiton.models.GetAllQuizResponceModel
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_nav_explore.*

@SuppressLint("NewApi")
@Suppress("UNCHECKED_CAST")
class ActivityNavExplore : AppCompatActivity(), View.OnClickListener {

    private lateinit var featuredQuizzesList: ArrayList<GetAllQuizResponceModel.Allquizzes> //list for all the featured quizzes
    private lateinit var quizList: ArrayList<GetAllQuizResponceModel.Allquizzes> //list for all the quizzes
    private lateinit var categoryList: ArrayList<GetAllQuizResponceModel.CategoryList> //list for all the categories
    private var doubleBackToExitPressedOnce = false // to close app

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_explore)

        Log.e("Explore", FirebaseInstanceId.getInstance().getToken())

        //this will initialize the main view
        initMainView()

        Log.i("Explore", FirebaseInstanceId.getInstance().getToken())

        //this will initialize the bottom nav bar
        initBottomMenu()

        //creatting notification channel
        createNotificationChannel()


        // this is to set the colors of refreshing
        swipeToRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)

        // Initialize the handler instance
        //val mHandler = Handler()

        // this will refresh the whole feed
        swipeToRefresh.setOnRefreshListener {

            //var mRunnable = Runnable {

            //this will initialize the main view
            initMainView()
            swipeToRefresh.isRefreshing = false
            //}

            // Execute the task after specified time
//            mHandler.postDelayed(
//                    mRunnable,
//                    ((3) * 1000).toLong() // Delay max 1 to 5 seconds
//            )
        }


    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var name = "General"
            var description = "General message"
            var importance = NotificationManager.IMPORTANCE_HIGH
            var channel = NotificationChannel("28+", name, importance)
            channel.description = description
            //channel.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    //this will initialize all the views
    private fun initMainView() {

        //This will get he list of all quizzes and category list
        quizList = (intent.getSerializableExtra("allQuiz") as ArrayList<GetAllQuizResponceModel.Allquizzes>?)!!
        categoryList = (intent.getSerializableExtra("categoryList") as ArrayList<GetAllQuizResponceModel.CategoryList>?)!!
        featuredQuizzesList = (intent.getSerializableExtra("featured_quizzes") as ArrayList<GetAllQuizResponceModel.Allquizzes>?)!!

        //this will load the main featured quiz on the page
        rv_featured_quiz_today.layoutManager = GridLayoutManager(this@ActivityNavExplore, 1, GridLayoutManager.HORIZONTAL, false)
        rv_featured_quiz_today.adapter = AdaptorFeaturedQuizToday(this@ActivityNavExplore, this@ActivityNavExplore, featuredQuizzesList)

        //this will attach featured quiz adapter to featured quiz recycler view
        rv_featured_quiz_previous.layoutManager = GridLayoutManager(this@ActivityNavExplore, 1, GridLayoutManager.HORIZONTAL, false)
        rv_featured_quiz_previous.adapter = AdaptorFeaturedQuizPrevious(this@ActivityNavExplore, this@ActivityNavExplore, featuredQuizzesList)

        //this will attach category list adapter to category list recycler view
        rv_category_list.layoutManager = GridLayoutManager(this@ActivityNavExplore, 2, GridLayoutManager.HORIZONTAL, false)
        rv_category_list.adapter = AdaptorCategoryList(this@ActivityNavExplore, this@ActivityNavExplore, categoryList, quizList, this@ActivityNavExplore, featuredQuizzesList)

        // add on click listener to the top bar seach button
        top_bar_search_button.setOnClickListener(this@ActivityNavExplore)

    }

    //initiates bottom navigation bar
    private fun initBottomMenu() {
        btm_nav_enter_pin.setOnClickListener { startActivity(Intent(this@ActivityNavExplore, ActivityNavEnterPin::class.java))
            (it.context as ActivityNavExplore).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        //btm_nav_explore.setOnClickListener { do nothing }
        btm_nav_my_quizzes.setOnClickListener {
            startActivity(Intent(this@ActivityNavExplore, ActivityNavMyQuizzes::class.java))
            (it.context as ActivityNavExplore).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        btm_nav_profile.setOnClickListener { startActivity(Intent(this@ActivityNavExplore, ActivityNavMyProfile::class.java))
            (it.context as ActivityNavExplore).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        btm_nav_compete.setOnClickListener { startActivity(Intent(this@ActivityNavExplore, CourseHomePage::class.java))
            (it.context as ActivityNavExplore).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    //onclick functions for search button and category list
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.top_bar_search_button -> {
                openSearchActivity() // opens search
            }
            R.id.tvQuizCatName -> {
                openCategory(p0.tag as Int) //opens search for category
            }

        }
    }

    //opens search activity
    private fun openSearchActivity() {
        featuredQuizzesList.addAll(quizList)
        startActivity(Intent(this@ActivityNavExplore, ActivityNavSearch::class.java) //open search acitivity
                .putExtra("quizData", featuredQuizzesList)//pass featured quiz list after adding all quiz into it
                .putExtra("from", "home") //pass "from" key with value "home"
                .putExtra("categoryName", "FeaturedQuizzes")) //pass default filter keyword "FeaturedQuizzes" which will display featured quiz by default
        finish() // close current acitivtiy
    }

    //opens category
    private fun openCategory(selectedCategory: Int) {
        val categoryId = categoryList[selectedCategory].category_id //get selected category name from category list
        startActivity(Intent(this@ActivityNavExplore, ActivityNavSearch::class.java) //open search activtiy
                .putExtra("quizData", getCategoryData(categoryId)) //pass category data using getCategoryFunction(int cateory id)
                .putExtra("from", "category")   //pass "from" key with value "category"
                .putExtra("categoryName", categoryList[selectedCategory].category_name)) //pass filter category name as filter keyword which will show quiz of that cateory
        finish()
    }

    //get the data of category from id
    private fun getCategoryData(id: Int): ArrayList<GetAllQuizResponceModel.Allquizzes> {
        val categoryQuizData = ArrayList<GetAllQuizResponceModel.Allquizzes>() //get all quiz from all quiz respoonse model
        for (i in quizList.indices) {   // for each object of quiz in the list
            val categoryId = quizList[i].category_id  // get the cateory id of that object
            if (categoryId == id) { // of category id euals to the var "id" (the id passed as argument)
                categoryQuizData.add(quizList[i]) //add that quiz object into the categoryQuizData list
            }
        }
        return categoryQuizData // return the list
    }

    //this fucntion is called when user clicks on back button {for noobs}
    override fun onBackPressed() {

        //put task in backgrund instead of finishing it
        //moveTaskToBack(true)

        if (doubleBackToExitPressedOnce) {    // checking the amount of times back pressed
            super.onBackPressed()
            finishAffinity()
            return
        }

        this.doubleBackToExitPressedOnce = true

        val toast : Toast  = Toast.makeText(this,"Please click BACK again to exit", Toast.LENGTH_SHORT);  // to show toast in center
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000 ) // time in which second back should press and quit app


    }

}