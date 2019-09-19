package com.combrainiton.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import com.combrainiton.R
import kotlinx.android.synthetic.main.activity_plan_select.*

class ActivityPlanSelect : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_select)
        cardone_bottomtext_planselect.setText(Html.fromHtml("<b>₹ 299 </b><br> ₹99/mon")).toString()
        cardtwo_bottomtext_planselect.setText(Html.fromHtml("<b>₹ 499 </b><br> ₹39/mon")).toString()

        continue_button_plan_select.setOnClickListener {
            startActivity(Intent(this,PaymentActivity::class.java))
        }
    }
}
