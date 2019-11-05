package com.combrainiton.main

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.adaptors.AdaptorCategoryList
import com.combrainiton.adaptors.AdaptorFeaturedQuizPrevious
import com.combrainiton.adaptors.AdaptorFeaturedQuizToday
import com.combrainiton.adaptors.SubscriptionAdapter
import com.combrainiton.fragments.AvailableSubscriptionFragment
import com.combrainiton.fragments.MySubscriptionFragment
import com.combrainiton.models.GetAllQuizResponceModel
import com.combrainiton.subscription.ServiceGenerator
import com.combrainiton.subscription.SubscriptionInterface
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.utils.InActiveNotification
import com.combrainiton.utils.ToastBroadcastReceiver
import com.google.android.material.tabs.TabLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_nav_explore.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@SuppressLint("NewApi")
@Suppress("UNCHECKED_CAST")
class ActivityNavExplore : AppCompatActivity(), View.OnClickListener {

    private lateinit var featuredQuizzesList: ArrayList<GetAllQuizResponceModel.Allquizzes> //list for all the featured quizzes
    private lateinit var quizList: ArrayList<GetAllQuizResponceModel.Allquizzes> //list for all the quizzes
    private lateinit var categoryList: ArrayList<GetAllQuizResponceModel.CategoryList> //list for all the categories
    var category: HashMap<String, Int> = HashMap<String,Int>()
    var flag: Boolean = false
    private var doubleBackToExitPressedOnce = false // to close app
    lateinit var inActiveNotiIntent: Intent
    lateinit var inActiveNotification: InActiveNotification

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_explore)


        Log.e("Explore", FirebaseInstanceId.getInstance().getToken().toString())

        //this will initialize the main view
        initMainView()

        //this will initialize the bottom nav bar
        initBottomMenu()

        //Background notification service
        inActiveNotification = InActiveNotification()
        inActiveNotiIntent = Intent(this, InActiveNotification::class.java)
        if (!isMyServiceRunning(InActiveNotification::class.java)) {
            startService(inActiveNotiIntent)
        }

        //creating notification channel
        createNotificationChannel()

        //Saving last app opened date to sharedpreference
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        Log.i("explore",dateFormat.format(Date()))
        val sharedPreference = getSharedPreferences("InActiveNotification", Context.MODE_PRIVATE)
        val edit = sharedPreference.edit()
        if(getSharedPreferences("InActiveNotification",Context.MODE_PRIVATE).contains("lastDate")){
            edit.remove("lastDate")
            edit.apply()
            edit.putString("lastDate",dateFormat.format(Date()))
            edit.apply()
        } else{
            edit.putString("lastDate",dateFormat.format(Date()))
            edit.apply()
        }

        checkSubscriptionForNotificationTopic()

        // this is to set the colors of refreshing
        swipeToRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
        swipeToRefresh.setOnRefreshListener {
            //this will initialize the main view
            initMainView()
        }


    }

    override fun onDestroy() {
        stopService(inActiveNotiIntent)
        super.onDestroy()
    }

    fun isMyServiceRunning(serviceClass: Class<InActiveNotification>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for(service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Int.MAX_VALUE)){
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running")
                return true
            }
        }

        Log.i ("Service status", "Not running")
        return false
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
        val sharedPreference = getSharedPreferences("CategoryCount", Context.MODE_PRIVATE)

        try {
            val categoryString = sharedPreference.getString("categorycount",null)

            flag = categoryString != null
        } catch (e: Exception) {
            flag = false
            Log.i("explore","exception")
            e.printStackTrace()
        }

        if(!flag){
            var i = 0
            while (i < categoryList.size){
                category.set(categoryList[i].category_name, categoryList[i].category_number)
                i += 1
            }

            //Converting hashmap to gson to store it in shared preference
            val categoryString = Gson().toJson(category)
            sharedPreference.edit().putString("categorycount",categoryString).apply()
        }

        Log.i("explore",flag.toString())

        //this will load the main featured quiz on the page
        rv_featured_quiz_today.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@ActivityNavExplore, 1, androidx.recyclerview.widget.GridLayoutManager.HORIZONTAL, false)
        rv_featured_quiz_today.adapter = AdaptorFeaturedQuizToday(this@ActivityNavExplore, this@ActivityNavExplore, featuredQuizzesList)

        //this will attach featured quiz adapter to featured quiz recycler view
        rv_featured_quiz_previous.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@ActivityNavExplore, 1, androidx.recyclerview.widget.GridLayoutManager.HORIZONTAL, false)
        rv_featured_quiz_previous.adapter = AdaptorFeaturedQuizPrevious(this@ActivityNavExplore, this@ActivityNavExplore, featuredQuizzesList)

        //this will attach category list adapter to category list recycler view
        rv_category_list.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@ActivityNavExplore, 2, androidx.recyclerview.widget.GridLayoutManager.HORIZONTAL, false)
        rv_category_list.adapter = AdaptorCategoryList(this@ActivityNavExplore, this@ActivityNavExplore, categoryList, quizList, this@ActivityNavExplore, featuredQuizzesList,flag)

        // add on click listener to the top bar seach button
        top_bar_search_button.setOnClickListener(this@ActivityNavExplore)

        swipeToRefresh.isRefreshing = false


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
        btm_nav_premium.setOnClickListener { startActivity(Intent(this@ActivityNavExplore, ActivityNavCompete::class.java))
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
        featuredQuizzesList.clear()
        featuredQuizzesList.addAll(quizList)
        startActivity(Intent(this@ActivityNavExplore, ActivityNavSearch::class.java) //open search acitivity
                .putExtra("quizData", featuredQuizzesList)//pass featured quiz list after adding all quiz into it
                .putExtra("from", "home") //pass "from" key with value "home"
                .putExtra("categoryName", "FeaturedQuizzes")) //pass default filter keyword "FeaturedQuizzes" which will display featured quiz by default
        //finish() // close current acitivtiy
    }

    //opens category
    private fun openCategory(selectedCategory: Int) {
        val categoryId = categoryList[selectedCategory].category_id //get selected category name from category list
        Log.e("catagory id ", categoryId.toString())
        startActivity(Intent(this@ActivityNavExplore, ActivityNavSearch::class.java) //open search activtiy
                .putExtra("quizData", getCategoryData(categoryId)) //pass category data using getCategoryFunction(int cateory id)
                .putExtra("from", "category")   //pass "from" key with value "category"
                .putExtra("categoryName", categoryList[selectedCategory].category_name)) //pass filter category name as filter keyword which will show quiz of that cateory
        //finish()
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

    fun checkSubscriptionForNotificationTopic(){
        //create api client first
        val apiToken: String = AppSharedPreference(applicationContext).getString("apiToken")

        Log.e("sigin token" , "API token "+apiToken)

        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)
        val call = apiClient.getMysubscriptions()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                Toast.makeText(applicationContext, "Something went Wrong to add to firebase!!" , Toast.LENGTH_SHORT).show();

            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(applicationContext, "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return;
                }

                try {

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)
                    val subscriptions = rootObj.getJSONArray("subscriptions")
                    if (subscriptions.length().equals(0)){


                        //Creating topic for subscribed user
                        FirebaseMessaging.getInstance().subscribeToTopic("general")
                                .addOnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        Log.i("plan", "subscribe to general unsucessful")
                                    } else{
                                        Log.i("plan", "subscribe to general")
                                    }
                                }

                        //remove for subscribed user
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("subscribed")
                                .addOnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        Log.i("plan", "cannot unsubscribed from subscribed topic")
                                    } else{
                                        Log.i("plan", "unsubscribed from subscribed topic")
                                    }
                                }


                    }

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