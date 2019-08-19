@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.combrainiton.api


import com.combrainiton.models.CommonResponceModel
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response


/**
 * Created by Dipendra Sharma  on 11-05-2018.
 */
class ApiErrorParser {
    fun errorResponce(responce: Response<*>): CommonResponceModel {
        val errorConvertor: Converter<ResponseBody, CommonResponceModel>? = ApiClient.getClient().responseBodyConverter(CommonResponceModel::class.java, arrayOfNulls(0))
        val errorModel: CommonResponceModel?
        try {
            errorModel = errorConvertor!!.convert(responce.errorBody())
        } catch (e: Exception) {
            return CommonResponceModel()

        }
        return errorModel
    }
}
