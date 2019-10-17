package com.combrainiton.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CheckSumModel {

    @SerializedName("CHECKSUMHASH")
    @Expose
    var checksumhash: String? = null
    @SerializedName("ORDERID")
    @Expose
    var orderId: String? = null
    @SerializedName("CUSTOMERID")
    @Expose
    var customerId: String? = null

}