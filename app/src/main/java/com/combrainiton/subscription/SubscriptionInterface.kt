package com.combrainiton.subscription

import retrofit2.Call
import retrofit2.http.*

interface SubscriptionInterface {

    @GET("api/subs/get_all_brands/")
    fun getAllBrands() : Call<BrandsResponseModel>

    @GET("api/subs/brand/{brandID}/view/")
    fun getAllCourses() : Call<CoursesResponseModel>


}