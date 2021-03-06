package com.combrainiton.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.combrainiton.R
import com.combrainiton.adaptors.AdaptorBrandHomeList
import com.combrainiton.utils.ItemOffsetDecoration
import com.google.android.material.appbar.CollapsingToolbarLayout
import androidx.core.content.res.ResourcesCompat
import android.util.Log
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.combrainiton.adaptors.CompeteAdapter
import com.combrainiton.api.ApiClient
import com.combrainiton.managers.NormalQuizManagementInterface
import com.combrainiton.models.GetAllQuizResponceModel
import com.combrainiton.subscription.*
import com.combrainiton.utils.AppAlerts
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class BrandHomePage : AppCompatActivity() {

    private var images = ArrayList<String>()
    private var brandHomeTryList: ArrayList<String> = ArrayList<String>()
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    lateinit var collapseToolbarLayout: CollapsingToolbarLayout
    var requestInterface: SubscriptionInterface? = null
    lateinit var brandImage: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brand_home_page)

        //getting ids
        recyclerView = findViewById(R.id.compete_brandHomeRecycler)
        collapseToolbarLayout = findViewById(R.id.brand_CollapseToolbar)
        brandImage = findViewById(R.id.brand_imageView)

        initView()

        //this will attach category list adapter to category list recycler view
        //BrandHomeRecycler.layoutManager = GridLayoutManager(this@BrandHomePage, 2, GridLayoutManager.VERTICAL, false)
        //BrandHomeRecycler.adapter = AdaptorBrandHomeList(this@BrandHomePage, this@BrandHomePage, brandHomeTryList)

    }

    private fun initView(){

        //If image is available it will be displayed
        if(intent.getStringExtra("brandImage") != "") {
            Picasso.get()
                    .load(intent.getStringExtra("brandImage"))
                    .fit()
                    .into(brandImage)
        }

        //Setting fontStyle and color to title based on expand and collapse
        collapseToolbarLayout?.apply {

            //setting title
            setTitle(intent.getStringExtra("brandName"))

            //Creates typefaces for fonts to be used
            val bold = ResourcesCompat.getFont(this@BrandHomePage, R.font.raleway_bold)
            val medium = ResourcesCompat.getFont(this@BrandHomePage, R.font.raleway_medium)

            //setting typrface
            setCollapsedTitleTypeface(bold)
            setExpandedTitleTypeface(medium)

            //setting collapsed text color
            setCollapsedTitleTextColor(resources.getColor(R.color.colorTextPrimaryDark))

            getAllCourses()
        }

    }

    private fun getAllCourses() {
        //Getting API client
        requestInterface = ApiClient.getClient().create(SubscriptionInterface::class.java)

        Log.e("intent data","brandID" + intent.getIntExtra("brandId",0))

        val getCoursesCall: Call<CoursesResponseModel>? = requestInterface!!.getAllCourses(intent.getIntExtra("brandId",0))

        getCoursesCall!!.enqueue(object : Callback<CoursesResponseModel> {
            override fun onFailure(call: Call<CoursesResponseModel>, t: Throwable) {
                AppAlerts().showAlertMessage(this@BrandHomePage, "Error", resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<CoursesResponseModel>, response: Response<CoursesResponseModel>) {
                if(response.isSuccessful){
                    val courses: ArrayList<AllCourses> = response.body()!!.courses as ArrayList<AllCourses>

                    //Showing latest courses first
                    Collections.reverse(courses)

                    val spacingInPixel = resources.getDimensionPixelSize(R.dimen.recyclerBrand)

                    val adapter = AdaptorBrandHomeList(this@BrandHomePage,courses)
                    recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@BrandHomePage, 2)
                    val decoration = ItemOffsetDecoration(2, spacingInPixel, true)
                    recyclerView.addItemDecoration(decoration)
                    recyclerView.adapter = adapter
                }
            }


        })
    }

    /*private fun dpToPx(dp: Int): Int {
        val r = resources
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics))
    }*/
}
