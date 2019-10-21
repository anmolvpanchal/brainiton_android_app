package com.combrainiton.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.combrainiton.R
import com.combrainiton.api.ApiClient
import com.combrainiton.api.ApiErrorParser
import com.combrainiton.authentication.ActivitySignIn
import com.combrainiton.managers.UserManagementInterface
import com.combrainiton.models.CommonResponceModel
import com.combrainiton.models.UserResponseModel
import com.combrainiton.subscription.ServiceGenerator
import com.combrainiton.subscription.SubscriptionInterface
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.utils.Constants
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PayTmGateway : AppCompatActivity(), PaytmPaymentTransactionCallback {
    override fun clientAuthenticationFailed(inErrorMessage: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun someUIErrorOccurred(inErrorMessage: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTransactionCancel(inErrorMessage: String?, inResponse: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun networkNotAvailable() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onErrorLoadingWebPage(iniErrorCode: Int, inErrorMessage: String?, inFailingUrl: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBackPressedCancelTransaction() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var userInfo: UserManagementInterface? = null
    var mobileNo: String = ""
    var checkSumHash: String? = ""
    var customerId: String? = ""
    var orderId: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_tm_gateway)

        //Getting checksum from API
        getCheckSum()
    }

    fun getCheckSum() {

        val apiToken: String = AppSharedPreference(this@PayTmGateway).getString("apiToken")

        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)

        val type = HashMap<String, String>()
        type["type"] = "type1"

        val checkSum: Call<ResponseBody> = apiClient.getCheckSumHash(type)

        checkSum!!.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("paytm", "failure")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful()) {
                    Toast.makeText(this@PayTmGateway, "Something went Wrong!!" + response.code(), Toast.LENGTH_SHORT).show()
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return
                }

                try {
                    val resp = response.body()!!.string()
                    val rootObj = JSONObject(resp)

                    checkSumHash = rootObj.getString("CHECKSUMHASH")
                    orderId = rootObj.getString("ORDERID")
                    customerId = rootObj.getString("CUSTOMERID")

                    Log.e("paytm", "user checkSumHash madeyo  $checkSumHash")
                    Log.e("paytm", "user orderId madeyo  $orderId")
                    Log.e("paytm", "user customerId madeyo  $customerId")

                    //Generating PaytmOrder and calling it's gateway
                    setOrderObjectAndCallGateway()

                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.i("paytm", ex.toString())
                }
            }

        })
    }

    private fun setOrderObjectAndCallGateway() {
        // when app is ready to publish use production service
        //PaytmPGService  Service = PaytmPGService.getProductionService()

        /*val mDialog = AppProgressDialog(this@PayTmGateway)
        mDialog.show()*/

        val Service = PaytmPGService.getStagingService()

        val paramMap = HashMap<String, String>()

        paramMap.put("MID", Constants.MID)
        paramMap.put("ORDER_ID", orderId!!)
        paramMap.put("CUST_ID", customerId!!)
        paramMap.put("CHANNEL_ID", Constants.CHANNEL_ID)
        paramMap.put("TXN_AMOUNT", "1")
        paramMap.put("WEBSITE", Constants.WEBSITE)
        paramMap.put("INDUSTRY_TYPE_ID", Constants.INDUSTRY_TYPE_ID)
        paramMap.put("CALLBACK_URL", "https://securegw-stage.paytm.in/order/status")
        paramMap.put("CHECKSUMHASH", checkSumHash!!)
        //Not required
        //paramMap.put("MOBILE_NO" , customerId!!)
        //paramMap.put("EMAIL" , "username@emailprovider.com")

        val order = PaytmOrder(paramMap)

        Service.initialize(order, null)
        // start payment service call here
        Service.startPaymentTransaction(this@PayTmGateway, true, false,
                this@PayTmGateway)
    }


    override fun onTransactionResponse(inResponse: Bundle?) {
        Toast.makeText(this@PayTmGateway, "Transaction Response", Toast.LENGTH_SHORT)
        Log.e("paytem", "responce value" + inResponse.toString())

        val bundleFromNotifications: Bundle? = inResponse
        bundleFromNotifications?.keySet()?.forEach {
            Log.d("paytem", it + "=> \"" + bundleFromNotifications.get(it) + "\"")
        }

        fun onTransactionResponse(inResponse: Bundle?) {
            Toast.makeText(this@PayTmGateway, "Transaction Response", Toast.LENGTH_SHORT)
            Log.i("paytm", "Response: " + inResponse.toString())
            Log.i("paytm", "lalalalallalalala: ")
        }



        fun getUserPhoneNo(): String {
            val apiToken: String = AppSharedPreference(this@PayTmGateway).getString("apiToken")

            userInfo = ApiClient.getClient(apiToken).create(UserManagementInterface::class.java)

            val getDetailCall = userInfo!!.getUserDetail()

            getDetailCall.enqueue(object : Callback<UserResponseModel> {
                override fun onResponse(call: Call<UserResponseModel>, response: Response<UserResponseModel>) {
                    //mProgressDialog.dialog.dismiss()
                    if (response.isSuccessful) {
                        mobileNo = response!!.body()!!.mobile
                    } else {
                        val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response) }
                }

                override fun onFailure(call: Call<UserResponseModel>, t: Throwable) {
                    //mProgressDialog.dialog.dismiss()
                    //display error message
                    AppAlerts().showAlertMessage(this@PayTmGateway, "Error", getString(R.string.error_server_problem))
                }
            })

            return mobileNo
        }


        startActivity(Intent(this@PayTmGateway, ActivityNavCompete::class.java))
        finish()
    }
}
