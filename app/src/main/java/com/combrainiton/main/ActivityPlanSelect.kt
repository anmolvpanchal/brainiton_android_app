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
import com.combrainiton.R
import com.combrainiton.subscription.ServiceGenerator
import com.combrainiton.subscription.SubscriptionInterface
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppSharedPreference
import kotlinx.android.synthetic.main.activity_plan_select.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response


class ActivityPlanSelect : AppCompatActivity() {

    var enteredCode: String = ""
    var courseId: Int = 0
    lateinit var alertDialog: AlertDialog
    lateinit var viewGroup: ViewGroup
    lateinit var builder: AlertDialog.Builder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_select)
        cardone_bottomtext_planselect.setText(Html.fromHtml("<b>₹ 299 </b><br> ₹99/mon")).toString()
        cardtwo_bottomtext_planselect.setText(Html.fromHtml("<b>₹ 499 </b><br> ₹39/mon")).toString()

        Log.i("plan",intent.getIntExtra("course_id",0).toString())

        firstcard_plan_select.setOnClickListener {

            firstcard_plan_select_layout.background = resources.getDrawable(R.drawable.planselect_cardselect_background)
            secondcard_plan_select_layout.setBackgroundColor(resources.getColor(R.color.selectplan_background))
        }

        secondcard_plan_select.setOnClickListener {
            secondcard_plan_select_layout.background = resources.getDrawable(R.drawable.planselect_cardselect_background)
            firstcard_plan_select_layout.setBackgroundColor(resources.getColor(R.color.selectplan_background))
        }

        continue_button_plan_select.setOnClickListener {
            startActivity(Intent(this, PayTmGateway::class.java))
        }

        nextbutton_plan_select.setOnClickListener {
            if (Validation()){

                courseId = intent.getIntExtra("course_id",0)
                enteredCode = enter_subscription_code_eittext.text.toString()
                val requestData = HashMap<String, String>()
                requestData["course"] = courseId.toString() //add quiz id to request data
                requestData["code"] = enteredCode //add question id to request data

                Log.e("hashmap","course id "+ requestData["course"] + "entered code " + requestData["code"])
                enterSubscriptionCode(requestData)
            }
        }
    }

    fun alertDialogForCorrectCode(){
        viewGroup = findViewById(android.R.id.content) //This is an in-built id and not made by programmer
        val dialogView = LayoutInflater.from(this).inflate(R.layout.correct_subscription_code_popup,viewGroup,false)
        builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        alertDialog= builder.create()

        //This won't allow dialog to dismiss if touched outside it's area
        alertDialog.setCanceledOnTouchOutside(false)

        //Transparent background for alert dialog
        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()

        val  button : Button? = dialogView.findViewById(R.id.correct_subscription_btn)

        button?.setOnClickListener {
            startActivity(Intent(this@ActivityPlanSelect,ActivityNavCompete::class.java))
            alertDialog.dismiss()
        }

    }

    fun alertDialogForWrongCode(){
        viewGroup = findViewById(android.R.id.content) //This is an in-built id and not made by programmer
        val dialogView = LayoutInflater.from(this).inflate(R.layout.incorrect_subscription_code_popup,viewGroup,false)
        builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        alertDialog= builder.create()

        //This won't allow dialog to dismiss if touched outside it's area
        alertDialog.setCanceledOnTouchOutside(false)

        //Transparent background for alert dialog
        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()

        val  button : Button? = dialogView.findViewById(R.id.incorrect_subscription_btn)

        button?.setOnClickListener {
            alertDialog.dismiss()
        }

    }



    fun Validation(): Boolean {
        if (enter_subscription_code_eittext.text.isEmpty()){
            enter_subscription_code_eittext.error = "Please enter code"
            return false
        }else if (enter_subscription_code_eittext.text.length > 6){
            enter_subscription_code_eittext.error = "Please enter valid code"
            return false
        }else{
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

                            if (errorCode.equals("400")){
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

                    if (errorCode.equals("201")){
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

