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
import com.combrainiton.subscription.LessonsDataList_API
import com.combrainiton.subscription.SubscriptionDataList_API

class CourseLessonsFragment (val lessonsDataList: ArrayList<LessonsDataList_API>, val subscriptionDataList: ArrayList<SubscriptionDataList_API>,val position: Int) : Fragment() {

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_course_lessons, container, false)

        Log.i("course", "current: ${subscriptionDataList[position].currentLessonName}, quiz: ${subscriptionDataList[position].currentLessonQuiz}")

        recyclerView = view.findViewById(R.id.course_lessonsRecyclerView)

        val mSearchAdapter = AdapterCourseLesson(context!!, activity!!, lessonsDataList, subscriptionDataList,position)
        recyclerView.layoutManager = LinearLayoutManager(context)
        //This line will scroll directly to the new unlocked lesson
        recyclerView.scrollToPosition(subscriptionDataList[position].currentLessonNumber!!.toInt()-1)
        recyclerView.adapter = mSearchAdapter

        return view
    }


}
