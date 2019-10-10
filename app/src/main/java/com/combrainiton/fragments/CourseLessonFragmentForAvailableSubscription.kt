package com.combrainiton.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.combrainiton.R
import com.combrainiton.adaptors.AdapterCourseLessonForAvailableSubscription
import com.combrainiton.subscription.LessonsDataListForAvaiable_API

class CourseLessonFragmentForAvailableSubscription(val lessonsDataListForAvailable: ArrayList<LessonsDataListForAvaiable_API>) : Fragment() {

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_course_lessons, container, false)

        recyclerView = view.findViewById(R.id.course_lessonsRecyclerView)

        val mSearchAdapter = AdapterCourseLessonForAvailableSubscription(context!!, activity!!, lessonsDataListForAvailable)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mSearchAdapter


        return view
    }


}
