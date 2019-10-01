package com.combrainiton.main

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.combrainiton.R
import com.combrainiton.adaptors.MySubscribtionAdapter
import com.combrainiton.subscription.ServiceGenerator
import com.combrainiton.subscription.SubscribedCourse_API
import com.combrainiton.subscription.SubscriptionInterface
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppSharedPreference
import kotlinx.android.synthetic.main.activity_plan_select.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


class ActivityPlanSelect : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_select)
        cardone_bottomtext_planselect.setText(Html.fromHtml("<b>₹ 299 </b><br> ₹99/mon")).toString()
        cardtwo_bottomtext_planselect.setText(Html.fromHtml("<b>₹ 499 </b><br> ₹39/mon")).toString()

        enterSubscriptionCode("2","11cTp1")

        firstcard_plan_select.setOnClickListener {

            firstcard_plan_select_layout.background = resources.getDrawable(R.drawable.planselect_cardselect_background)
            secondcard_plan_select_layout.setBackgroundColor(resources.getColor(R.color.selectplan_background))
        }

        secondcard_plan_select.setOnClickListener {
            secondcard_plan_select_layout.background = resources.getDrawable(R.drawable.planselect_cardselect_background)
            firstcard_plan_select_layout.setBackgroundColor(resources.getColor(R.color.selectplan_background))
        }

        continue_button_plan_select.setOnClickListener {
            startActivity(Intent(this,PaymentActivity::class.java))
        }
    }

    fun enterSubscriptionCode(course : String , code : String){
//create api client first
        val apiToken: String = AppSharedPreference(this@ActivityPlanSelect).getString("apiToken")


        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)
        val call = apiClient.subscribeWithCode(course, code)

        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(this@ActivityPlanSelect, "Error", resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(this@ActivityPlanSelect, "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("!response.isSuccessful", "body \n"
                            + response.body().toString()
                            + " code ${response.code()}")
                    return;
                }

                try {

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)
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
