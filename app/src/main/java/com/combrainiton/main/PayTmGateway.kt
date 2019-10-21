package com.combrainiton.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.api.ApiClient
import com.combrainiton.api.ApiErrorParser
import com.combrainiton.authentication.ActivitySignIn
import com.combrainiton.managers.UserManagementInterface
import com.combrainiton.models.CheckSumModel
import com.combrainiton.models.CommonResponceModel
import com.combrainiton.models.UserResponseModel
import com.combrainiton.utils.Constants
import com.combrainiton.subscription.ServiceGenerator
import com.combrainiton.subscription.SubscriptionInterface
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class PayTmGateway : AppCompatActivity(), PaytmPaymentTransactionCallback {

    var userInfo: UserManagementInterface? = null
    var mobileNo: String = ""
    var checkSumHash: String? = ""
    var customerId: String? = ""
    var orderId: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_tm_gateway)

        getUserPhoneNo()
        //getCheckSum()
    }

/*
    fun getCheckSum() {

        val apiToken: String = AppSharedPreference(this@PayTmGateway).getString("apiToken")

        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)

        val type = HashMap<String, String>()
        type["type"] = "type1"

        val checkSum: Call<CheckSumModel>? = apiClient.getCheckSumHash(type)

        checkSum!!.enqueue(object : Callback<CheckSumModel> {
            override fun onFailure(call: Call<CheckSumModel>, t: Throwable) {
                Log.i("paytm", "failure")
            }

            override fun onResponse(call: Call<CheckSumModel>, response: Response<CheckSumModel>) {
                if (!response.isSuccessful()) {
                    Toast.makeText(this@PayTmGateway, "Something went Wrong!!" + response.code(), Toast.LENGTH_SHORT).show()
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return
                }

                try {
                    Log.i("paytm", "check: "+response.body()!!.checksumhash)
                    Log.i("paytm", "order: "+response.body()!!.orderId)
                    Log.i("paytm", "cust: "+response.body()!!.customerId)

                    checkSumHash = response.body()!!.checksumhash
                    orderId = response.body()!!.orderId
                    customerId = response.body()!!.customerId

                    setOrderObject()

                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.i("paytm", ex.toString())
                }
            }

        })
    }
*/

    private fun setOrderObject() {
        // when app is ready to publish use production service
        //PaytmPGService  Service = PaytmPGService.getProductionService()

        val mDialog = AppProgressDialog(this@PayTmGateway)
        mDialog.show()

        val Service = PaytmPGService.getStagingService()

        val paramMap = HashMap<String, String>()

        paramMap.put("MID", Constants.MID)
        paramMap.put("ORDER_ID" , orderId!!)
        paramMap.put("CUST_ID", customerId!!)
        paramMap.put("CHANNEL_ID", Constants.CHANNEL_ID)
        paramMap.put("TXN_AMOUNT" , "1")
        paramMap.put("WEBSITE", Constants.WEBSITE)
        paramMap.put("INDUSTRY_TYPE_ID", Constants.INDUSTRY_TYPE_ID)
        paramMap.put("CALLBACK_URL", Constants.CALLBACK_URL)
        paramMap.put("CHECKSUMHASH", checkSumHash!!)
        //Not required
        //paramMap.put("MOBILE_NO" , customerId!!)
        //paramMap.put("EMAIL" , "username@emailprovider.com")

        val order = PaytmOrder(paramMap)

        Service.initialize(order, null)
        // start payment service call here
        Service.startPaymentTransaction(this@PayTmGateway, true, true,
                this@PayTmGateway)
    }


    override fun onTransactionResponse(inResponse: Bundle?) {
        Toast.makeText(this@PayTmGateway,"Transaction Response",Toast.LENGTH_SHORT)
        Log.e("paytem","responce value"+inResponse.toString())

        val bundleFromNotifications: Bundle? = inResponse
        bundleFromNotifications?.keySet()?.forEach{
            Log.d("paytem", it + "=> \"" + bundleFromNotifications.get(it) + "\"")
        }

    }

    override fun clientAuthenticationFailed(inErrorMessage: String?) {
        Toast.makeText(this@PayTmGateway,"Client Authentication Failed",Toast.LENGTH_SHORT)
        Log.e("paytem","clientAuthenticationFailed" + inErrorMessage)

    }

    override fun someUIErrorOccurred(inErrorMessage: String?) {
        Toast.makeText(this@PayTmGateway,"UI Error",Toast.LENGTH_SHORT)
        Log.e("paytem","someUIErrorOccurred" + inErrorMessage)
    }

    override fun onTransactionCancel(inErrorMessage: String?, inResponse: Bundle?) {
        Toast.makeText(this@PayTmGateway,"Transaction cancelled",Toast.LENGTH_SHORT)
        Log.e("paytem","onTransactionCancel" + inErrorMessage + "  " + inResponse)
        startActivity(Intent(this@PayTmGateway,ActivityNavCompete::class.java))
        finish()
    }

    override fun networkNotAvailable() {
        Toast.makeText(this@PayTmGateway,"Network not available",Toast.LENGTH_SHORT)
        Log.e("paytem","networkNotAvailable" )

    }

    override fun onErrorLoadingWebPage(iniErrorCode: Int, inErrorMessage: String?, inFailingUrl: String?) {
        Toast.makeText(this@PayTmGateway,"Error Loading WebPage",Toast.LENGTH_SHORT)
        Log.e("paytem","onErrorLoadingWebPage" + inErrorMessage )

    }

    override fun onBackPressedCancelTransaction() {
        Toast.makeText(this@PayTmGateway,"Back Pressed",Toast.LENGTH_SHORT)
        Log.e("paytem","onBackPressedCancelTransaction" )
        startActivity(Intent(this@PayTmGateway,ActivityNavCompete::class.java))
        finish()
    }

    private fun getUserPhoneNo() : String{
        val apiToken: String = AppSharedPreference(this@PayTmGateway).getString("apiToken")

        userInfo = ApiClient.getClient(apiToken).create(UserManagementInterface::class.java)

        val getDetailCall = userInfo!!.getUserDetail()

        getDetailCall.enqueue(object : Callback<UserResponseModel> {
            override fun onResponse(call: Call<UserResponseModel>, response: Response<UserResponseModel>) {
                //mProgressDialog.dialog.dismiss()
                if (response.isSuccessful) {
                    mobileNo = response!!.body()!!.mobile
                } else {
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)
                }
            }

            override fun onFailure(call: Call<UserResponseModel>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                //display error message
                AppAlerts().showAlertMessage(this@PayTmGateway, "Error", getString(R.string.error_server_problem))
            }
        })

        return mobileNo
    }

    private fun isSessionExpire(errorMsgModle: CommonResponceModel) {
        //if error message status is equal to 404
        if (errorMsgModle.status == 404) {

            //show the message to user
            Toast.makeText(this@PayTmGateway, "Your Session Is Expire. Login Again For Continue.", Toast.LENGTH_LONG).show()

            //delete all shared preferences
            AppSharedPreference(this@PayTmGateway).deleteSharedPreference()

            //start login activity
            startActivity(Intent(this@PayTmGateway, ActivitySignIn::class.java)
                    .putExtra("from", "fail"))//add set from key value to "fail"

            //finish current activity
            finish()

        } else {
            //display other message
            AppAlerts().showAlertMessage(this@PayTmGateway, "Error", errorMsgModle.message)
        }
    }

}
