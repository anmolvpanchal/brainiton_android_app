package com.combrainiton.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.combrainiton.R
import com.combrainiton.adaptors.AdaptorBrandHomeList
import com.combrainiton.utils.ItemOffsetDecoration
import com.google.android.material.appbar.CollapsingToolbarLayout

class BrandHomePage : AppCompatActivity() {

    private var images = ArrayList<String>()
    private var brandHomeTryList: ArrayList<String> = ArrayList<String>()
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    lateinit var collapseToolbarLayout: CollapsingToolbarLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brand_home_page)

        //getting ids
        recyclerView = findViewById(R.id.compete_brandHomeRecycler)
        collapseToolbarLayout = findViewById(R.id.brand_CollapseToolbar)

        images.add("http://aagamacademy.com/brainiton/delete/cell1.jpeg")
        images.add("http://aagamacademy.com/brainiton/delete/cell2.jpeg")
        images.add("http://aagamacademy.com/brainiton/delete/cell3.jpeg")
        images.add("http://aagamacademy.com/brainiton/delete/cell4.jpeg")


        brandHomeTryList.add("Crazy Chemistry")
        brandHomeTryList.add("Fun Biology")
        brandHomeTryList.add("Learn English")
        brandHomeTryList.add("Aptitude and GK")

        initView()

        //this will attach category list adapter to category list recycler view
        //BrandHomeRecycler.layoutManager = GridLayoutManager(this@BrandHomePage, 2, GridLayoutManager.VERTICAL, false)
        //BrandHomeRecycler.adapter = AdaptorBrandHomeList(this@BrandHomePage, this@BrandHomePage, brandHomeTryList)

        val spacingInPixel = resources.getDimensionPixelSize(R.dimen.recyclerBrand)

        val adapter = AdaptorBrandHomeList(this, brandHomeTryList, images)
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        val decoration = ItemOffsetDecoration(2, spacingInPixel, true)
        recyclerView.addItemDecoration(decoration)
        recyclerView.adapter = adapter

        Log.i("Check", spacingInPixel.toString())

    }

    private fun initView() {

        //Setting fontStyle and color to title based on expand and collapse
        collapseToolbarLayout?.apply {

            //setting title
            setTitle("Curious 365")

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
