package com.combrainiton.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.combrainiton.R
import com.combrainiton.subscription.ServiceGenerator
import com.combrainiton.subscription.SubscriptionInterface
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppSharedPreference
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_plan_select.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import retrofit2.Callback


class ActivityPlanSelect : AppCompatActivity(), PurchasesUpdatedListener {

    var enteredCode: String = ""
    var courseId: Int = 0
    lateinit var alertDialog: AlertDialog
    lateinit var viewGroup: ViewGroup
    lateinit var builder: AlertDialog.Builder
    var selectedPlan: Int = 0
    lateinit var billingClient: BillingClient
    var orderId : String = ""
    var Sub_package_quarter = ""
    var Sub_package_year = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_select)
        cardone_bottomtext_planselect.setText(Html.fromHtml("<b>₹ 299 </b><br> ₹99/mon")).toString()
        cardtwo_bottomtext_planselect.setText(Html.fromHtml("<b>₹ 499 </b><br> ₹39/mon")).toString()

        startGoogleBilling()

        secondcard_plan_select_layout.background = resources.getDrawable(R.drawable.planselect_cardselect_background)
        firstcard_plan_select_layout.setBackgroundColor(resources.getColor(R.color.selectplan_background))
        secondcard_plan_select.isSelected = true
        selectedPlan = 499

        Log.i("plan", intent.getIntExtra("course_id", 0).toString())

        try {

             Sub_package_quarter = intent.getStringExtra("package_quarter")
             Sub_package_year = intent.getStringExtra("package_year")
            Log.i("error in plan","Sub_package_quarter" + Sub_package_quarter)

        }catch ( e : Exception){
            Log.i("error in plan","planselect error")
            Toast.makeText(this@ActivityPlanSelect, "Product not available yet", Toast.LENGTH_LONG).show()

        }

        firstcard_plan_select.setOnClickListener {
            firstcard_plan_select_layout.background = resources.getDrawable(R.drawable.planselect_cardselect_background)
            secondcard_plan_select_layout.setBackgroundColor(resources.getColor(R.color.selectplan_background))
            firstcard_plan_select.isSelected = true
            selectedPlan = 299
        }

        secondcard_plan_select.setOnClickListener {
            secondcard_plan_select_layout.background = resources.getDrawable(R.drawable.planselect_cardselect_background)
            firstcard_plan_select_layout.setBackgroundColor(resources.getColor(R.color.selectplan_background))
            secondcard_plan_select.isSelected = true
            selectedPlan = 499
        }


        continue_button_plan_select.setOnClickListener {
            if (selectedPlan.equals(0)) {
                Toast.makeText(this, "please select plan ", Toast.LENGTH_SHORT).show()
            } else {

                if (Sub_package_quarter != null && !Sub_package_quarter.isEmpty() && !Sub_package_quarter.equals("null") &&
                        Sub_package_year != null && !Sub_package_year.isEmpty() && !Sub_package_year.equals("null")){

                    if (billingClient.isReady) {

                        val params = SkuDetailsParams.newBuilder()
                        params.setSkusList(Arrays.asList(Sub_package_quarter, Sub_package_year))
                        params.setType(BillingClient.SkuType.INAPP)

                        billingClient.querySkuDetailsAsync(params.build(), object : SkuDetailsResponseListener {

                            override fun onSkuDetailsResponse(billingResult: BillingResult?, skuDetailsList: MutableList<SkuDetails>?) {

                                val billingFlowParams = BillingFlowParams.newBuilder()

                                if (selectedPlan == 299) {
                                    Log.i("plan", "299")
                                    billingFlowParams.setSkuDetails(skuDetailsList!![0])
                                } else if (selectedPlan == 499) {
                                    Log.i("plan", "499")
                                    billingFlowParams.setSkuDetails(skuDetailsList!![1])
                                }
                                billingClient.launchBillingFlow(this@ActivityPlanSelect, billingFlowParams.build())
                            }

                        })
                    } else {
                        Toast.makeText(this@ActivityPlanSelect, "Billing client is starting", Toast.LENGTH_SHORT).show()
                        startGoogleBilling()
                    }


                }else{


                    Toast.makeText(this@ActivityPlanSelect, "Product not made yet", Toast.LENGTH_SHORT).show()
                }
                /*if (firstcard_plan_select.isSelected){
                    val intent = Intent(this,PayTmGateway::class.java)
                    intent.putExtra("planSelected", selectedPlan)
                    this.startActivity(intent)
                }else if (secondcard_plan_select.isSelected){
                    val intent = Intent(this,PayTmGateway::class.java)
                    intent.putExtra("planSelected", selectedPlan)
                    this.startActivity(intent)
                }else{
                    Toast.makeText(this,"please select plan ",Toast.LENGTH_LONG).show()
                }*/

            }
        }

        nextbutton_plan_select.setOnClickListener {
            if (Validation()) {

                courseId = intent.getIntExtra("course_id", 0)
                enteredCode = enter_subscription_code_eittext.text.toString()
                val requestData = HashMap<String, String>()
                requestData["course"] = courseId.toString() //add quiz id to request data
                requestData["code"] = enteredCode //add question id to request data
                requestData["order"] = ""       //only used when purchased

                Log.e("hashmap", "course id " + requestData["course"] + "entered code " + requestData["code"])
                enterSubscriptionCode(requestData)
            }
        }
    }



    fun startGoogleBilling() {
        billingClient = BillingClient.newBuilder(this@ActivityPlanSelect).setListener(this@ActivityPlanSelect).enablePendingPurchases().build()
        billingClient.startConnection(object : BillingClientStateListener {

            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                if (billingResult!!.responseCode == BillingClient.BillingResponseCode.OK)
                    Log.i("plan", "Billing setup finished: " + billingResult.toString())
            }

            override fun onBillingServiceDisconnected() {
                Toast.makeText(this@ActivityPlanSelect, "Billing service is disconnected", Toast.LENGTH_SHORT).show()
                Log.i("plan", "Billing service disconnected")
            }

        })
    }

    fun destroyBillingClient() {
        billingClient.endConnection()
        Log.i("plan", "Billing service disconnected")

    }

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        if (billingResult!!.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            Log.i("plan",purchases[0].orderId.toString())
            Log.i("plan", "purchase successfull")
            Log.i("plan",billingResult.responseCode.toString())


            for (purchase in purchases) {
                handlePurchase(purchase)
                //allowMultiplePurchases(purchase)
            }

            // closing the connection
            destroyBillingClient()
            // to get order id from server
            getOrderId()

            courseId = intent.getIntExtra("course_id", 0)
            val requestData = HashMap<String, String>()
            requestData["course"] = courseId.toString() //add quiz id to request data
            Log.i("planselect for purchase"," course id " + courseId)
            requestData["code"] = "" //add question id to request data
            requestData["order"] = orderId       //only used when purchased

            // is purchase successful them subscribe the user
            enterSubscriptionCode(requestData)

        } else if (billingResult!!.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.i("plan", "already purchased")
            Toast.makeText(this@ActivityPlanSelect, "You Have Already Purchased This plan", Toast.LENGTH_SHORT).show()

            //checkhistory()
            for (purchase in purchases!!) {
                Log.i("plan", purchase.orderId)
                handlePurchase(purchase)
                //allowMultiplePurchases(purchase)
            }
        } else if (billingResult!!.responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
            Log.i("plan", "developer error")
        } else if (billingResult!!.responseCode == BillingClient.BillingResponseCode.ERROR) {
            Log.i("plan", "error")
        } else if (billingResult!!.responseCode == BillingClient.BillingResponseCode.ITEM_NOT_OWNED) {
            Log.i("plan", "item not owned")
        } else if (billingResult!!.responseCode == BillingClient.BillingResponseCode.ITEM_UNAVAILABLE) {
            Log.i("plan", "item unavailable")
        } else if (billingResult!!.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this@ActivityPlanSelect, "Transaction Was Cancelled By User", Toast.LENGTH_SHORT).show()
            Log.i("plan", "user cancelled")
        } else if (billingResult!!.responseCode == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE) {
            Log.i("plan", "service unavailable")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState === Purchase.PurchaseState.PURCHASED) {

            // Acknowledge the purchase if it hasn't already been acknowledged.
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, object : AcknowledgePurchaseResponseListener {
                    override fun onAcknowledgePurchaseResponse(billingResult: BillingResult?) {
                        if (billingResult!!.responseCode == BillingClient.BillingResponseCode.OK) {
                            Log.i("plan", "acknowledge ok")
                        } else {
                            Log.i("plan", "acknowledge not ok")
                        }
                    }

                })
            }
        }
    }

    fun checkhistory(){

        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,object : PurchaseHistoryResponseListener{
            override fun onPurchaseHistoryResponse(billingResult: BillingResult?, purchaseHistoryRecordList: MutableList<PurchaseHistoryRecord>?) {
                if(billingResult!!.responseCode == BillingClient.BillingResponseCode.OK){
                    Log.i("plan","history")
                }
            }

        })
    }

    fun allowMultiplePurchases(purchase: Purchase) {

        if (purchase.purchaseState === Purchase.PurchaseState.PURCHASED) {
            Log.i("plan", "1")
            val consumeParams =
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .setDeveloperPayload(purchase.developerPayload)
                            .build()
            billingClient.consumeAsync(consumeParams, object : ConsumeResponseListener {
                override fun onConsumeResponse(billingResult: BillingResult?, purchaseToken: String?) {
                    Log.i("plan", "consume")
                    if (billingResult!!.responseCode == BillingClient.BillingResponseCode.OK && purchaseToken != null) {
                        Log.i("plan", "multiple purchase allowed")
                    } else {
                        Log.i("plan", "multiple purchase not allowed")
                    }
                }
            })
        } else {
            Log.i("plan", "else")
        }

    }

    fun alertDialogForCorrectCode() {
        viewGroup = findViewById(android.R.id.content) //This is an in-built id and not made by programmer
        val dialogView = LayoutInflater.from(this).inflate(R.layout.correct_subscription_code_popup, viewGroup, false)
        builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        alertDialog = builder.create()

        //This won't allow dialog to dismiss if touched outside it's area
        alertDialog.setCanceledOnTouchOutside(false)

        //Transparent background for alert dialog
        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //Creating topic for subscribed user
        FirebaseMessaging.getInstance().subscribeToTopic("subscribed")
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.i("plan", "cannot subscrib to topic subscribed")
                    } else {
                        Log.i("plan", "subscrib to topic subscribed")
                    }
                }

        //removing from unsubscribed topic of firebase
        FirebaseMessaging.getInstance().unsubscribeFromTopic("general")
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.i("plan", "cannot unsubscribe from geneal")
                    } else {
                        Log.i("plan", "unsubscribe from geneal")
                    }
                }

        alertDialog.show()

        val button: Button? = dialogView.findViewById(R.id.correct_subscription_btn)

        button?.setOnClickListener {
            startActivity(Intent(this@ActivityPlanSelect, ActivityNavCompete::class.java))
            alertDialog.dismiss()
        }

    }

    fun alertDialogForWrongCode() {
        viewGroup = findViewById(android.R.id.content) //This is an in-built id and not made by programmer
        val dialogView = LayoutInflater.from(this).inflate(R.layout.incorrect_subscription_code_popup, viewGroup, false)
        builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        alertDialog = builder.create()

        //This won't allow dialog to dismiss if touched outside it's area
        alertDialog.setCanceledOnTouchOutside(false)

        //Transparent background for alert dialog
        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()

        val button: Button? = dialogView.findViewById(R.id.incorrect_subscription_btn)

        button?.setOnClickListener {
            alertDialog.dismiss()
        }

    }


    fun Validation(): Boolean {
        if (enter_subscription_code_eittext.text.isEmpty()) {
            enter_subscription_code_eittext.error = "Please enter code"
            return false
        } else if (enter_subscription_code_eittext.text.length > 6) {
            enter_subscription_code_eittext.error = "Please enter valid code"
            return false
        } else {
            enteredCode = enter_subscription_code_eittext.text.toString()
            return true
        }
    }

    fun enterSubscriptionCode(requestDataMap: Map<String, String>) {
//create api client first
        val apiToken: String = AppSharedPreference(this@ActivityPlanSelect).getString("apiToken")


        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)
        val call = apiClient.subscribeWithCode(requestDataMap)

        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(this@ActivityPlanSelect, "Error", resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.code() == 400) {
                    if (!response.isSuccessful()) {
                        var jsonObject: JSONObject? = null;
                        try {
                            jsonObject = JSONObject(response.errorBody()?.string());
                            val internalMessage = jsonObject.getString("message")
                            val errorCode = jsonObject.getString("status")

                            Log.e("! Responce ", "some message " + internalMessage + errorCode)

                            if (errorCode.equals("400")) {
                                alertDialogForWrongCode()
                                Toast.makeText(this@ActivityPlanSelect,"Something Went worng while subscribing please contact us !\n" + internalMessage,Toast.LENGTH_LONG).show()
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace();

                        }
                    }

                }

                try {

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)
                    val message = rootObj.getString("message")
                    val errorCode = rootObj.getString("status")

                    Log.e(" Responce ", "some message " + rootObj)

                    if (errorCode.equals("201")) {
                        alertDialogForCorrectCode()
                    }


                } catch (ex: Exception) {
                    when (ex) {
                        is IllegalAccessException, is IndexOutOfBoundsException -> {
                            Log.e("catch block", "some known exception" + ex)
                        }
                        else -> Log.e("catch block", "other type of exception" + ex)

                    }

                }
            }

        })

    }

    fun getOrderId() {

        val apiToken: String = AppSharedPreference(this).getString("apiToken")

        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)

        val type = HashMap<String, String>()

        if (selectedPlan == 299) {
            type["type"] = "type1"
        } else if (selectedPlan == 499) {
            type["type"] = "type2"
        }


        val checkSum: Call<ResponseBody> = apiClient.getCheckSumHash(type)

        checkSum!!.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("paytm", "failure")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful()) {
                    Toast.makeText(this@ActivityPlanSelect, "Something went Wrong!!" + response.code(), Toast.LENGTH_SHORT).show()
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return
                }

                try {
                    val resp = response.body()!!.string()
                    val rootObj = JSONObject(resp)

                    orderId = rootObj.getString("ORDERID")
                    Log.e("paytm", "user orderId madeyo  $orderId")

                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.i("planselect exception", ex.toString())
                }
            }

        })
    }



}
