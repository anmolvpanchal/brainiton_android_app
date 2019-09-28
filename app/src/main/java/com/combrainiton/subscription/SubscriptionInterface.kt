package com.combrainiton.subscription

import retrofit2.Call
import retrofit2.http.*

interface SubscriptionInterface {

    //Getting all brands
    @GET("api/subs/get_all_brands/")
    fun getAllBrands() : Call<BrandsResponseModel>

    //Getting all courses for the brand whose id has been passed
    @GET("api/subs/brand/{brandID}/view/")
    fun getAllCourses(@Path("brandID")brandId: Int) : Call<CoursesResponseModel>


}