package com.combrainiton.fragments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.adaptors.AdapterCourseLesson
import com.combrainiton.adaptors.AdaptorSearchResultList
import com.combrainiton.api.ApiClient
import com.combrainiton.api.ApiErrorParser
import com.combrainiton.authentication.ActivitySignIn
import com.combrainiton.main.ActivityNavExplore
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.managers.NormalQuizManagementInterface
import com.combrainiton.models.CommonResponceModel
import com.combrainiton.models.GetAllQuizResponceModel
import com.combrainiton.subscription.LessonsDataList_API
import com.combrainiton.subscription.SubscriptionDataList_API
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_nav_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseLessonsFragment(val lessonsDataList: ArrayList<LessonsDataList_API>,val subscriptionDataList: ArrayList<SubscriptionDataList_API>) : androidx.fragment.app.Fragment() {

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_course_lessons,container,false)

        //quizList = response.body()!!.quizzes as ArrayList<GetAllQuizResponceModel.Allquizzes>?

        /*val mDialog = AppProgressDialog(context!!)
        mDialog.show()

        NormalQuizManagement(context!!, activity!!, mDialog).getAllQuiz()*/

        Log.i("course","current: ${subscriptionDataList[0].currentLessonName}, quiz: ${subscriptionDataList[0].currentLessonQuiz}")

        recyclerView = view.findViewById(R.id.course_lessonsRecyclerView)
        val mSearchAdapter = AdapterCourseLesson(context!!, activity!!, lessonsDataList,subscriptionDataList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mSearchAdapter

        return view
    }

}
