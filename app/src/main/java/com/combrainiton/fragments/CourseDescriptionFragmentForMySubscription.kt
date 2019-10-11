package com.combrainiton.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.combrainiton.R
import kotlinx.android.synthetic.main.fragment_course_description.*

class CourseDescriptionFragmentForMySubscription(val courseDescription: String) : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val  view : View = inflater.inflate(R.layout.fragment_course_description,container,false)

        Log.e("description","message " + courseDescription)

        val description : TextView = view.findViewById(R.id.description_text_for_fragment)

        description.text = courseDescription

        return view

    }
}
