package com.combrainiton.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.combrainiton.R
import com.combrainiton.adaptors.AdapterCourseLesson
import com.combrainiton.adaptors.CoursePagerAdapter
import com.combrainiton.subscription.LessonsDataList_API
import com.combrainiton.subscription.SubscriptionDataList_API

class CourseLessonsFragment (val lessonsDataList: ArrayList<LessonsDataList_API>, val subscriptionDataList: ArrayList<SubscriptionDataList_API>) : Fragment() {

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_course_lessons, container, false)


        //quizList = response.body()!!.quizzes as ArrayList<GetAllQuizResponceModel.Allquizzes>?

        /*val mDialog = AppProgressDialog(context!!)
        mDialog.show()

        NormalQuizManagement(context!!, activity!!, mDialog).getAllQuiz()*/

        Log.i("course", "current: ${subscriptionDataList[0].currentLessonName}, quiz: ${subscriptionDataList[0].currentLessonQuiz}")

        recyclerView = view.findViewById(R.id.course_lessonsRecyclerView)
        val mSearchAdapter = AdapterCourseLesson(context!!, activity!!, lessonsDataList, subscriptionDataList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mSearchAdapter

        return view
    }


}
