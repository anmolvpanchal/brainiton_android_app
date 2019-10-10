package com.combrainiton.main

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_select)
        cardone_bottomtext_planselect.setText(Html.fromHtml("<b>₹ 299 </b><br> ₹99/mon")).toString()
        cardtwo_bottomtext_planselect.setText(Html.fromHtml("<b>₹ 499 </b><br> ₹39/mon")).toString()

        Log.i("plan",intent.getIntExtra("course_id",0).toString())

        val requestData = HashMap<String, String>()
        requestData["course"] = courseId.toString() //add quiz id to request data
        requestData["code"] = enteredCode //add question id to request data

        firstcard_plan_select.setOnClickListener {

            firstcard_plan_select_layout.background = resources.getDrawable(R.drawable.planselect_cardselect_background)
            secondcard_plan_select_layout.setBackgroundColor(resources.getColor(R.color.selectplan_background))
        }

        secondcard_plan_select.setOnClickListener {
            secondcard_plan_select_layout.background = resources.getDrawable(R.drawable.planselect_cardselect_background)
            firstcard_plan_select_layout.setBackgroundColor(resources.getColor(R.color.selectplan_background))
        }

        continue_button_plan_select.setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }

        nextbutton_plan_select.setOnClickListener {
            if (Validation()){
                enterSubscriptionCode(requestData)
            }
        }
    }

    fun Validation(): Boolean {
        if (enter_subscription_code_eittext.text.isEmpty()){
            enter_subscription_code_eittext.error = "Please enter code"
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
                            val internalMessage = jsonObject.getString("message");
                            val errorCode = jsonObject.getString("status");
                            val toast : Toast  = Toast.makeText(this@ActivityPlanSelect,internalMessage, Toast.LENGTH_LONG);  // to show toast in center
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show()
                            Log.e("! Responce ", "some message " + internalMessage + errorCode)

                        } catch (e: JSONException) {
                            e.printStackTrace();

                        }
                    }

                }

                try {

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)
                    val message = rootObj.getString("message")
                    Toast.makeText(this@ActivityPlanSelect, message, Toast.LENGTH_SHORT).show();
                    Log.e(" Responce ", "some message " + rootObj)


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

