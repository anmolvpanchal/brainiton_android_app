package com.combrainiton.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.combrainiton.R
import com.combrainiton.adaptors.AdaptorBrandHomeList
import kotlinx.android.synthetic.main.activity_brand_home_page.*



class BrandHomePage : AppCompatActivity() {

    private var brandHomeTryList: ArrayList<String> = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brand_home_page)

        brandHomeTryList.add("first quiz")
        brandHomeTryList.add("second quiz")
        brandHomeTryList.add("third quiz")
        brandHomeTryList.add("fourth quiz")
        brandHomeTryList.add("fifth quiz")
        brandHomeTryList.add("sixth quiz")
        brandHomeTryList.add("seventh quiz")
        brandHomeTryList.add("eight quiz")
        brandHomeTryList.add("nine quiz")


        val spanCount = 2 // 2 columns
        val spacing = 15 // 20px
        val includeEdge = true



        //this will attach category list adapter to category list recycler view
        BrandHomeRecycler.layoutManager = GridLayoutManager(this@BrandHomePage, 2, GridLayoutManager.VERTICAL, false)
        BrandHomeRecycler.adapter = AdaptorBrandHomeList(this@BrandHomePage, this@BrandHomePage, brandHomeTryList)
        BrandHomeRecycler.addItemDecoration(SpacesItemDecoration(spacing))



    }
}
