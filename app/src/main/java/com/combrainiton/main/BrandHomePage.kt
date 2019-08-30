package com.combrainiton.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.combrainiton.R
import com.combrainiton.adaptors.AdaptorBrandHomeList
import com.combrainiton.adaptors.CompeteAdapter
import com.combrainiton.utils.ItemOffsetDecoration
import kotlinx.android.synthetic.main.activity_brand_home_page.*
import android.util.TypedValue
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.android.material.appbar.CollapsingToolbarLayout
import androidx.core.content.res.ResourcesCompat
import android.util.Log

class BrandHomePage : AppCompatActivity() {

    private var brandHomeTryList: ArrayList<String> = ArrayList<String>()
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    lateinit var collapseToolbarLayout: CollapsingToolbarLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brand_home_page)

        //getting ids
        recyclerView = findViewById(R.id.compete_brandHomeRecycler)
        collapseToolbarLayout = findViewById(R.id.brand_CollapseToolbar)

        brandHomeTryList.add("first quiz")
        brandHomeTryList.add("second quiz")
        brandHomeTryList.add("third quiz")
        brandHomeTryList.add("fourth quiz")
        brandHomeTryList.add("fifth quiz")
        brandHomeTryList.add("sixth quiz")
        brandHomeTryList.add("seventh quiz")
        brandHomeTryList.add("eight quiz")
        brandHomeTryList.add("nine quiz")

        initView()

        //this will attach category list adapter to category list recycler view
        //BrandHomeRecycler.layoutManager = GridLayoutManager(this@BrandHomePage, 2, GridLayoutManager.VERTICAL, false)
        //BrandHomeRecycler.adapter = AdaptorBrandHomeList(this@BrandHomePage, this@BrandHomePage, brandHomeTryList)

        val spacingInPixel = resources.getDimensionPixelSize(R.dimen.recyclerBrand)

        val adapter = AdaptorBrandHomeList(this,brandHomeTryList)
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        val decoration = ItemOffsetDecoration(2, spacingInPixel, true)
        recyclerView.addItemDecoration(decoration)
        recyclerView.adapter = adapter

        Log.i("Check",spacingInPixel.toString())

    }

    private fun initView(){

        //Setting fontStyle and color to title based on expand and collapse
        collapseToolbarLayout?.apply {

            //setting title
            setTitle("BrandHomePage")

            //Creates typefaces for fonts to be used
            val bold = ResourcesCompat.getFont(this@BrandHomePage, R.font.raleway_bold)
            val medium = ResourcesCompat.getFont(this@BrandHomePage, R.font.raleway_medium)

            //setting typrface
            setCollapsedTitleTypeface(bold)
            setExpandedTitleTypeface(medium)

            //setting collapsed text color
            setCollapsedTitleTextColor(resources.getColor(R.color.colorAccent))
        }

    }

    /*private fun dpToPx(dp: Int): Int {
        val r = resources
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics))
    }*/
}
