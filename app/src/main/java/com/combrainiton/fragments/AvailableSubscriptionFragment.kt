package com.combrainiton.fragments

import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.combrainiton.R
import com.combrainiton.adaptors.CompeteAdapter
import com.combrainiton.adaptors.SubscriptionAdapter
import com.combrainiton.api.ApiClient
import com.combrainiton.main.ActivityNavCompete
import com.combrainiton.subscription.AllBrands
import com.combrainiton.subscription.BrandsResponseModel
import com.combrainiton.subscription.SubscriptionInterface
import com.combrainiton.utils.AppAlerts
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AvailableSubscriptionFragment : androidx.fragment.app.Fragment() {

    var images = ArrayList<String>()
    var imagesUri = ArrayList<String>()
    lateinit var viewPager: ViewPager
    var requestInterface: SubscriptionInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_available_subscription, container, false)

        viewPager = view.findViewById(R.id.avail_sub_viewPager)

        viewPager.setPageTransformer(true, ViewPagerStack())
        viewPager.offscreenPageLimit = 3
        viewPager.setClipToPadding(false)
        viewPager.setPadding(0, 0, 0, 45)

        initView()

        // Inflate the layout for this fragment
        return view


    }

    fun initView() {

        //This links will be displayed on card
        images.add("http://aagamacademy.com/brainiton/delete/brand1.jpeg")
//        images.add("http://link.brainiton.in/imgcard5")
//        images.add("https://i.imgur.com/VFzhBmW.jpg")
//        images.add("https://i.imgur.com/eXdt2ND.jpg")
//        images.add("https://i.imgur.com/GGCHVIi.jpg")
//        images.add("https://i.imgur.com/DH9QbAq.jpg")

        //This links will be opened when corresponding card is clicked
        imagesUri.add("http://link.brainiton.in/txtcard4")
        /*imagesUri.add("http://link.brainiton.in/txtcard5")
        imagesUri.add("http://link.brainiton.in/txtcard6")*/

        getAllBrands()

    }

    private fun getAllBrands() {
        //Getting API client
        requestInterface = ApiClient.getClient().create(SubscriptionInterface::class.java)

        val getBrandCall: Call<BrandsResponseModel>? = requestInterface!!.getAllBrands()

        getBrandCall!!.enqueue(object : Callback<BrandsResponseModel> {

            override fun onFailure(call: Call<BrandsResponseModel>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(context!!, "Error", resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<BrandsResponseModel>, response: Response<BrandsResponseModel>) {
                if(response.isSuccessful){
                    val responce : String = response.body().toString()

                    Log.e("responce_entire","  " + responce)
                    val brands: ArrayList<AllBrands> = response.body()!!.brands as ArrayList<AllBrands>

                    Log.i("sub","${brands[0].brandName}    ${brands[0].brandAuthor}")

                    val adapter: PagerAdapter = CompeteAdapter(brands,activity!!,context!!)
                    viewPager.adapter = adapter

                    viewPager.setOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
                        override fun onPageScrollStateChanged(p0: Int) {
                            //Log.i("Compete","check")
                        }

                        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                            //Log.i("Compete","check")
                        }

                        override fun onPageSelected(p0: Int) {
                        }

                    })
                }
            }

        })
    }

    private inner class ViewPagerStack : androidx.viewpager.widget.ViewPager.PageTransformer {
        @Override
        override fun transformPage(page: View, position: Float) {

            // transition 1
//            if (position >= 0) {
//                page.scaleY = 1f - 0.9f * position
//                page.scaleX = 1f
//                page.translationY = -page.width * position
//                page.translationX = 360 * position
//            }


            // transition 2

            if (position >= 0) {

                page.setScaleX(1f - 0.30f * position);
                page.setScaleY(1f);
                page.setTranslationX(-page.getWidth() * position);
                page.setTranslationY(30 * position);
            }
        }
    }

}
