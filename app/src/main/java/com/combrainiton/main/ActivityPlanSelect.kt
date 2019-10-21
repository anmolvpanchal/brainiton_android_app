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




class ActivityPlanSelect : AppCompatActivity(), PurchasesUpdatedListener {

    var enteredCode: String = ""
    var courseId: Int = 0
    lateinit var alertDialog: AlertDialog
    lateinit var viewGroup: ViewGroup
    lateinit var builder: AlertDialog.Builder
    var selectedPlan: Int = 0
    lateinit var billingClient: BillingClient

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

                if (billingClient.isReady) {

                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(Arrays.asList("android.test.purchased", "android.test.purchased"))
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
                    Toast.makeText(this@ActivityPlanSelect, "Billing client is not ready", Toast.LENGTH_SHORT).show()
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
                requestData["order"] = ""

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
                    Toast.makeText(this@ActivityPlanSelect, billingResult.toString(), Toast.LENGTH_SHORT).show()
                Log.i("plan", "Billing setup finished: " + billingResult.toString())
            }

            override fun onBillingServiceDisconnected() {
                Toast.makeText(this@ActivityPlanSelect, "Billing service disconnected", Toast.LENGTH_SHORT).show()
                Log.i("plan", "Billing service disconnected")
            }

        })
    }

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        if (billingResult!!.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            Log.i("plan", "purchase successfull")
            for (purchase in purchases) {
                handlePurchase(purchase)
                //allowMultiplePurchases(purchase)
            }

            Toast.makeText(this@ActivityPlanSelect, "Purchased", Toast.LENGTH_LONG).show()
        } else if (billingResult!!.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.i("plan", "already purchased")
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

        if (purchase != null) {

        } else {

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
                        Log.i("plan", "subscribed")
                    } else {
                        Toast.makeText(this@ActivityPlanSelect, "added", Toast.LENGTH_SHORT).show()
                    }
                }

        //removing from unsubscribed topic of firebase
        FirebaseMessaging.getInstance().unsubscribeFromTopic("general")
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.i("plan", "unsubscribed")
                    } else {
                        Toast.makeText(this@ActivityPlanSelect, "removed", Toast.LENGTH_SHORT).show()
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


}

