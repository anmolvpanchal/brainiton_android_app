package com.combrainiton.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import com.combrainiton.R
import com.combrainiton.adaptors.MySubscribtionAdapter
import com.combrainiton.subscription.ServiceGenerator
import com.combrainiton.subscription.SubscribedCourse_API
import com.combrainiton.subscription.SubscriptionInterface
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppSharedPreference
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MySubscriptionFragment : androidx.fragment.app.Fragment() {

    var images = ArrayList<String>()
    var imagesUri = ArrayList<String>()
    lateinit var viewPager: androidx.viewpager.widget.ViewPager
    var requestInterface: SubscriptionInterface? = null
    var course_description : String = ""

    val subscribedCourcesList: ArrayList<SubscribedCourse_API> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_my_subscription, container, false)

        viewPager = view.findViewById(R.id.my_sub_viewPager) as androidx.viewpager.widget.ViewPager

        viewPager.setPageTransformer(true, ViewPagerStack())
        viewPager.offscreenPageLimit = 3
        viewPager.setClipToPadding(false)
        viewPager.setPadding(0, 0, 0, 45)

        initView()

        return view
    }


    fun initView() {

        //This links will be displayed on card
        images.add("http://go.brainiton.in/img1")
        images.add("http://link.brainiton.in/imgcard5")
        images.add("https://i.imgur.com/VFzhBmW.jpg")
        images.add("https://i.imgur.com/eXdt2ND.jpg")
        images.add("https://i.imgur.com/GGCHVIi.jpg")
        images.add("https://i.imgur.com/DH9QbAq.jpg")

        //This links will be opened when corresponding card is clicked
        imagesUri.add("http://link.brainiton.in/txtcard4")
        imagesUri.add("http://link.brainiton.in/txtcard5")
        imagesUri.add("http://link.brainiton.in/txtcard6")

        MyCources()

    }

    fun MyCources() {

        //create api client first
        val apiToken: String = AppSharedPreference(activity!!.applicationContext).getString("apiToken")

        Log.e("sigin token" , "API token "+apiToken)

        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)
        val call = apiClient.getMysubscriptions()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(context!!, "Error", resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(activity?.applicationContext, "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return;
                }

                try {

                    subscribedCourcesList.clear()

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)
                    val subscriptions = rootObj.getJSONArray("subscriptions")
                    if (subscriptions.length().equals(0)){
                        val toast : Toast  = Toast.makeText(activity?.applicationContext,"You donot have any Subscription !!\n Please Subscribe From Available Subscription First", Toast.LENGTH_SHORT);  // to show toast in center
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show()
                    }else{

                        for (i in 0 until subscriptions.length()) {

                            val innerobject: JSONObject = subscriptions.getJSONObject(i)

                            val course_image = innerobject.getString("course_image")
                            val lesson_id = innerobject.getString("lesson_id")
                            val lesson_quiz = innerobject.getString("lesson_quiz")
                            val course_name = innerobject.getString("course_name")
                            val subscription_id = innerobject.getString("subscription_id")
                            val course_id = innerobject.getString("course_id")
                            val lesson_name = innerobject.getString("lesson_name")
                            val lesson_number = innerobject.getString("lesson_number")
                            course_description = innerobject.getString("course_description")

                            Log.e("working", " yess" + course_id + lesson_name + course_description)



                            subscribedCourcesList.add(SubscribedCourse_API(course_image, lesson_id, lesson_quiz,
                                    course_name, subscription_id, course_id, lesson_name, lesson_number, course_description))

                        }

                        //This will show latest subscribed courses first
                        Collections.reverse(subscribedCourcesList)

                    }

                    val adapter: PagerAdapter = MySubscribtionAdapter(subscribedCourcesList, activity!!,context!!,viewPager.currentItem,course_description)
                    viewPager.adapter = adapter

                    viewPager.setOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
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


    private inner class ViewPagerStack : androidx.viewpager.widget.ViewPager.PageTransformer {
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
