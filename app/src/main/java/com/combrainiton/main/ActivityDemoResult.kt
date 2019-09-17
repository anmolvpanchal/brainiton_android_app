package com.combrainiton.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.combrainiton.R
import com.combrainiton.adaptors.AdapterResultDemo

class ActivityDemoResult : AppCompatActivity() {

    var answer = arrayOf(true,false,true,false,true,false,true,false,true,false,true,false)
    lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_result)

        val adapter = AdapterResultDemo(this,answer)
        recycler = findViewById(R.id.demo_result_recycler)
        recycler.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        recycler.adapter = adapter
    }
}
